package com.mafiozi.seriousgame.dialogue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class DialogueRenderer {
    private BitmapFont font;
    private ShapeRenderer shapes;
    private GlyphLayout layout;
    private float windowX, windowY, windowWidth, windowHeight;
    private float shakeOffsetX, shakeOffsetY;
    
    private float alpha = 0f;
    private float targetAlpha = 0f;
    private float alphaSmoothness = 0.08f;
    private float scale = 0.8f;
    private float targetScale = 0.8f;
    private float scaleSmoothness = 0.06f;
    
    private boolean wasActive = false;
    
    public DialogueRenderer(BitmapFont font, ShapeRenderer shapes,
                            float x, float y, float width, float height) {
        this.font = font;
        this.shapes = shapes;
        this.layout = new GlyphLayout();
        this.windowX = x;
        this.windowY = y;
        this.windowWidth = width;
        this.windowHeight = height;
    }
    
    public void renderBackground(DialogueEngine engine) {
        boolean isActive = engine.isActive();
        boolean isNewDialogue = isActive && !wasActive;
        if (isNewDialogue) {
            alpha = 0f;
            targetAlpha = 1f;
            scale = 0.8f;
            targetScale = 1f;
        }
        wasActive = isActive;
        
        if (!isActive) {
            targetAlpha = 0f;
            targetScale = 0.8f;
            updateSmoothValues();
            return;
        }
        
        targetAlpha = 1f;
        targetScale = 1f;
        updateSmoothValues();
        
        if (alpha < 0.01f) return;
        
        float drawX = windowX - (windowWidth * (scale - 1) / 2);
        float drawY = windowY - (windowHeight * (scale - 1) / 2);
        float drawWidth = windowWidth * scale;
        float drawHeight = windowHeight * scale;
        
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0, 0, 0, 0.85f * alpha);
        shapes.rect(drawX, drawY, drawWidth, drawHeight);
        shapes.end();
        
        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(1, 1, 1, 0.3f * alpha);
        shapes.rect(drawX, drawY, drawWidth, drawHeight);
        shapes.end();
    }
    
    public void renderText(SpriteBatch batch, DialogueEngine engine) {
        boolean isActive = engine.isActive();
        if (!isActive) return;
        if (alpha < 0.01f) return;
        
        Color oldColor = batch.getColor().cpy();
        
        float drawX = windowX - (windowWidth * (scale - 1) / 2);
        float drawY = windowY - (windowHeight * (scale - 1) / 2);
        float drawWidth = windowWidth * scale;
        float drawHeight = windowHeight * scale;
        
        String text = engine.getDisplayedText();
        if (text != null && !text.isEmpty()) {
            float shake = engine.getShakeIntensity();
            if (shake > 0) {
                shakeOffsetX = (float) (Math.random() - 0.5) * shake * 2;
                shakeOffsetY = (float) (Math.random() - 0.5) * shake * 2;
            } else {
                shakeOffsetX = 0;
                shakeOffsetY = 0;
            }
            
            String colorHex = engine.getCurrentColor();
            Color color = Color.valueOf(colorHex);
            
            batch.setColor(color.r, color.g, color.b, alpha);
            font.setColor(color);
            
            float textX = drawX + 20 + shakeOffsetX;
            float textY = drawY + drawHeight - 30 + shakeOffsetY;
            float maxWidth = drawWidth - 40;
            
            drawWrappedText(batch, text, textX, textY, maxWidth);
            
            batch.setColor(Color.WHITE);
            font.setColor(Color.WHITE);
        }
        
        Array<DialogueChoice> choices = engine.getChoices();
        if (choices != null && choices.size > 0) {
            float yPos = drawY + 50;
            for (int i = 0; i < choices.size; i++) {
                String choiceText = (i + 1) + ": " + choices.get(i).text;
                float textYPos = yPos + (choices.size - 1 - i) * 30;
                
                if (i == 0) {
                    batch.setColor(1, 0.8f, 0.2f, alpha);
                } else {
                    batch.setColor(1, 1, 1, alpha);
                }
                
                font.draw(batch, choiceText, drawX + 30, textYPos);
            }
            batch.setColor(Color.WHITE);
        }
        
        batch.setColor(oldColor);
    }
    
    private void updateSmoothValues() {
        alpha += (targetAlpha - alpha) * alphaSmoothness;
        scale += (targetScale - scale) * scaleSmoothness;
    }
    
    private void drawWrappedText(SpriteBatch batch, String text, float x, float y, float maxWidth) {
        Array<String> lines = new Array<>();
        StringBuilder currentLine = new StringBuilder();
        String[] words = text.split(" ");
        
        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            layout.setText(font, testLine);
            if (layout.width <= maxWidth) {
                if (currentLine.length() > 0) currentLine.append(" ");
                currentLine.append(word);
            } else {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            }
        }
        if (currentLine.length() > 0) lines.add(currentLine.toString());
        
        float lineHeight = font.getLineHeight();
        for (int i = 0; i < lines.size; i++) {
            font.draw(batch, lines.get(i), x, y - i * lineHeight);
        }
    }
    
    public void resetAnimation() {
        this.wasActive = false;
        this.alpha = 0f;
        this.targetAlpha = 0f;
        this.scale = 0.8f;
        this.targetScale = 0.8f;
    }
}