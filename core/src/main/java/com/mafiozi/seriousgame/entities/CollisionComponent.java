package com.mafiozi.seriousgame.entities;

import com.badlogic.gdx.math.Rectangle;

public class CollisionComponent {
    private Entity entity;
    private float offsetX, offsetY;
    private float width, height;

    public CollisionComponent(Entity entity, float width, float height, float offsetX, float offsetY) {
        this.entity = entity;
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public Rectangle getBounds(float x, float y) {
        return new Rectangle(x + offsetX, y + offsetY, width, height);
    }

    public Rectangle getBounds() {
        return getBounds(entity.getX(), entity.getY());
    }
}