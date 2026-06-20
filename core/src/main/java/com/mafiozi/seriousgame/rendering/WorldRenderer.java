package com.mafiozi.seriousgame.rendering;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mafiozi.seriousgame.entities.EntityManager;

public class WorldRenderer {
    private final TiledMapRenderer mapRenderer;
    private final EntityManager entityManager;
    private final SpriteBatch batch;

    public WorldRenderer(TiledMap map, EntityManager entityManager) {
        this.mapRenderer = new OrthogonalTiledMapRenderer(map);
        this.entityManager = entityManager;
        this.batch = new SpriteBatch();
    }

    public void render(OrthographicCamera camera) {
        // карта
        mapRenderer.setView(camera);
        mapRenderer.render();

        // сущности
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        entityManager.draw(batch);
        batch.end();
    }

    public void dispose() {
        batch.dispose();
        ((OrthogonalTiledMapRenderer) mapRenderer).dispose();
    }
}