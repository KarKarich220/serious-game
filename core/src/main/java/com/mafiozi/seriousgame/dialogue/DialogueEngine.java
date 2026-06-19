package com.mafiozi.seriousgame.dialogue;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class DialogueEngine {
    private final DialogueManager manager;
    private final Map<String, DialogueAction> actions = new HashMap<>();
    private final DialogueCommandExecutor commandExecutor;
    private final DialogueTyper typer;

    private DialogueSession session;
    private boolean active = false;

    public DialogueEngine(DialogueManager manager) {
        this.manager = manager;
        this.typer = new DialogueTyper();
        this.commandExecutor = new DialogueCommandExecutor(actions, typer);
        this.typer.setCommandExecutor(commandExecutor);
    }

    public void registerAction(String name, DialogueAction action) {
        actions.put(name, action);
    }

    public void setTypingSound(com.badlogic.gdx.audio.Sound sound) {
        typer.setTypingSound(sound);
    }

    public void startDialogue(String dialogueId) {
        Dialogue dialogue = manager.getDialogue(dialogueId);
        if (dialogue == null) {
            Gdx.app.error("DialogueEngine", "Dialogue not found: " + dialogueId);
            return;
        }
        session = new DialogueSession(dialogue);
        active = true;
        
        String text = session.getText();
        Array<DialogueToken> tokens = DialogueParser.parse(text);
        
        commandExecutor.resetEffects();
        
        typer.start(text, tokens, 0.025f);
    }

    public void update(float delta) {
        if (!active || session == null) return;
        typer.update(delta);
    }

    public void advance() {
        if (!active || session == null) return;

        if (!typer.isTypingComplete()) {
            typer.skipTyping();
            return;
        }

        if (session.hasChoices()) return;

        if (session.hasNext()) {
            session.advanceByNext();
            String text = session.getText();
            Array<DialogueToken> tokens = DialogueParser.parse(text);
            commandExecutor.resetEffects();
            typer.start(text, tokens, 0.025f);
        } else if (session.isFinished()) {
            closeDialogue();
        }
    }

    public void chooseOption(int index) {
        if (!active || session == null) return;
        if (!session.hasChoices()) return;
        if (index < 0 || index >= session.getChoices().size) return;

        session.choose(index);
        if (session.isFinished()) {
            closeDialogue();
        } else {
            String text = session.getText();
            Array<DialogueToken> tokens = DialogueParser.parse(text);
            commandExecutor.resetEffects();
            typer.start(text, tokens, 0.025f);
        }
    }

    public void closeDialogue() {
        active = false;
        session = null;
        typer.reset();
        commandExecutor.resetEffects();
    }

    public boolean isActive() { return active; }
    public String getDisplayedText() { return typer.getDisplayedText(); }
    public boolean isTypingComplete() { return typer.isTypingComplete(); }
    public Array<DialogueChoice> getChoices() {
        return session != null ? session.getChoices() : null;
    }
    public float getShakeIntensity() { return commandExecutor.getShakeIntensity(); }
    public String getCurrentColor() { return commandExecutor.getCurrentColor(); }
    public boolean isColorActive() { return commandExecutor.isColorActive(); }
    public boolean isShakeActive() { return commandExecutor.isShakeActive(); }

    public DialogueManager getDialogueManager() { return manager; }
    public DialogueSession getSession() { return session; }

    public interface DialogueAction {
        void execute(String param);
    }
}