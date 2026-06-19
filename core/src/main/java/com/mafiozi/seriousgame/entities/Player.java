package com.mafiozi.seriousgame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mafiozi.seriousgame.GameConfig;
import com.mafiozi.seriousgame.ICollisionSystem;

public class Player extends Entity implements CollidableEntity {
    private final ICollisionSystem collisionSystem;
    private final AnimationComponent animation;
    private final CollisionComponent collision;
    private float speed = GameConfig.PLAYER_SPEED;

    private float moveDx = 0f;
    private float moveDy = 0f;

    public Player(float x, float y, Texture texture, ICollisionSystem collisionSystem) {
        super(x, y, GameConfig.PLAYER_WIDTH, GameConfig.PLAYER_HEIGHT, "player");
        if (texture == null) {
            throw new IllegalArgumentException("Texture cannot be null");
        }
        this.collisionSystem = collisionSystem;
        
        this.collision = new CollisionComponent(this,
            GameConfig.PLAYER_COLLISION_WIDTH,
            GameConfig.PLAYER_COLLISION_HEIGHT,
            GameConfig.PLAYER_COLLISION_OFFSET_X,
            GameConfig.PLAYER_COLLISION_OFFSET_Y
        );

        this.animation = new AnimationComponent();
        initAnimations(texture);
    }

    private void initAnimations(Texture texture) {
        int W = (int)GameConfig.PLAYER_WIDTH;
        int H = (int)GameConfig.PLAYER_HEIGHT;
        int COLS = GameConfig.SPRITE_SHEET_COLS;
        float fps = GameConfig.ANIMATION_FRAME_DURATION;

        String[][] anims = {
            {"idle_DOWN",       "8",  "4"},
            {"idle_DOWN_SIDE",  "12", "4"},
            {"idle_SIDE",       "16", "4"},
            {"idle_UP_SIDE",    "20", "4"},
            {"idle_UP",         "24", "4"},
            {"walk_DOWN",       "28", "4"},
            {"walk_DOWN_SIDE",  "32", "4"},
            {"walk_SIDE",       "36", "4"},
            {"walk_UP_SIDE",    "40", "4"},
            {"walk_UP",         "44", "4"}
        };

        for (String[] a : anims) {
            String name = a[0];
            int start = Integer.parseInt(a[1]);
            int count = Integer.parseInt(a[2]);
            animation.addAnimation(name, texture, start, count, W, H, COLS, fps);
        }
    }

    public void move(float dx, float dy) {
        this.moveDx = dx;
        this.moveDy = dy;
    }

    @Override
    public void update(float delta) {
        float oldX = getX();
        float oldY = getY();

        float newX = oldX + moveDx;
        float newY = oldY + moveDy;

        if (!collisionSystem.collides(this, newX, oldY)) setX(newX);
        if (!collisionSystem.collides(this, oldX, newY)) setY(newY);

        float actualDx = getX() - oldX;
        float actualDy = getY() - oldY;
        boolean moving = (Math.abs(actualDx) > 0.001f || Math.abs(actualDy) > 0.001f);
        animation.updateDirection(actualDx, actualDy, moving);
        animation.update(delta);

        moveDx = 0f;
        moveDy = 0f;
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(animation.getFrame(), getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public CollisionComponent getCollision() {
        return collision;
    }
}