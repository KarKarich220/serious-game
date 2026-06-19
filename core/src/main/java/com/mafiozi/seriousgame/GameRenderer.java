package com.mafiozi.seriousgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.VignettingEffect; // Пример готового эффекта из либы
// import com.crashinvaders.vfx.effects.FilmGrainEffect; // Если захочешь зернистость
import com.mafiozi.seriousgame.dialogue.DialogueEngine;
import com.mafiozi.seriousgame.dialogue.DialogueRenderer;
import com.mafiozi.seriousgame.entities.EntityManager;
import com.mafiozi.seriousgame.entities.Player;

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

    // Твоя плавная камера
    private float smoothCameraX;
    private float smoothCameraY;
    private final float cameraSmoothness = 0.08f;

    private float targetZoom = 1f;
    private float currentZoom = 1f;
    private final float zoomSmoothness = 0.05f;

    private float lookAheadX = 0f;
    private float lookAheadY = 0f;

    // Менеджер эффектов из новой библиотеки
    private final VfxManager vfxManager;
    private boolean vfxEnabled = true;

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

        this.showDebug = false;
        this.smoothCameraX = 0f;
        this.smoothCameraY = 0f;

        // Инициализируем VFX либу
        // gdx-vfx сама подберет правильный внутренний формат пикселей
        this.vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        
        // ДЛЯ ТЕСТА: Добавим виньетку из стандартного набора либы.
        // Если у тебя есть свой кастомный FilterManager со старым шейдером, 
        // ниже я напишу, как его сюда встроить.
        VignettingEffect vignette = new VignettingEffect(false);
        vignette.setIntensity(0.5f);
        vfxManager.addEffect(vignette);
    }

    public void render(Player player, DialogueEngine dialogueEngine, float delta) {
        // --- 1. МАТЕМАТИКА КАМЕРЫ (Чистый рабочий float без округлений) ---
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

        if (dialogueEngine.isActive()) {
            targetZoom = 1.2f;
        } else {
            targetZoom = 1.0f;
        }
        currentZoom += (targetZoom - currentZoom) * zoomSmoothness;

        // Обновляем камеру
        camera.position.set(smoothCameraX, smoothCameraY, 0);
        camera.viewportWidth = GAME_WIDTH / currentZoom;
        camera.viewportHeight = GAME_HEIGHT / currentZoom;
        camera.update();

        // --- 2. РЕНДЕРИНГ ИГРЫ ---
        
        if (vfxEnabled) {
            // Либа перехватывает весь рендер в свой внутренний буфер
            vfxManager.cleanUpBuffers();
            vfxManager.beginInputCapture();
        }

        // Очищаем экран внутри захвата буфера
        ScreenUtils.clear(0.05f, 0.05f, 0.08f, 1f);
        viewport.apply();

        // Рендерим карту и сущностей
        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        entityManager.draw(batch);
        batch.end();

        if (vfxEnabled) {
            // Заканчиваем захват сцены
            vfxManager.endInputCapture();
            
            // Применяем эффекты и выводим готовый буфер прямо на экран!
            // Она автоматически учтет размеры окна и FitViewport.
            vfxManager.applyEffects();
            vfxManager.renderToScreen();
        }

        // --- 3. РЕНДЕР ИНТЕРФЕЙСА (Поверх эффектов, чтобы текст не мылился) ---
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

    public void toggleFilter() {
        vfxEnabled = !vfxEnabled;
    }

    private void renderDebug(DialogueEngine dialogueEngine) {
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 700);
        font.draw(batch, "Dialogue active: " + dialogueEngine.isActive(), 10, 670);
        font.draw(batch, "Zoom: " + String.format("%.2f", currentZoom), 10, 610);
        font.draw(batch, "VFX: " + (vfxEnabled ? "ON" : "OFF"), 10, 580);
    }

    public void resize(int width, int height) {
        viewport.update(width, height, false);
        uiViewport.update(width, height, true);
        // Либа сама знает, как правильно изменить размер своих буферов!
        vfxManager.resize(width, height);
    }

    public void dispose() {
        batch.dispose();
        shapes.dispose();
        font.dispose();
        vfxManager.dispose();
    }
}