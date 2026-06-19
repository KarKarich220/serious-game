package com.mafiozi.seriousgame;

public class GameController {
    private final GameWorld gameWorld;
    
    public GameController(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }
    
    public void update(float delta) {
        gameWorld.update(delta);
    }
}