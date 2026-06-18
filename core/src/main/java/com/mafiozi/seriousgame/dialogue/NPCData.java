package com.mafiozi.seriousgame.dialogue;

public class NPCData {
    public String name;
    public String texture;
    public String dialogueId;
    public String typingSound;
    public float interactRange = 24f;
    public float scale = 1.0f;
    public float offsetX = 0f;
    public float offsetY = 0f;
    
    public NPCData() {
        // Конструктор по умолчанию для JSON
    }
    
    public NPCData(String name, String texture, String dialogueId) {
        this.name = name;
        this.texture = texture;
        this.dialogueId = dialogueId;
    }
}