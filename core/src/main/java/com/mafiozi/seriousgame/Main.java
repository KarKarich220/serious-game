package com.mafiozi.seriousgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class Main extends Game {
    private AssetLoader assetLoader;

    @Override
    public void create() {
        assetLoader = new AssetLoader();
        try {
            assetLoader.loadAll();
        } catch (Exception e) {
            Gdx.app.error("Main", "Ошибка загрузки", e);
            Gdx.app.exit();
            return;
        }
        setScreen(new GameScreen(assetLoader));
    }

    @Override
    public void dispose() {
        super.dispose();
        assetLoader.dispose();
    }
}