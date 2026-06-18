package com.mafiozi.seriousgame.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Entity {
    protected float x, y;
    protected float width, height;
    protected String id;

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
        float dx = (x + width/2) - (other.x + other.width/2);
        float dy = (y + height/2) - (other.y + other.height/2);
        return dx * dx + dy * dy <= range * range;
    }

    public String getId() { return id; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}