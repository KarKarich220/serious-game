package com.mafiozi.seriousgame.dialogue;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.util.HashMap;
import java.util.Map;

public class NPCLoader {
    
    public static Map<String, NPCData> loadNPCs(FileHandle jsonFile) {
        Map<String, NPCData> npcMap = new HashMap<>();
        
        if (!jsonFile.exists()) {
            return npcMap;
        }
        
        JsonReader reader = new JsonReader();
        JsonValue root = reader.parse(jsonFile);
        JsonValue npcsObj = root.get("npcs");
        
        if (npcsObj == null) return npcMap;
        
        for (JsonValue entry : npcsObj.iterator()) {
            String id = entry.name();
            NPCData data = new NPCData();
            
            data.name = entry.getString("name", id);
            data.texture = entry.getString("texture", "assets/player_sheet.png");
            data.dialogueId = entry.getString("dialogueId", id);
            data.typingSound = entry.getString("typingSound", null);
            data.interactRange = entry.getFloat("interactRange", 24f);
            data.scale = entry.getFloat("scale", 1.0f);
            data.offsetX = entry.getFloat("offsetX", 0f);
            data.offsetY = entry.getFloat("offsetY", 0f);
            
            npcMap.put(id, data);
        }
        
        return npcMap;
    }
}