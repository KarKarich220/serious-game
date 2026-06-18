package com.mafiozi.seriousgame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.environment.AmbientCubemap;

public class Player extends Entity implements CollidableEntity {
    private MovementComponent movement;
    private AnimationComponent animation;
    private CollisionComponent collision;
    private CollisionSystem collisionSystem;
    private Texture sheet;

    public Player(float x, float y, Texture texture, CollisionSystem collisionSystem) {
        super(x, y, 24, 24, "player");
        this.collisionSystem = collisionSystem;
        
        this.collision = new CollisionComponent(this, 14, 14, 4, 0);
        this.movement = new MovementComponent(this);
        animation = new AnimationComponent();
        int W = 24, H = 24, COLS = 8;
        float fps = 0.12f;

        animation.addAnimation("idle_DOWN",      texture, 8,  4, W, H, COLS, fps);
        animation.addAnimation("idle_DOWN_SIDE", texture, 12, 4, W, H, COLS, fps);
        animation.addAnimation("idle_SIDE",      texture, 16, 4, W, H, COLS, fps);
        animation.addAnimation("idle_UP_SIDE",   texture, 20, 4, W, H, COLS, fps);
        animation.addAnimation("idle_UP",        texture, 24, 4, W, H, COLS, fps);

        animation.addAnimation("walk_DOWN",      texture, 28, 4, W, H, COLS, fps);
        animation.addAnimation("walk_DOWN_SIDE", texture, 32, 4, W, H, COLS, fps);
        animation.addAnimation("walk_SIDE",      texture, 36, 4, W, H, COLS, fps);
        animation.addAnimation("walk_UP_SIDE",   texture, 40, 4, W, H, COLS, fps);
        animation.addAnimation("walk_UP",        texture, 44, 4, W, H, COLS, fps);
    }

    @Override
    public void update(float delta) {
        float oldX = x, oldY = y;
        movement.update(delta);
        if (collisionSystem.collides(this, x, oldY)) x = oldX;
        if (collisionSystem.collides(this, oldX, y)) y = oldY;

        float dx = x - oldX;
        float dy = y - oldY;
        animation.updateDirection(dx, dy, movement.isMoving());
        animation.update(delta);
    }

    @Override
    public void draw(SpriteBatch batch) {
    	batch.draw(animation.getFrame(), x, y, width, height);
    }

    @Override
    public CollisionComponent getCollision() {
        return collision;
    }
}