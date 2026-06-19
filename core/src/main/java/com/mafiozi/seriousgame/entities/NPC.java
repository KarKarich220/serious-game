package com.mafiozi.seriousgame.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mafiozi.seriousgame.dialogue.DialogueComponent;

public class NPC extends Entity implements CollidableEntity {
    private final TextureRegion sprite;
    private final DialogueComponent dialogue;
    private final CollisionComponent collision;
    private String displayName;

    public NPC(float x, float y, float width, float height, String id,
               TextureRegion sprite, DialogueComponent dialogue) {
        super(x, y, width, height, id);
        if (sprite == null) {
            throw new IllegalArgumentException("Sprite cannot be null");
        }
        if (dialogue == null) {
            throw new IllegalArgumentException("DialogueComponent cannot be null");
        }
        this.sprite = sprite;
        this.dialogue = dialogue;

        float colWidth = width * 0.7f;
        float colHeight = height * 0.7f;
        float colOffX = (width - colWidth) / 2f;
        float colOffY = (height - colHeight) / 2f;
        this.collision = new CollisionComponent(this, colWidth, colHeight, colOffX, colOffY);
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(sprite, getX(), getY(), getWidth(), getHeight());
    }

    public void setName(String name) {
        this.displayName = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public DialogueComponent getDialogue() {
        return dialogue;
    }

    @Override
    public CollisionComponent getCollision() {
        return collision;
    }
}