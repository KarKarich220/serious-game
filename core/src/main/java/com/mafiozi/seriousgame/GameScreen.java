package com.mafiozi.seriousgame;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mafiozi.seriousgame.dialogue.Dialogue;
import com.mafiozi.seriousgame.dialogue.DialogueComponent;
import com.mafiozi.seriousgame.dialogue.DialogueEngine;
import com.mafiozi.seriousgame.dialogue.DialogueLoader;
import com.mafiozi.seriousgame.dialogue.DialogueManager;
import com.mafiozi.seriousgame.dialogue.DialogueRenderer;
import com.mafiozi.seriousgame.dialogue.NPCData;
import com.mafiozi.seriousgame.dialogue.NPCLoader;
import com.mafiozi.seriousgame.entities.CollisionSystem;
import com.mafiozi.seriousgame.entities.Entity;
import com.mafiozi.seriousgame.entities.EntityManager;
import com.mafiozi.seriousgame.entities.NPC;
import com.mafiozi.seriousgame.entities.Player;

public class GameScreen implements Screen {
	
	private boolean showDebug = false;
	
    private final Main game;
    private SpriteBatch batch;
    private ShapeRenderer shapes;
    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;
    private Viewport viewport;
    private FitViewport uiViewport;

    private Player player;
    private TiledMap map;
    private TiledMapRenderer mapRenderer;
    private TiledMapTileLayer wallLayer;
    private EntityManager entityManager;
    private CollisionSystem collisionSystem;

    private DialogueManager dialogueManager;
    private DialogueEngine dialogueEngine;
    private DialogueRenderer dialogueRenderer;

    private BitmapFont font;
    private Music bgMusic;

