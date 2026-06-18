package com.mafiozi.seriousgame.dialogue;

import com.badlogic.gdx.utils.Array;

public class DialogueNode {
    public String id;
    public String text;
    public Array<DialogueChoice> choices;
    public String next; // fallback if no choices
}