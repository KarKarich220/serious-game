package com.mafiozi.seriousgame.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Entity {
    private float x, y;
    private float width, height;
    private String id;

    public Entity(float x, float y, float width, float height, String id) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.id = id;
    }

    public abstract void update(float delta);
    public abstract void draw(SpriteBatch batch);

    public boolean isNear(Entity other, float range) {
        float cx1 = getCenterX();
        float cy1 = getCenterY();
        float cx2 = other.getCenterX();
        float cy2 = other.getCenterY();
        float dx = cx1 - cx2;
        float dy = cy1 - cy2;
        return dx * dx + dy * dy <= range * range;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public String getId() { return id; }

    public float getCenterX() { return x + width / 2; }
    public float getCenterY() { return y + height / 2; }

    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}