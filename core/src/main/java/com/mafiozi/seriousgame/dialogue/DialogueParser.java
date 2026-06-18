package com.mafiozi.seriousgame.dialogue;

import com.badlogic.gdx.utils.Array;

public class DialogueParser {
    
    public static Array<DialogueToken> parse(String text) {
        Array<DialogueToken> tokens = new Array<>();
        StringBuilder currentText = new StringBuilder();
        
        int i = 0;
        while (i < text.length()) {
            char c = text.charAt(i);
            
            if (c == '{') {
                if (currentText.length() > 0) {
                    tokens.add(new DialogueToken(DialogueToken.Type.TEXT, currentText.toString()));
                    currentText.setLength(0);
                }
                
                int end = text.indexOf('}', i);
                if (end == -1) break;
                
                String command = text.substring(i + 1, end).trim();
                tokens.add(parseCommand(command));
                
                i = end + 1;
            } 
            else if (c == '<') {
                if (currentText.length() > 0) {
                    tokens.add(new DialogueToken(DialogueToken.Type.TEXT, currentText.toString()));
                    currentText.setLength(0);
                }
                
                int end = text.indexOf('>', i);
                if (end == -1) break;
                
                String tag = text.substring(i + 1, end).trim();
                tokens.add(parseTag(tag));
                
                i = end + 1;
            }
            else {
                currentText.append(c);
                i++;
            }
        }
        
        if (currentText.length() > 0) {
            tokens.add(new DialogueToken(DialogueToken.Type.TEXT, currentText.toString()));
        }
        
        return tokens;
    }
    
    private static DialogueToken parseCommand(String command) {
        // Форматы команд:
        // pause:0.5
        // speed:0.025
        // music:assets/music.ogg
        // sound:assets/sounds/click.wav
        // call:actionName:param
        
        if (command.startsWith("pause:")) {
            String val = command.substring(6);
            float seconds = Float.parseFloat(val);
            return new DialogueToken(DialogueToken.Type.PAUSE, "", seconds);
        } 
        else if (command.startsWith("speed:")) {
            String val = command.substring(6);
            float speed = Float.parseFloat(val);
            return new DialogueToken(DialogueToken.Type.SPEED, "", speed);
        } 
        else if (command.startsWith("music:")) {
            String path = command.substring(6);
            return new DialogueToken(DialogueToken.Type.MUSIC, path);
        } 
        else if (command.startsWith("sound:")) {
            String path = command.substring(6);
            return new DialogueToken(DialogueToken.Type.SOUND, path);
        } 
        else if (command.startsWith("call:")) {
            String[] parts = command.substring(5).split(":", 2);
            String actionName = parts[0];
            String param = parts.length > 1 ? parts[1] : "";
            return new DialogueToken(DialogueToken.Type.CALL, actionName + ":" + param);
        } 
        else {
            return new DialogueToken(DialogueToken.Type.TEXT, "{" + command + "}");
        }
    }
    
    private static DialogueToken parseTag(String tag) {
        // Форматы тегов:
        // shake - начало тряски
        // /shake - конец тряски
        // color:#FF0000 - начало цвета
        // /color - конец цвета
        
        if (tag.equals("shake")) {
            return new DialogueToken(DialogueToken.Type.SHAKE_ON, "");
        } 
        else if (tag.equals("/shake")) {
            return new DialogueToken(DialogueToken.Type.SHAKE_OFF, "");
        } 
        else if (tag.startsWith("color:")) {
            String color = tag.substring(6);
            return new DialogueToken(DialogueToken.Type.COLOR_ON, color);
        } 
        else if (tag.equals("/color")) {
            return new DialogueToken(DialogueToken.Type.COLOR_OFF, "");
        } 
        else {
            return new DialogueToken(DialogueToken.Type.TEXT, "<" + tag + ">");
        }
    }
}