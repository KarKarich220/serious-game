package com.mafiozi.seriousgame.dialogue;

public class DialogueToken {
    public enum Type {
        TEXT,      // обычный текст
        PAUSE,     // пауза {pause:0.5}
        SHAKE_ON,  // начало тряски <shake>
        SHAKE_OFF, // конец тряски </shake>
        COLOR_ON,  // начало цвета <color:#FF0000>
        COLOR_OFF, // конец цвета </color>
        MUSIC,     // музыка {music:path}
        CALL,      // вызов действия {call:actionName:param}
        SPEED,     // скорость печати {speed:0.05}
        SOUND      // звук {sound:path}
    }
    
    public Type type;
    public String value;
    public float floatParam;
    
    public DialogueToken(Type type, String value) {
        this.type = type;
        this.value = value;
    }
    
    public DialogueToken(Type type, String value, float floatParam) {
        this(type, value);
        this.floatParam = floatParam;
    }
}