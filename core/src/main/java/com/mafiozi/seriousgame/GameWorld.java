package com.mafiozi.seriousgame;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mafiozi.seriousgame.dialogue.Dialogue;
import com.mafiozi.seriousgame.dialogue.DialogueEngine;
import com.mafiozi.seriousgame.dialogue.DialogueLoader;
import com.mafiozi.seriousgame.dialogue.DialogueManager;
import com.mafiozi.seriousgame.dialogue.DialogueRenderer;
import com.mafiozi.seriousgame.dialogue.NPCData;
import com.mafiozi.seriousgame.dialogue.NPCLoader;
import com.mafiozi.seriousgame.entities.CollisionSystem;
import com.mafiozi.seriousgame.entities.EntityManager;
import com.mafiozi.seriousgame.entities.NPC;
import com.mafiozi.seriousgame.entities.Player;

public class GameWorld {
    private final TiledMap map;
    private final TiledMapTileLayer wallLayer;
    private final EntityManager entityManager;
    private final Player player;
    private final DialogueManager dialogueManager;
    private final DialogueEngine dialogueEngine;
    private final Music bgMusic;
    
    public GameWorld(AssetLoader assetLoader) {
        this.map = assetLoader.get(AssetPaths.MAP, TiledMap.class);
        this.wallLayer = (TiledMapTileLayer) map.getLayers().get("walls");
        
        this.dialogueManager = new DialogueManager();
        loadDialogues(assetLoader);
        loadNPCData(assetLoader);
        
        WorldLoader worldLoader = new WorldLoader(assetLoader);
        LoadedWorld loadedWorld = worldLoader.loadWorld(map, dialogueManager);
        
        this.entityManager = new EntityManager();
        
        for (NPC npc : loadedWorld.getNpcs()) {
            entityManager.add(npc);
        }
        
        CollisionSystem collisionSystem = new CollisionSystem(wallLayer, entityManager.getAllEntities());
        this.player = new Player(
            loadedWorld.getSpawnX(), 
            loadedWorld.getSpawnY(), 
            assetLoader.get(AssetPaths.PLAYER_SHEET, Texture.class),
            collisionSystem
        );
        entityManager.add(player);
        
        this.dialogueEngine = new DialogueEngine(dialogueManager, null);
        registerDialogueActions(assetLoader);
        
        this.bgMusic = assetLoader.get(AssetPaths.BACKGROUND_MUSIC, Music.class);
        this.bgMusic.setLooping(true);
        this.bgMusic.setVolume(0.4f);
    }
    
    public void setDialogueRenderer(DialogueRenderer renderer) {
        if (dialogueEngine != null) {
            dialogueEngine.setRenderer(renderer);
        }
    }
    
    private void loadDialogues(AssetLoader assetLoader) {
        FileHandle dialoguesDir = Gdx.files.internal("assets/dialogues");
        if (dialoguesDir.exists() && dialoguesDir.isDirectory()) {
            for (FileHandle entry : dialoguesDir.list()) {
                if (!entry.extension().equals("json")) continue;
                
                String fileName = entry.name();
                String id = entry.nameWithoutExtension();
                
                if (fileName.equals("npcs.json") || id.equals("npcs")) {
                    continue;
                }
                
                try {
                    Dialogue dialogue = DialogueLoader.loadDialogue(entry);
                    dialogueManager.addDialogue(id, dialogue);
                    Gdx.app.log("GameWorld", "Loaded dialogue: " + id);
                } catch (Exception e) {
                    Gdx.app.error("GameWorld", "Failed to load dialogue: " + fileName, e);
                }
            }
        }
    }
    
    private void loadNPCData(AssetLoader assetLoader) {
        FileHandle npcFile = Gdx.files.internal("assets/dialogues/npcs.json");
        if (!npcFile.exists()) {
            Gdx.app.log("GameWorld", "npcs.json not found!");
            return;
        }
        
        try {
            Map<String, NPCData> npcDataMap = NPCLoader.loadNPCs(npcFile);
            for (Map.Entry<String, NPCData> entry : npcDataMap.entrySet()) {
                dialogueManager.addNPCData(entry.getKey(), entry.getValue());
                Gdx.app.log("GameWorld", "Loaded NPC data: " + entry.getKey());
            }
        } catch (Exception e) {
            Gdx.app.error("GameWorld", "Failed to load NPC data", e);
        }
    }
    
    private void registerDialogueActions(AssetLoader assetLoader) {
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
                Music music = assetLoader.get(param, Music.class);
                if (music != null) {
                    music.setLooping(true);
                    music.play();
                    Gdx.app.log("DialogueAction", "Playing music: " + param);
                }
            } catch (Exception e) {
                Gdx.app.log("DialogueAction", "Music not found: " + param);
            }
        });
    }
    
    public void update(float delta) {
        entityManager.update(delta);
        dialogueEngine.update(delta);
    }
    
    public TiledMap getMap() { return map; }
    public TiledMapTileLayer getWallLayer() { return wallLayer; }
    public EntityManager getEntityManager() { return entityManager; }
    public Player getPlayer() { return player; }
    public DialogueManager getDialogueManager() { return dialogueManager; }
    public DialogueEngine getDialogueEngine() { return dialogueEngine; }
    public Music getBgMusic() { return bgMusic; }
    
    public void dispose() {
        bgMusic.dispose();
    }
}