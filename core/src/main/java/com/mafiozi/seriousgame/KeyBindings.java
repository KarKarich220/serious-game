package com.mafiozi.seriousgame;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Input.Keys;

public class KeyBindings {
    private final Map<String, Integer> bindings = new HashMap<>();
    
    public KeyBindings() {
        bindings.put("move_up", Keys.W);
        bindings.put("move_down", Keys.S);
        bindings.put("move_left", Keys.A);
        bindings.put("move_right", Keys.D);
        bindings.put("interact", Keys.Z);
        bindings.put("dialogue_advance", Keys.Z);
        bindings.put("debug", Keys.F3);
        
        for (int i = 0; i < 9; i++) {
            bindings.put("dialogue_option_" + i, Keys.NUM_1 + i);
        }
    }
    
    public int getActionKey(String action) {
        return bindings.getOrDefault(action, Keys.UNKNOWN);
    }
    
    public int getDialogueOptionKey(int index) {
        return bindings.getOrDefault("dialogue_option_" + index, Keys.NUM_1 + index);
    }
}