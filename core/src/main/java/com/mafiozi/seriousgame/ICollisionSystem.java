package com.mafiozi.seriousgame;

import com.mafiozi.seriousgame.entities.*;

public interface ICollisionSystem {
    boolean collides(Entity entity, float newX, float newY);
}