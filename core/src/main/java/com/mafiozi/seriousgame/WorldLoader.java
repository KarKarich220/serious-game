package com.mafiozi.seriousgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.mafiozi.seriousgame.dialogue.DialogueComponent;
import com.mafiozi.seriousgame.dialogue.DialogueManager;
import com.mafiozi.seriousgame.dialogue.NPCData;
import com.mafiozi.seriousgame.entities.NPC;

public class WorldLoader {
    private final AssetLoader assetLoader;
    
    public WorldLoader(AssetLoader assetLoader) {
        this.assetLoader = assetLoader;
    }
    
    public LoadedWorld loadWorld(TiledMap map, DialogueManager dialogueManager) {
        MapLayer objectLayer = map.getLayers().get("objects");
        List<NPC> npcs = new ArrayList<>();
        float spawnX = 80f, spawnY = 80f;
        
        if (objectLayer != null) {
            for (MapObject object : objectLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    String type = object.getProperties().get("type", String.class);
                    
                    if ("npc".equals(type)) {
                        NPC npc = createNPC(object, rect, dialogueManager);
                        if (npc != null) npcs.add(npc);
                    } else if ("spawn".equals(type)) {
                        spawnX = rect.x;
                        spawnY = rect.y;
                    }
                }
            }
        }
        
        return new LoadedWorld(npcs, spawnX, spawnY);
    }
    
    private NPC createNPC(MapObject object, Rectangle rect, DialogueManager dialogueManager) {
        String id = object.getName();
        String npcId = object.getProperties().get("npc_id", String.class);
        if (npcId == null) npcId = id;
        
        NPCData data = dialogueManager.getNPCData(npcId);
        if (data == null) {
            data = createDefaultNPCData(npcId);
        }
        
        Texture npcTexture = assetLoader.getTexture(data.texture);
        TextureRegion npcSprite = new TextureRegion(npcTexture);
        
        DialogueComponent dc = new DialogueComponent(data.dialogueId);
        dc.setInteractRange(data.interactRange);
        
        if (data.typingSound != null) {
            Sound typingSound = assetLoader.get(data.typingSound, Sound.class);
            dc.setTypingSound(typingSound);
        }
        
        float width = rect.width * data.scale;
        float height = rect.height * data.scale;
        
        NPC npc = new NPC(rect.x + data.offsetX, rect.y + data.offsetY, 
                          width, height, id, npcSprite, dc);
        npc.setName(data.name);
        return npc;
    }
    
    private NPCData createDefaultNPCData(String id) {
        NPCData data = new NPCData();
        data.name = id;
        data.texture = AssetPaths.NPC_GUIDE;
        data.dialogueId = id;
        data.interactRange = 24f;
        data.scale = 1.0f;
        data.offsetX = 0f;
        data.offsetY = 0f;
        return data;
    }
}