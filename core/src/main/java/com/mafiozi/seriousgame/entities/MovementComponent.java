package com.mafiozi.seriousgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class MovementComponent {
    private Player player;
    private float speed = 60f;
    private boolean moving;

    public MovementComponent(Player player) {
        this.player = player;
    }

    public void update(float delta) {
        float dx = 0, dy = 0;

        if (Gdx.input.isKeyPressed(Keys.W)) dy += 1;
        if (Gdx.input.isKeyPressed(Keys.S)) dy -= 1;
        if (Gdx.input.isKeyPressed(Keys.A)) dx -= 1;
        if (Gdx.input.isKeyPressed(Keys.D)) dx += 1;

        if (dx != 0 && dy != 0) {
            dx *= 0.7071f;
            dy *= 0.7071f;
        }

        moving = dx != 0 || dy != 0;

        player.x += dx * speed * delta;
        player.y += dy * speed * delta;
        
        
    }

    public boolean isMoving() {
        return moving;
    }
}