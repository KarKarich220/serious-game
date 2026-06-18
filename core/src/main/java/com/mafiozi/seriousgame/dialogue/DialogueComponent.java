package com.mafiozi.seriousgame.dialogue;

import com.badlogic.gdx.audio.Sound;

public class DialogueComponent {
    private String dialogueId;
    private float interactRange = 24f;
    private Sound typingSound;  // звук печати для этого NPC

    public DialogueComponent(String dialogueId) {
        this.dialogueId = dialogueId;
        this.typingSound = null;  // будет установлен позже
    }
    
    public DialogueComponent(String dialogueId, Sound typingSound) {
        this.dialogueId = dialogueId;
        this.typingSound = typingSound;
    }

    public String getDialogueId() {
        return dialogueId;
    }

    public float getInteractRange() {
        return interactRange;
    }
    
    public void setInteractRange(float range) {
        this.interactRange = range;
    }
    
    public Sound getTypingSound() {
        return typingSound;
    }
    
    public void setTypingSound(Sound sound) {
        this.typingSound = sound;
    }
}