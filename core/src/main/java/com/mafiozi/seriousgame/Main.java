package com.mafiozi.seriousgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class Main extends Game {
    private AssetManager assetManager;

    @Override
    public void create() {
        assetManager = new AssetManager();

        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));

        assetManager.load("assets/map.tmx", TiledMap.class);
        assetManager.load("assets/player_sheet.png", Texture.class);
        assetManager.load("assets/guide_npc.png", Texture.class);
        assetManager.load("assets/music/background_music.ogg", Music.class);
        assetManager.load("assets/sounds/npc_guide_type.wav", Sound.class);
        
        assetManager.load("assets/fonts/font.fnt", BitmapFont.class);

        assetManager.finishLoading();
        
        setScreen(new GameScreen(this));
    }
    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public void dispose() {
        super.dispose();
        assetManager.dispose();
    }
}