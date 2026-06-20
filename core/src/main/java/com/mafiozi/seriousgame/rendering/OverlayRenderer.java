package com.mafiozi.seriousgame.rendering;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class OverlayRenderer {
    private final SpriteBatch batch;
    private final ShapeRenderer shapes;

    public OverlayRenderer(SpriteBatch batch, ShapeRenderer shapes) {
        this.batch = batch;
        this.shapes = shapes;
    }

    public void render(EffectsController fx, OrthographicCamera camera) {
        float fade = fx.getFade();
        float flash = fx.getFlash();
        float damage = fx.getDamage();

        if (fade <= 0 && flash <= 0 && damage <= 0) return;

        shapes.setProjectionMatrix(camera.combined);

        shapes.begin(ShapeRenderer.ShapeType.Filled);

        // fade (чёрный)
        if (fade > 0) {
            shapes.setColor(0, 0, 0, fade);
            shapes.rect(0, 0, camera.viewportWidth, camera.viewportHeight);
        }

        // flash (белый)
        if (flash > 0) {
            shapes.setColor(1, 1, 1, flash);
            shapes.rect(0, 0, camera.viewportWidth, camera.viewportHeight);
        }

        // damage (красный)
        if (damage > 0) {
            shapes.setColor(1, 0, 0, damage * 0.7f);
            shapes.rect(0, 0, camera.viewportWidth, camera.viewportHeight);
        }

        shapes.end();
    }
}