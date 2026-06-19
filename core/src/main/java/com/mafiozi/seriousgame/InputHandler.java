package com.mafiozi.seriousgame;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.mafiozi.seriousgame.dialogue.Dialogue;
import com.mafiozi.seriousgame.dialogue.DialogueEngine;
import com.mafiozi.seriousgame.entities.Entity;
import com.mafiozi.seriousgame.entities.EntityManager;
import com.mafiozi.seriousgame.entities.NPC;
import com.mafiozi.seriousgame.entities.Player;

public class InputHandler {
    private final Player player;
    private final DialogueEngine dialogueEngine;
    private final EntityManager entityManager;
    private final KeyBindings keyBindings;
    
    public InputHandler(Player player, DialogueEngine dialogueEngine, 
                        EntityManager entityManager, KeyBindings keyBindings) {
        this.player = player;
        this.dialogueEngine = dialogueEngine;
        this.entityManager = entityManager;
        this.keyBindings = keyBindings;
    }
    
    public void handleInput() {
        if (dialogueEngine.isActive()) {
            handleDialogueInput();
        } else {
            handleGameInput();
        }
    }
    
    private void handleDialogueInput() {
        for (int i = 0; i < 9; i++) {
            int key = keyBindings.getDialogueOptionKey(i);
            if (Gdx.input.isKeyJustPressed(key)) {
                dialogueEngine.chooseOption(i);
                break;
            }
        }
        
        if (Gdx.input.isKeyJustPressed(keyBindings.getActionKey("dialogue_advance"))) {
            dialogueEngine.advance();
        }
    }
    
    private void handleGameInput() {
        float dx = 0, dy = 0;
        if (Gdx.input.isKeyPressed(keyBindings.getActionKey("move_up"))) dy += 1;
        if (Gdx.input.isKeyPressed(keyBindings.getActionKey("move_down"))) dy -= 1;
        if (Gdx.input.isKeyPressed(keyBindings.getActionKey("move_left"))) dx -= 1;
        if (Gdx.input.isKeyPressed(keyBindings.getActionKey("move_right"))) dx += 1;
        
        if (dx != 0 || dy != 0) {
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            
            dx /= length;
            dy /= length;
            
            float speed = GameConfig.PLAYER_SPEED;
            player.move(dx * speed * Gdx.graphics.getDeltaTime(),
                        dy * speed * Gdx.graphics.getDeltaTime());
        }
        
        if (Gdx.input.isKeyJustPressed(keyBindings.getActionKey("interact"))) {
            Entity nearby = entityManager.getNearby(player, GameConfig.INTERACT_RANGE);
            if (nearby instanceof NPC) {
                NPC npc = (NPC) nearby;
                String dialogueId = npc.getDialogue().getDialogueId();
                Dialogue dialogue = dialogueEngine.getDialogueManager().getDialogue(dialogueId);
                if (dialogue != null) {
                    Sound typingSound = npc.getDialogue().getTypingSound();
                    dialogueEngine.setTypingSound(typingSound);
                    dialogueEngine.startDialogue(dialogueId);
                }
            }
        }
    }
}