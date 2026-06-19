package com.mafiozi.seriousgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class AssetLoader {
    private final AssetManager assetManager;
    private Texture fallbackTexture;

    public AssetLoader() {
        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    }

    public void loadAll() {
    	assetManager.load(AssetPaths.MAP, TiledMap.class);
        assetManager.load(AssetPaths.PLAYER_SHEET, Texture.class);
        assetManager.load(AssetPaths.NPC_GUIDE, Texture.class);
        assetManager.load(AssetPaths.NPC_GUIDE_SOUND, Sound.class);
        assetManager.load(AssetPaths.BACKGROUND_MUSIC, Music.class);
        assetManager.load(AssetPaths.FONT, BitmapFont.class);
        assetManager.load(AssetPaths.FALLBACK_TEXTURE, Texture.class);
        assetManager.finishLoading();
        fallbackTexture = assetManager.get(AssetPaths.FALLBACK_TEXTURE, Texture.class);
    }

    public Texture getTexture(String path) {
        if (assetManager.isLoaded(path, Texture.class)) {
            return assetManager.get(path, Texture.class);
        } else {
            Gdx.app.log("AssetLoader", "Текстура не найдена: " + path + ", используется заглушка");
            return fallbackTexture;
        }
    }
    
    public <T> T get(String path, Class<T> type) {
        if (assetManager.isLoaded(path, type)) {
            return assetManager.get(path, type);
        } else {
            throw new IllegalStateException("Ресурс не загружен: " + path);
        }
    }

    public void dispose() {
        assetManager.dispose();
    }
}