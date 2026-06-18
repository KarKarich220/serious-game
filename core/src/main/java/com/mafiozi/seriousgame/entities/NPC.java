package com.mafiozi.seriousgame.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mafiozi.seriousgame.dialogue.DialogueComponent;

public class NPC extends Entity implements CollidableEntity {
    private TextureRegion sprite;
    private DialogueComponent dialogue;
    private CollisionComponent collision;
    private String displayName;

    public NPC(float x, float y, float width, float height, String id, TextureRegion sprite, DialogueComponent dialogue) {
        super(x, y, width, height, id);
        this.sprite = sprite;
        this.dialogue = dialogue;
        // Создаём коллизию такого же размера, как спрайт (можно чуть меньше)
        this.collision = new CollisionComponent(this, width, height, 0, 0);
    }
    @Override
    public void update(float delta) {
        // idle for now
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(sprite, x, y, width, height);
    }
    
    public void setName(String name) {
        this.displayName = name;
    }

    public DialogueComponent getDialogue() {
        return dialogue;
    }
	@Override
	public CollisionComponent getCollision() {
		// TODO Auto-generated method stub
		return collision;
	}
}