package com.mafiozi.seriousgame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;

public class AnimationComponent {

    public enum Direction { DOWN, DOWN_SIDE, SIDE, UP_SIDE, UP }

    private final HashMap<String, Animation<TextureRegion>> animations = new HashMap<>();
    private final TextureRegion frameCache = new TextureRegion();

    private String currentState = "idle";
    private Direction currentDir = Direction.DOWN;
    private boolean flipX = false;
    private float stateTime = 0f;
    
    private Direction lastMovingDir = Direction.DOWN;
    private boolean lastMovingFlipX = false;

    public void addAnimation(String name, Texture sheet,
                             int startFrame, int frameCount,
                             int frameW, int frameH, int sheetCols,
                             float frameDuration) {
        TextureRegion[] frames = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            int idx = startFrame + i;
            int col = idx % sheetCols;
            int row = idx / sheetCols;
            frames[i] = new TextureRegion(sheet, col * frameW, row * frameH, frameW, frameH);
        }
        animations.put(name, new Animation<>(frameDuration, frames));
    }
    
    public void updateDirection(float dx, float dy, boolean moving) {
        String state = moving ? "walk" : "idle";
        
        // Если стоим на месте - ничего не меняем
        if (Math.abs(dx) < 0.1f && Math.abs(dy) < 0.1f) {
        	currentDir = lastMovingDir;
            flipX = lastMovingFlipX;
            setState(state + "_" + currentDir.name());
            return;
        }
        
        // Вычисляем угол в градусах (0° - вправо, 90° - вверх)
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        // Приводим к диапазону [0, 360)
        if (angle < 0) angle += 360;
        
        // Определяем направление с зонами (каждый сектор по 45°)
        // Смещаем на 22.5° для более естественного поведения
        Direction newDir = currentDir;
        boolean newFlipX = flipX;
        
        if (angle >= 22.5f && angle < 67.5f) {
            // Вверх-вправо
            newDir = Direction.UP_SIDE;
            newFlipX = false;
        } else if (angle >= 67.5f && angle < 112.5f) {
            // Вверх
            newDir = Direction.UP;
            newFlipX = false;
        } else if (angle >= 112.5f && angle < 157.5f) {
            // Вверх-влево
            newDir = Direction.UP_SIDE;
            newFlipX = true;
        } else if (angle >= 157.5f && angle < 202.5f) {
            // Влево
            newDir = Direction.SIDE;
            newFlipX = true;
        } else if (angle >= 202.5f && angle < 247.5f) {
            // Вниз-влево
            newDir = Direction.DOWN_SIDE;
            newFlipX = true;
        } else if (angle >= 247.5f && angle < 292.5f) {
            // Вниз
            newDir = Direction.DOWN;
            newFlipX = false;
        } else if (angle >= 292.5f && angle < 337.5f) {
            // Вниз-вправо
            newDir = Direction.DOWN_SIDE;
            newFlipX = false;
        } else {
            // Вправо (0° ± 22.5°)
            newDir = Direction.SIDE;
            newFlipX = false;
        }
        
        lastMovingDir = newDir;
        lastMovingFlipX = newFlipX;
        
        if (newDir != currentDir || newFlipX != flipX) {
            currentDir = newDir;
            flipX = newFlipX;
            setState(state + "_" + currentDir.name());
        }
    }

    public void setState(String state) {
        if (!state.equals(currentState)) {
            currentState = state;
            stateTime = 0f;
        }
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public TextureRegion getFrame() {
        Animation<TextureRegion> anim = animations.get(currentState);
        if (anim == null) anim = animations.get("idle_DOWN"); // fallback
        if (anim == null) return null;

        TextureRegion src = anim.getKeyFrame(stateTime, true);
        frameCache.setRegion(src);
        if (frameCache.isFlipX() != flipX) {
            frameCache.flip(true, false);
        }
        return frameCache;
    }

    public boolean isFinished() {
        Animation<TextureRegion> anim = animations.get(currentState);
        return anim == null || anim.isAnimationFinished(stateTime);
    }
}