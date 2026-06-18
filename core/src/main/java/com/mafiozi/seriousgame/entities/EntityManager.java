package com.mafiozi.seriousgame.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class EntityManager {
    private final Array<Entity> entities = new Array<>();
    private boolean needsSorting = true;

    public void add(Entity e) {
        entities.add(e);
        needsSorting = true;
    }

    public void update(float delta) {
        Entity[] snapshot = entities.toArray(Entity.class);
        for (Entity e : snapshot) {
            e.update(delta);
        }
    }

    public void draw(SpriteBatch batch) {
        if (needsSorting) {
            entities.sort((a, b) -> Float.compare(b.y, a.y));
            needsSorting = false;
        }
        for (Entity e : entities) {
            e.draw(batch);
        }
    }

    public Entity getNearby(Entity source, float range) {
        for (Entity e : entities) {
            if (e != source && e.isNear(source, range)) {
                return e;
            }
        }
        return null;
    }
    
    public Array<Entity> getAllEntities() {
        return entities;
    }
}