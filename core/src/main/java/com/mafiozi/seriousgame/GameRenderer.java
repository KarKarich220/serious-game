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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
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
    
    public GameRenderer(AssetLoader assetLoader, TiledMap map, 
                        EntityManager entityManager, DialogueEngine dialogueEngine) {
        this.batch = new SpriteBatch();
        this.shapes = new ShapeRenderer();
        this.font = assetLoader.get(AssetPaths.FONT, BitmapFont.class);
        
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 320, 240);
        this.uiCamera = new OrthographicCamera();
        this.uiCamera.setToOrtho(false, 1280, 720);
        
        this.viewport = new ExtendViewport(320, 240, camera);
        this.uiViewport = new FitViewport(1280, 720, uiCamera);
        
        this.mapRenderer = new OrthogonalTiledMapRenderer(map);
        this.entityManager = entityManager;
        this.dialogueRenderer = new DialogueRenderer(font, shapes, 100, 50, 1080, 350);
        this.showDebug = false;
    }
    
    public void render(Player player, DialogueEngine dialogueEngine, float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();
        viewport.apply();
        
        mapRenderer.setView(camera);
        mapRenderer.render();
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        entityManager.draw(batch);
        batch.end();
        
        uiViewport.apply();
        shapes.setProjectionMatrix(uiCamera.combined);
        batch.setProjectionMatrix(uiCamera.combined);
        
        if (dialogueEngine.isActive()) {
            dialogueRenderer.render(batch, dialogueEngine);
        }
        
        if (showDebug) {
            renderDebug(dialogueEngine);
        }
    }
    
    public void toggleDebug() {
    	showDebug = !showDebug;
    }
    
    private void renderDebug(DialogueEngine dialogueEngine) {
    	batch.begin();
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 700);
        font.draw(batch, "Dialogue active: " + dialogueEngine.isActive(), 10, 670);
        font.draw(batch, "Typing complete: " + dialogueEngine.isTypingComplete(), 10, 640);
        font.draw(batch, "Shake: " + dialogueEngine.getShakeIntensity(), 10, 610);
        if (dialogueEngine.isActive()) {
            font.draw(batch, "Text length: " + dialogueEngine.getDisplayedText().length(), 10, 580);
        }
        batch.end();
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