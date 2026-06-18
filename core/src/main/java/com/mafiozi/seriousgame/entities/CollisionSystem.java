package com.mafiozi.seriousgame.entities;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class CollisionSystem {
    private TiledMapTileLayer wallLayer;
    private Array<Entity> entities;

    public CollisionSystem(TiledMapTileLayer wallLayer, Array<Entity> entities) {
        this.wallLayer = wallLayer;
        this.entities = entities;
    }

    public boolean collides(Entity entity, float newX, float newY) {
        if (!(entity instanceof CollidableEntity)) return false;
        CollisionComponent col = ((CollidableEntity) entity).getCollision();
        Rectangle bounds = col.getBounds(newX, newY);
        return collidesWithTiles(bounds) || collidesWithEntities(bounds, entity);
    }

    private boolean collidesWithTiles(Rectangle rect) {
        int tileSize = 16;
        int startX = (int)(rect.x / tileSize);
        int startY = (int)(rect.y / tileSize);
        int endX = (int)((rect.x + rect.width) / tileSize);
        int endY = (int)((rect.y + rect.height) / tileSize);
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                if (wallLayer.getCell(x, y) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean collidesWithEntities(Rectangle rect, Entity selfEntity) {
        for (Entity e : entities) {
            if (e == selfEntity) continue;
            if (e instanceof CollidableEntity) {
                CollisionComponent otherCol = ((CollidableEntity) e).getCollision();
                if (rect.overlaps(otherCol.getBounds())) {
                    return true;
                }
            }
        }
        return false;
    }
}