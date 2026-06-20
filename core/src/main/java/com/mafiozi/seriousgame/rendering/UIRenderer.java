package com.mafiozi.seriousgame.rendering;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mafiozi.seriousgame.dialogue.DialogueEngine;
import com.mafiozi.seriousgame.dialogue.DialogueRenderer;

public class UIRenderer {
    private final DialogueRenderer dialogueRenderer;
    private final ShapeRenderer shapes;

    public UIRenderer(DialogueRenderer dialogueRenderer, ShapeRenderer shapes) {
        this.dialogueRenderer = dialogueRenderer;
        this.shapes = shapes;
    }

    // 🔳 BACKGROUND (ShapeRenderer)
    public void renderBackground(DialogueEngine engine, OrthographicCamera uiCamera) {
        if (!engine.isActive()) return;

        shapes.setProjectionMatrix(uiCamera.combined);
        dialogueRenderer.renderBackground(engine);
    }

    // 📝 TEXT (SpriteBatch)
    public void renderText(SpriteBatch batch, DialogueEngine engine) {
        if (!engine.isActive()) return;

        dialogueRenderer.renderText(batch, engine);
    }
}