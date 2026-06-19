package com.mafiozi.seriousgame.dialogue;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;

public class DialogueTyper {
    private Array<DialogueToken> tokens;
    private int currentTokenIndex;
    private int charsPrintedInToken;

    private String fullText;
    private String displayedText;

    private float typingTimer;
    private float typingSpeed;
    private boolean isTypingComplete;

    private boolean isPaused;
    private float pauseTimer;

    private Sound typingSound;
    private DialogueCommandExecutor commandExecutor;

    public DialogueTyper() {
        reset();
    }

    public void reset() {
        tokens = null;
        currentTokenIndex = 0;
        charsPrintedInToken = 0;
        fullText = "";
        displayedText = "";
        typingTimer = 0f;
        typingSpeed = 0.025f;
        isTypingComplete = false;
        isPaused = false;
        pauseTimer = 0f;
    }

    public void start(String text, Array<DialogueToken> tokenList, float speed) {
        reset();
        
        StringBuilder sb = new StringBuilder();
        for (DialogueToken t : tokenList) {
            if (t.type == DialogueToken.Type.TEXT) {
                sb.append(t.value);
            }
        }
        this.fullText = sb.toString();
        this.tokens = tokenList;
        this.typingSpeed = speed;
        this.isTypingComplete = false;
    }

    public void update(float delta) {
        if (isTypingComplete || tokens == null) return;

        if (isPaused) {
            pauseTimer -= delta;
            if (pauseTimer <= 0) {
                isPaused = false;
            }
            return;
        }

        typingTimer += delta;

        while (typingTimer >= typingSpeed && !isTypingComplete && !isPaused) {
            typingTimer -= typingSpeed;

            if (currentTokenIndex >= tokens.size) {
                isTypingComplete = true;
                break;
            }

            DialogueToken token = tokens.get(currentTokenIndex);

            if (token.type == DialogueToken.Type.TEXT) {
                String text = token.value;
                if (charsPrintedInToken < text.length()) {
                    char c = text.charAt(charsPrintedInToken);
                    displayedText += c;
                    charsPrintedInToken++;

                    if (typingSound != null && c != ' ') {
                        typingSound.play(0.4f);
                    }

                    if (charsPrintedInToken >= text.length()) {
                        currentTokenIndex++;
                        charsPrintedInToken = 0;
                        processNextToken();
                    }
                }
            } else {
                commandExecutor.execute(token);
                currentTokenIndex++;
                charsPrintedInToken = 0;

                if (currentTokenIndex >= tokens.size) {
                    isTypingComplete = true;
                } else {
                    processNextToken();
                }
            }
        }
    }

    private void processNextToken() {
        if (currentTokenIndex >= tokens.size) {
            isTypingComplete = true;
            return;
        }

        DialogueToken token = tokens.get(currentTokenIndex);
        if (token.type != DialogueToken.Type.TEXT) {
            commandExecutor.execute(token);
            currentTokenIndex++;
            processNextToken();
        }
    }

    public void skipTyping() {
        if (isTypingComplete) return;

        while (currentTokenIndex < tokens.size) {
            DialogueToken token = tokens.get(currentTokenIndex);
            if (token.type != DialogueToken.Type.TEXT) {
                commandExecutor.execute(token);
            }
            currentTokenIndex++;
        }
        displayedText = fullText;
        isTypingComplete = true;
        isPaused = false;
        pauseTimer = 0f;
    }

    public void setCommandExecutor(DialogueCommandExecutor executor) {
        this.commandExecutor = executor;
    }
    
    public String getDisplayedText() { return displayedText; }
    public boolean isTypingComplete() { return isTypingComplete; }
    public boolean isPaused() { return isPaused; }

    public void setTypingSpeed(float speed) {
        this.typingSpeed = Math.max(0.001f, speed);
    }

    public void pause(float seconds) {
        if (seconds > 0) {
            isPaused = true;
            pauseTimer = seconds;
        }
    }

    public void setTypingSound(Sound sound) {
        this.typingSound = sound;
    }

    public String getFullText() { return fullText; }
}