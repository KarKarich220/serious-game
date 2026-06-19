package com.mafiozi.seriousgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;

public class GameScreen implements Screen {
    private final GameWorld gameWorld;
    private final GameRenderer renderer;
    private final InputHandler inputHandler;
    private final GameController gameController;
    
    public GameScreen(AssetLoader assetLoader) {
        this.gameWorld = new GameWorld(assetLoader);
        
        this.renderer = new GameRenderer(assetLoader, gameWorld);
        
        this.gameController = new GameController(gameWorld);
        this.inputHandler = new InputHandler(
            gameWorld.getPlayer(),
            gameWorld.getDialogueEngine(),
            gameWorld.getEntityManager(),
            new KeyBindings()
        );
    }
    
    @Override
    public void render(float delta) {
        gameController.update(delta);
        inputHandler.handleInput();
        
        renderer.render(
            gameWorld.getPlayer(),
            gameWorld.getDialogueEngine(),
            delta
        );
        
        if (Gdx.input.isKeyJustPressed(Keys.F3)) {
            renderer.toggleDebug();
        }
    }
    
    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
    }
    
    @Override
    public void show() {
        Music bgMusic = gameWorld.getBgMusic();
        if (bgMusic != null && !bgMusic.isPlaying()) {
            bgMusic.play();
        }
    }
    
    @Override
    public void hide() {
        Music bgMusic = gameWorld.getBgMusic();
        if (bgMusic != null && bgMusic.isPlaying()) {
            bgMusic.pause();
        }
    }
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void dispose() {
        renderer.dispose();
        gameWorld.dispose();
    }
}