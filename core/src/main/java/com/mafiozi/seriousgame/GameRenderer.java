package com.mafiozi.seriousgame;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mafiozi.seriousgame.dialogue.DialogueEngine;
import com.mafiozi.seriousgame.dialogue.DialogueRenderer;
import com.mafiozi.seriousgame.entities.EntityManager;
import com.mafiozi.seriousgame.entities.Player;
import com.mafiozi.seriousgame.rendering.DebugRenderer;
import com.mafiozi.seriousgame.rendering.EffectsController;
import com.mafiozi.seriousgame.rendering.OverlayRenderer;
import com.mafiozi.seriousgame.rendering.PostProcessor;
import com.mafiozi.seriousgame.rendering.UIRenderer;
import com.mafiozi.seriousgame.rendering.WorldRenderer;

public class GameRenderer {
    private static final int GAME_WIDTH = 320;
    private static final int GAME_HEIGHT = 240;

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
    private final float cameraSmoothness = 0.08f;

    private float targetZoom = 1f;
    private float currentZoom = 1f;
    private final float zoomSmoothness = 0.05f;

    private float lookAheadX = 0f;
    private float lookAheadY = 0f;
    
    private boolean vfxEnabled = true;
    
    private WorldRenderer worldRenderer;
    private UIRenderer uiRenderer;
    private DebugRenderer debugRenderer;
    private PostProcessor postProcessor;
    private EffectsController effects;
    private OverlayRenderer overlayRenderer;

    public GameRenderer(AssetLoader assetLoader, GameWorld gameWorld) {
        this.batch = new SpriteBatch();
        this.shapes = new ShapeRenderer();
        this.font = assetLoader.get(AssetPaths.FONT, BitmapFont.class);

        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        this.uiCamera = new OrthographicCamera();
        this.uiCamera.setToOrtho(false, 1280, 720);

        this.viewport = new FitViewport(GAME_WIDTH, GAME_HEIGHT, camera);
        this.uiViewport = new FitViewport(1280, 720, uiCamera);

        TiledMap map = gameWorld.getMap();
        this.mapRenderer = new OrthogonalTiledMapRenderer(map);
        this.entityManager = gameWorld.getEntityManager();

        this.dialogueRenderer = new DialogueRenderer(font, shapes, 100, 50, 1080, 350);
        gameWorld.setDialogueRenderer(dialogueRenderer);
        
        this.effects = new EffectsController();
        this.postProcessor = new PostProcessor();
        this.worldRenderer = new WorldRenderer(map, entityManager);
        this.uiRenderer = new UIRenderer(dialogueRenderer, shapes);
        this.debugRenderer = new DebugRenderer(font);
        this.overlayRenderer = new OverlayRenderer(batch, shapes);

        this.showDebug = false;
        this.smoothCameraX = 0f;
        this.smoothCameraY = 0f;
        
    }

    public void render(Player player, DialogueEngine dialogueEngine, float delta) {

        effects.update(delta);
        postProcessor.update(effects);

        updateCamera(player, effects);

     // ===== WORLD =====
        postProcessor.begin(vfxEnabled);

        worldRenderer.render(camera);

        postProcessor.end(vfxEnabled);

        // ===== UI =====
        uiViewport.apply();
        uiCamera.update();

        batch.setProjectionMatrix(uiCamera.combined);
        shapes.setProjectionMatrix(uiCamera.combined);
		// 🔥 OVERLAY СЮДА
        overlayRenderer.render(effects, uiCamera);

        // dialogue background
        uiRenderer.renderBackground(dialogueEngine, uiCamera);

        // text
        batch.begin();
        uiRenderer.renderText(batch, dialogueEngine);
        debugRenderer.render(batch, dialogueEngine, currentZoom, vfxEnabled);
        batch.end();
    }
    
    private void updateCamera(Player player, EffectsController effects) {
        float targetX = player.getX();
        float targetY = player.getY();

        float lookAheadFactor = 15f;

        if (player.getMoveX() != 0 || player.getMoveY() != 0) {
            lookAheadX = player.getMoveX() * lookAheadFactor;
            lookAheadY = player.getMoveY() * lookAheadFactor;
        } else {
            lookAheadX *= (1 - cameraSmoothness);
            lookAheadY *= (1 - cameraSmoothness);
        }

        targetX += lookAheadX;
        targetY += lookAheadY;

        smoothCameraX += (targetX - smoothCameraX) * cameraSmoothness;
        smoothCameraY += (targetY - smoothCameraY) * cameraSmoothness;

        targetZoom = player.isMoving() ? 1.1f : 1f;
        currentZoom += (targetZoom - currentZoom) * zoomSmoothness;

        camera.position.set(smoothCameraX, smoothCameraY, 0);

        // 🎯 CAMERA SHAKE
        float shake = effects.getShakeIntensity();
        if (shake > 0) {
            camera.position.x += (Math.random() - 0.5f) * shake;
            camera.position.y += (Math.random() - 0.5f) * shake;
        }

        camera.viewportWidth = GAME_WIDTH / currentZoom;
        camera.viewportHeight = GAME_HEIGHT / currentZoom;

        camera.update();
    }

    public void toggleDebug() {
        showDebug = !showDebug;
    }

    public void toggleFilter() {
        vfxEnabled = !vfxEnabled;
    }

    public void resize(int width, int height) {
        viewport.update(width, height, false);
        uiViewport.update(width, height, true);
    }

    public void dispose() {
        batch.dispose();
        shapes.dispose();
        font.dispose();
    }
}