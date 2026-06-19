package com.mafiozi.seriousgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mafiozi.seriousgame.dialogue.DialogueEngine;
import com.mafiozi.seriousgame.dialogue.DialogueRenderer;
import com.mafiozi.seriousgame.entities.EntityManager;
import com.mafiozi.seriousgame.entities.Player;

public class GameRenderer {
    private final SpriteBatch batch;
    private final ShapeRenderer shapes;
    private final OrthographicCamera camera;
    private final OrthographicCamera uiCamera;
    private final Viewport viewport;
    private final Viewport uiViewport;
    
    private final TiledMapRenderer mapRenderer;
    private final EntityManager entityManager;
    private final DialogueRenderer dialogueRenderer;
    private final BitmapFont font;
    private boolean showDebug;

    private float smoothCameraX;
    private float smoothCameraY;
    private float cameraSmoothness = 0.08f;

    private float targetZoom = 1f;
    private float currentZoom = 1f;
    private float zoomSmoothness = 0.05f;

    private float lookAheadX = 0f;
    private float lookAheadY = 0f;
    
    public GameRenderer(AssetLoader assetLoader, GameWorld gameWorld) {
        this.batch = new SpriteBatch();
        this.shapes = new ShapeRenderer();
        this.font = assetLoader.get(AssetPaths.FONT, BitmapFont.class);
        
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 320, 240);
        this.uiCamera = new OrthographicCamera();
        this.uiCamera.setToOrtho(false, 1280, 720);
        
        this.viewport = new FitViewport(320, 240, camera);
        this.uiViewport = new FitViewport(1280, 720, uiCamera);
        
        TiledMap map = gameWorld.getMap();
        this.mapRenderer = new OrthogonalTiledMapRenderer(map);
        this.entityManager = gameWorld.getEntityManager();
        
        this.dialogueRenderer = new DialogueRenderer(font, shapes, 100, 50, 1080, 350);
        
        gameWorld.setDialogueRenderer(dialogueRenderer);
        
        this.showDebug = false;
        this.smoothCameraX = 0f;
        this.smoothCameraY = 0f;
    }
    
    public void render(Player player, DialogueEngine dialogueEngine, float delta) {
        ScreenUtils.clear(0.05f, 0.05f, 0.08f, 1);
        
        float targetX = player.getX();
        float targetY = player.getY();
        
        float lookAheadFactor = 15f;
        if (player.getMoveX() != 0 || player.getMoveY() != 0) {
            lookAheadX = player.getMoveX() * lookAheadFactor;
            lookAheadY = player.getMoveY() * lookAheadFactor;
        } else {
            lookAheadX *= (1 - cameraSmoothness);
            lookAheadY *= (1 - cameraSmoothness);
            if (Math.abs(lookAheadX) < 0.1f) lookAheadX = 0f;
            if (Math.abs(lookAheadY) < 0.1f) lookAheadY = 0f;
        }
        
        targetX += lookAheadX;
        targetY += lookAheadY;
        
        smoothCameraX += (targetX - smoothCameraX) * cameraSmoothness;
        smoothCameraY += (targetY - smoothCameraY) * cameraSmoothness;
        
        camera.position.set(smoothCameraX, smoothCameraY, 0);
        camera.update();
        viewport.apply();
        
        if (dialogueEngine.isActive()) {
            targetZoom = 1.2f;
        } else {
            targetZoom = 1.0f;
        }
        
        currentZoom += (targetZoom - currentZoom) * zoomSmoothness;
        
        float targetWidth = 320 / currentZoom;
        float targetHeight = 240 / currentZoom;
        camera.viewportWidth = targetWidth;
        camera.viewportHeight = targetHeight;
        camera.update();
        
        mapRenderer.setView(camera);
        mapRenderer.render();
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        entityManager.draw(batch);
        batch.end();
        
        uiViewport.apply();
        uiCamera.update();
        shapes.setProjectionMatrix(uiCamera.combined);
        batch.setProjectionMatrix(uiCamera.combined);

        if (dialogueEngine.isActive()) {
            dialogueRenderer.renderBackground(dialogueEngine);
        }

        batch.begin();

        if (dialogueEngine.isActive()) {
            dialogueRenderer.renderText(batch, dialogueEngine);
        }

        if (showDebug) {
            renderDebug(dialogueEngine);
        }

        batch.end();
    }
    
    public void toggleDebug() {
        showDebug = !showDebug;
    }
    
    private void renderDebug(DialogueEngine dialogueEngine) {
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 700);
        font.draw(batch, "Dialogue active: " + dialogueEngine.isActive(), 10, 670);
        font.draw(batch, "Typing complete: " + dialogueEngine.isTypingComplete(), 10, 640);
        font.draw(batch, "Zoom: " + String.format("%.2f", currentZoom), 10, 610);
        font.draw(batch, "Cam: " + String.format("%.0f", camera.position.x) + ", " + String.format("%.0f", camera.position.y), 10, 580);
    }
    
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiViewport.update(width, height, true);
    }
    
    public void dispose() {
        batch.dispose();
        shapes.dispose();
        font.dispose();
    }
}