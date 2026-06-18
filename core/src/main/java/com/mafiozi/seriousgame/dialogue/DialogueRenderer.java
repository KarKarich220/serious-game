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
    private Color tempColor = new Color();
    
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
    
    public void render(SpriteBatch batch, DialogueEngine engine) {
        if (!engine.isActive()) return;
        
        // Фон окна
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0, 0, 0, 0.85f);
        shapes.rect(windowX, windowY, windowWidth, windowHeight);
        shapes.end();
        
        // Текст с эффектами
        String text = engine.getDisplayedText();
        if (text != null && !text.isEmpty()) {
            // Тряска
            float shake = engine.getShakeIntensity();
            if (shake > 0) {
                shakeOffsetX = (float) (Math.random() - 0.5) * shake * 2;
                shakeOffsetY = (float) (Math.random() - 0.5) * shake * 2;
            } else {
                shakeOffsetX = 0;
                shakeOffsetY = 0;
            }
            
            // Цвет
            String colorHex = engine.getCurrentColor();
            Color color = Color.valueOf(colorHex);
            
            batch.begin();
            font.setColor(color);
            drawWrappedText(batch, text,
                    windowX + 20 + shakeOffsetX,
                    windowY + windowHeight - 30 + shakeOffsetY,
                    windowWidth - 40);
            font.setColor(Color.WHITE); // сбрасываем на белый
            batch.end();
        }
        
        // Выборы
        Array<DialogueChoice> choices = engine.getChoices();
        if (choices != null && choices.size > 0) {
            batch.begin();
            float yPos = windowY + 50;
            for (int i = 0; i < choices.size; i++) {
                String choiceText = (i + 1) + ": " + choices.get(i).text;
                font.draw(batch, choiceText, windowX + 30, yPos + (choices.size - 1 - i) * 30);
            }
            batch.end();
        }
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
}