package com.mafiozi.seriousgame.dialogue;

import java.util.HashMap;

public class DialogueManager {
    private final HashMap<String, Dialogue> dialogues = new HashMap<>();
    private final HashMap<String, NPCData> npcData = new HashMap<>();

    public Dialogue getDialogue(String id) {
        return dialogues.get(id);
    }
    
    public void addDialogue(String id, Dialogue dialogue) {
        dialogues.put(id, dialogue);
    }
    
    public NPCData getNPCData(String id) {
        return npcData.get(id);
    }
    
    public void addNPCData(String id, NPCData data) {
        npcData.put(id, data);
    }
    
    public boolean hasNPCData(String id) {
        return npcData.containsKey(id);
    }
}