    public GameScreen(Main game) {
        this.game = game;
        AssetManager am = game.getAssetManager();

        // --- Инициализация графики ---
        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        font = am.get("assets/fonts/font.fnt", BitmapFont.class);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 320, 240);
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, 1280, 720);
        uiCamera.update();

        viewport = new ExtendViewport(320, 240, camera);
        uiViewport = new FitViewport(1280, 720, uiCamera);

        // --- Загрузка карты ---
        map = am.get("assets/map.tmx", TiledMap.class);
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        wallLayer = (TiledMapTileLayer) map.getLayers().get("walls");

        // --- ИНИЦИАЛИЗИРУЕМ DialogueManager ПЕРВЫМ ---
        dialogueManager = new DialogueManager();

        // --- Загрузка диалогов из папки assets/dialogues ---
        FileHandle dialoguesDir = Gdx.files.internal("assets/dialogues");
        if (dialoguesDir.exists() && dialoguesDir.isDirectory()) {
            for (FileHandle entry : dialoguesDir.list()) {
                if (entry.extension().equals("json")) {
                    String fileName = entry.name();
                    String id = entry.nameWithoutExtension();
                    
                    // ПРОПУСКАЕМ npcs.json - ЭТО НЕ ДИАЛОГ!
                    if (fileName.equals("npcs.json") || id.equals("npcs")) {
                        Gdx.app.log("GameScreen", "Skipping NPC data file: " + fileName);
                        continue;
                    }
                    
                    try {
                        Dialogue dialogue = DialogueLoader.loadDialogue(entry);
                        dialogueManager.addDialogue(id, dialogue);
                        Gdx.app.log("GameScreen", "Loaded dialogue: " + id);
                    } catch (Exception e) {
                        Gdx.app.error("GameScreen", "Failed to load dialogue: " + fileName, e);
                    }
                }
            }
        }

        // --- Загрузка данных NPC из npcs.json ---
        FileHandle npcFile = Gdx.files.internal("assets/dialogues/npcs.json");
        if (npcFile.exists()) {
            try {
                Map<String, NPCData> npcDataMap = NPCLoader.loadNPCs(npcFile);
                for (Map.Entry<String, NPCData> entry : npcDataMap.entrySet()) {
                    dialogueManager.addNPCData(entry.getKey(), entry.getValue());
                    Gdx.app.log("GameScreen", "Loaded NPC data: " + entry.getKey());
                }
            } catch (Exception e) {
                Gdx.app.error("GameScreen", "Failed to load NPC data", e);
            }
        } else {
            Gdx.app.log("GameScreen", "npcs.json not found!");
        }

        // --- Сущности ---
        entityManager = new EntityManager();
        float spawnX = 80f, spawnY = 80f;

        // Чтение объектов с карты (NPC, спавн)
        MapLayer objectLayer = map.getLayers().get("objects");
        if (objectLayer != null) {
            for (MapObject object : objectLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    String id = object.getName();
                    String type = object.getProperties().get("type", String.class);
                    
                    // Получаем npcId из свойств объекта
                    String npcId = object.getProperties().get("npc_id", String.class);
                    if (npcId == null) npcId = id;

                    if ("npc".equals(type)) {
                        // Получаем данные из менеджера по npcId
                        NPCData data = dialogueManager.getNPCData(npcId);
                        if (data == null) {
                            Gdx.app.log("GameScreen", "NPC data not found for: " + npcId + ", using defaults");
                            data = new NPCData();
                            data.name = id;
                            data.texture = "assets/guide_npc.png";
                            data.dialogueId = id;
                            data.interactRange = 24f;
                            data.scale = 1.0f;
                            data.offsetX = 0f;
                            data.offsetY = 0f;
                        }
                        
                        // Загружаем текстуру
                        Texture npcTexture;
                        try {
                            npcTexture = am.get(data.texture, Texture.class);
                        } catch (Exception e) {
                            Gdx.app.error("GameScreen", "Failed to load texture: " + data.texture);
                            npcTexture = am.get("assets/player.png", Texture.class);
                        }
                        TextureRegion npcSprite = new TextureRegion(npcTexture, 0, 0,
                                npcTexture.getWidth(), npcTexture.getHeight());
                        
                        // Загружаем звук
                        Sound typingSound = null;
                        if (data.typingSound != null) {
                            try {
                                typingSound = am.get(data.typingSound, Sound.class);
                            } catch (Exception e) {
                                Gdx.app.log("GameScreen", "Sound not found: " + data.typingSound);
                            }
                        }
                        
                        // Создаём компонент диалога
                        DialogueComponent dc = new DialogueComponent(data.dialogueId);
                        if (typingSound != null) {
                            dc.setTypingSound(typingSound);
                        }
                        dc.setInteractRange(data.interactRange);
                        
                        // Создаём NPC с учётом масштаба и смещения
                        float width = rect.width * data.scale;
                        float height = rect.height * data.scale;
                        float x = rect.x + data.offsetX;
                        float y = rect.y + data.offsetY;
                        
                        NPC npc = new NPC(x, y, width, height, id, npcSprite, dc);
                        npc.setName(data.name);
                        
                        entityManager.add(npc);
                        Gdx.app.log("GameScreen", "Created NPC: " + data.name + " (id: " + id + ")");
                    } else if ("spawn".equals(type)) {
                        spawnX = rect.x;
                        spawnY = rect.y;
                    }
                }
            }
        }

        // --- Игрок ---
        Texture playerTexture = am.get("assets/player_sheet.png", Texture.class);
        collisionSystem = new CollisionSystem(wallLayer, entityManager.getAllEntities());
        player = new Player(spawnX, spawnY, playerTexture, collisionSystem);
        entityManager.add(player);

        // --- Создаём движок диалогов ---
        dialogueEngine = new DialogueEngine(dialogueManager);

        // --- Регистрируем действия ---
        dialogueEngine.registerAction("log", param -> {
            Gdx.app.log("DialogueAction", "Log: " + param);
        });

        dialogueEngine.registerAction("shake", param -> {
            Gdx.app.log("DialogueAction", "Shake: " + param);
        });

        dialogueEngine.registerAction("changeBackground", param -> {
            Gdx.app.log("DialogueAction", "Changing background to: " + param);
        });

        dialogueEngine.registerAction("debug", param -> {
            Gdx.app.log("DialogueAction", "Debug command: " + param);
        });

        dialogueEngine.registerAction("music", param -> {
            try {
                Music music = game.getAssetManager().get(param, Music.class);
                if (music != null) {
                    music.setLooping(true);
                    music.play();
                    Gdx.app.log("DialogueAction", "Playing music: " + param);
                }
            } catch (Exception e) {
                Gdx.app.log("DialogueAction", "Music not found: " + param);
            }
        });

        // Создаём рендерер диалогов
        dialogueRenderer = new DialogueRenderer(font, shapes, 100, 50, 1080, 350);

        // --- Музыка ---
        bgMusic = game.getAssetManager().get("assets/music/background_music.ogg", Music.class);
        bgMusic.setLooping(true);
        bgMusic.setVolume(0.4f);
        bgMusic.play();
    }

    @Override
    public void render(float delta) {
        // --- Обработка ввода ---
        handleInput();

        // --- Обновление мира ---
        entityManager.update(delta);
        dialogueEngine.update(delta);

        // --- Отрисовка ---
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

        // --- UI (диалоги) ---
        uiViewport.apply();
        shapes.setProjectionMatrix(uiCamera.combined);
        batch.setProjectionMatrix(uiCamera.combined);

        if (dialogueEngine.isActive()) {
            dialogueRenderer.render(batch, dialogueEngine);
        }
        
        // Отладка
        if (Gdx.input.isKeyJustPressed(Keys.F3)) {
            showDebug = !showDebug;
        }

        if (showDebug) {
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
    }

    private void handleInput() {
        if (dialogueEngine.isActive()) {
            // Выбор вариантов (цифры 1-9)
            for (int i = 1; i <= 9; i++) {
                if (Gdx.input.isKeyJustPressed(Keys.NUM_1 + i - 1)) {
                    dialogueEngine.chooseOption(i - 1);
                    break;
                }
            }

            // Продолжить / пропустить (Z)
            if (Gdx.input.isKeyJustPressed(Keys.Z)) {
                dialogueEngine.advance();
            }
        } else {
            // --- Если диалог не активен, пробуем начать ---
            if (Gdx.input.isKeyJustPressed(Keys.Z)) {
                Entity nearby = entityManager.getNearby(player, 24f);
                if (nearby instanceof NPC) {
                    NPC npc = (NPC) nearby;
                    String dialogueId = npc.getDialogue().getDialogueId();
                    Dialogue dialogue = dialogueManager.getDialogue(dialogueId);
                    if (dialogue != null) {
                        Sound typingSound = npc.getDialogue().getTypingSound();
                        dialogueEngine.setTypingSound(typingSound);
                        dialogueEngine.startDialogue(dialogueId);
                    } else {
                        Gdx.app.log("GameScreen", "Dialogue not found: " + dialogueId);
                    }
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiViewport.update(width, height, true);
    }

    @Override
    public void show() {
        if (bgMusic != null && !bgMusic.isPlaying()) {
            bgMusic.play();
        }
    }

    @Override
    public void hide() {
        if (bgMusic != null && bgMusic.isPlaying()) {
            bgMusic.pause();
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        batch.dispose();
        shapes.dispose();
        font.dispose();
    }
}