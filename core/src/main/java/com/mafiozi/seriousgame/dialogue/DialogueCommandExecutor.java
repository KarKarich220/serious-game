package com.mafiozi.seriousgame.dialogue;

import com.badlogic.gdx.Gdx;
import java.util.Map;

public class DialogueCommandExecutor {
    private final Map<String, DialogueEngine.DialogueAction> actions;
    private final DialogueTyper typer;

    private boolean shakeActive = false;
    private float shakeIntensity = 2f;
    private String currentColor = "#FFFFFF";
    private boolean colorActive = false;

    public DialogueCommandExecutor(Map<String, DialogueEngine.DialogueAction> actions,
                                   DialogueTyper typer) {
        this.actions = actions;
        this.typer = typer;
    }

    public void execute(DialogueToken token) {
        switch (token.type) {
            case PAUSE:
                typer.pause(token.floatParam);
                Gdx.app.log("DialogueCmd", "Pause: " + token.floatParam + "s");
                break;

            case SPEED:
                typer.setTypingSpeed(token.floatParam);
                Gdx.app.log("DialogueCmd", "Speed changed to: " + token.floatParam);
                break;

            case SHAKE_ON:
                shakeActive = true;
                Gdx.app.log("DialogueCmd", "Shake ON");
                break;

            case SHAKE_OFF:
                shakeActive = false;
                Gdx.app.log("DialogueCmd", "Shake OFF");
                break;

            case COLOR_ON:
                currentColor = token.value;
                colorActive = true;
                Gdx.app.log("DialogueCmd", "Color ON: " + currentColor);
                break;

            case COLOR_OFF:
                colorActive = false;
                currentColor = "#FFFFFF";
                Gdx.app.log("DialogueCmd", "Color OFF");
                break;

            case MUSIC:
                executeAction("music", token.value);
                break;

            case SOUND:
                executeAction("sound", token.value);
                break;

            case CALL:
                String[] parts = token.value.split(":", 2);
                String actionName = parts[0];
                String param = parts.length > 1 ? parts[1] : "";
                executeAction(actionName, param);
                break;

            default:
                Gdx.app.log("DialogueCmd", "Unknown command token: " + token.type);
                break;
        }
    }

    private void executeAction(String name, String param) {
        DialogueEngine.DialogueAction action = actions.get(name);
        if (action != null) {
            action.execute(param);
        } else {
            Gdx.app.log("DialogueCmd", "Action not registered: " + name);
        }
    }

    public boolean isShakeActive() { return shakeActive; }
    public float getShakeIntensity() { return shakeActive ? shakeIntensity : 0f; }
    public String getCurrentColor() { return colorActive ? currentColor : "#FFFFFF"; }
    public boolean isColorActive() { return colorActive; }

    public void resetEffects() {
        shakeActive = false;
        colorActive = false;
        currentColor = "#FFFFFF";
    }
}