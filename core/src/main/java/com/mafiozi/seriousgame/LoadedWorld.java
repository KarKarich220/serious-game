package com.mafiozi.seriousgame;

import java.util.List;
import com.mafiozi.seriousgame.entities.NPC;

public class LoadedWorld {
    private final List<NPC> npcs;
    private final float spawnX;
    private final float spawnY;
    
    public LoadedWorld(List<NPC> npcs, float spawnX, float spawnY) {
        this.npcs = npcs;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
    }
    
    public List<NPC> getNpcs() {
        return npcs;
    }
    
    public float getSpawnX() {
        return spawnX;
    }
    
    public float getSpawnY() {
        return spawnY;
    }
}