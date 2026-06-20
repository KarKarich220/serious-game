package com.mafiozi.seriousgame.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mafiozi.seriousgame.dialogue.DialogueEngine;

public class DebugRenderer {
    private final BitmapFont font;

    public DebugRenderer(BitmapFont font) {
        this.font = font;
    }

    public void render(SpriteBatch batch, DialogueEngine dialogueEngine, float zoom, boolean vfxEnabled) {
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 700);
        font.draw(batch, "Dialogue active: " + dialogueEngine.isActive(), 10, 670);
        font.draw(batch, "Zoom: " + String.format("%.2f", zoom), 10, 610);
        font.draw(batch, "VFX: " + (vfxEnabled ? "ON" : "OFF"), 10, 580);
    }
}