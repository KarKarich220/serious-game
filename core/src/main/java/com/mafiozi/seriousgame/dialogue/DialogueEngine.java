package com.mafiozi.seriousgame.dialogue;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;

public class DialogueEngine {
    // --- Состояние сессии ---
    private DialogueSession session;
    private boolean active = false;
    
    // --- Печать текста ---
    private String fullText = "";
    private String displayedText = "";
    private int charIndex = 0;
    private float typingTimer = 0f;
    private float typingSpeed = 0.025f;
    private boolean isTypingComplete = false;
    
    // --- Пауза ---
    private boolean isPaused = false;
    private float pauseTimer = 0f;
    private boolean waitingForCommand = false; // флаг, что мы ждём завершения команды
    
    // --- Токены ---
    private Array<DialogueToken> tokens;
    private int currentTokenIndex = 0;
    private int charsPrintedInToken = 0;
    
    // --- Эффекты ---
    private boolean shakeActive = false;
    private float shakeIntensity = 2f;
    private String currentColor = "#FFFFFF";
    private boolean colorActive = false;
    private Sound typingSound = null;
    
    // --- Действия ---
    private Map<String, DialogueAction> actions = new HashMap<>();
    private DialogueManager manager;
    
    public DialogueEngine(DialogueManager manager) {
        this.manager = manager;
    }
    
    public void registerAction(String name, DialogueAction action) {
        actions.put(name, action);
    }
    
    public void setTypingSound(Sound sound) {
        this.typingSound = sound;
    }
    
    public void startDialogue(String dialogueId) {
        Dialogue dialogue = manager.getDialogue(dialogueId);
        if (dialogue == null) return;
        session = new DialogueSession(dialogue);
        active = true;
        parseTokens(session.getText());
        resetTyping();
    }
    
    private void parseTokens(String text) {
        tokens = DialogueParser.parse(text);
        StringBuilder sb = new StringBuilder();
        for (DialogueToken t : tokens) {
            if (t.type == DialogueToken.Type.TEXT) {
                sb.append(t.value);
            }
        }
        fullText = sb.toString();
    }
    
    private void resetTyping() {
        displayedText = "";
        charIndex = 0;
        typingTimer = 0f;
        isTypingComplete = false;
        isPaused = false;
        pauseTimer = 0f;
        waitingForCommand = false;
        currentTokenIndex = 0;
        charsPrintedInToken = 0;
        shakeActive = false;
        shakeIntensity = 2f;
        currentColor = "#FFFFFF";
        colorActive = false;
    }
    
    public void update(float delta) {
        if (!active || session == null) return;
        
        // --- Если на паузе ---
        if (isPaused) {
            pauseTimer -= delta;
            if (pauseTimer <= 0) {
                isPaused = false;
                // После паузы продолжаем с того же места
                // Следующий вызов update() продолжит печать
            }
            return;
        }
        
        // --- Если печать завершена ---
        if (isTypingComplete) return;
        
        // --- Печать символов ---
        typingTimer += delta;
        
        // Печатаем по одному символу за раз (не в цикле, чтобы паузы работали корректно)
        while (typingTimer >= typingSpeed && !isTypingComplete && !isPaused) {
            typingTimer -= typingSpeed;
            
            // Проверяем, есть ли ещё токены
            if (currentTokenIndex >= tokens.size) {
                isTypingComplete = true;
                break;
            }
            
            DialogueToken token = tokens.get(currentTokenIndex);
            
            // Обрабатываем токен
            if (token.type == DialogueToken.Type.TEXT) {
                String text = token.value;
                if (charsPrintedInToken < text.length()) {
                    // Печатаем следующий символ
                    char c = text.charAt(charsPrintedInToken);
                    charIndex++;
                    displayedText = fullText.substring(0, Math.min(charIndex, fullText.length()));
                    charsPrintedInToken++;
                    
                    // Звук печати
                    if (typingSound != null && c != ' ') {
                        typingSound.play(0.4f);
                    }
                    
                    // Если закончили текст в токене, переходим к следующему
                    if (charsPrintedInToken >= text.length()) {
                        currentTokenIndex++;
                        charsPrintedInToken = 0;
                        // Проверяем следующий токен (может быть командой)
                        processNextToken();
                    }
                }
            } else {
                // Это команда - выполняем её
                processCommand(token);
                currentTokenIndex++;
                charsPrintedInToken = 0;
                
                // Если после команды больше нет токенов, завершаем
                if (currentTokenIndex >= tokens.size) {
                    isTypingComplete = true;
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
            // Это команда - выполняем её
            processCommand(token);
            currentTokenIndex++;
            // Рекурсивно проверяем следующий
            processNextToken();
        }
        // Если TEXT - ничего не делаем, печать продолжится в update()
    }
    
    private void processCommand(DialogueToken token) {
        switch (token.type) {
            case PAUSE:
                isPaused = true;
                pauseTimer = token.floatParam;
                Gdx.app.log("DialogueEngine", "Pause: " + pauseTimer + "s");
                break;
                
            case SHAKE_ON:
                shakeActive = true;
                Gdx.app.log("DialogueEngine", "Shake ON");
                break;
                
            case SHAKE_OFF:
                shakeActive = false;
                Gdx.app.log("DialogueEngine", "Shake OFF");
                break;
                
            case COLOR_ON:
                currentColor = token.value;
                colorActive = true;
                Gdx.app.log("DialogueEngine", "Color ON: " + currentColor);
                break;
                
            case COLOR_OFF:
                colorActive = false;
                currentColor = "#FFFFFF";
                Gdx.app.log("DialogueEngine", "Color OFF");
                break;
                
            case MUSIC:
                executeAction("music", token.value);
                break;
                
            case SOUND:
                executeAction("sound", token.value);
                break;
                
            case CALL:
                String[] parts = token.value.split(":", 2);
                String actionName = parts[0];
                String param = parts.length > 1 ? parts[1] : "";
                executeAction(actionName, param);
                break;
                
            case SPEED:
                typingSpeed = token.floatParam;
                Gdx.app.log("DialogueEngine", "Speed changed to: " + typingSpeed);
                break;
                
            default:
                break;
        }
    }
    
    private void executeAction(String name, String param) {
        DialogueAction action = actions.get(name);
        if (action != null) {
            action.execute(param);
        }
    }
    
    public void advance() {
        if (!active || session == null) return;
        
        // Если текст ещё печатается - пропускаем печать
        if (!isTypingComplete) {
            skipTyping();
            return;
        }
        
        // Если текст напечатан полностью
        // Если есть выбор - ничего не делаем (выбор обрабатывается отдельно)
        if (session.hasChoices()) return;
        
        // Если есть следующий узел - переходим
        if (session.hasNext()) {
            session.advanceByNext();
            parseTokens(session.getText());
            resetTyping();
        } else if (session.isFinished()) {
            closeDialogue();
        }
    }
    
    public void skipTyping() {
        if (!isTypingComplete) {
            // Быстро выполняем все оставшиеся команды
            while (currentTokenIndex < tokens.size) {
                DialogueToken token = tokens.get(currentTokenIndex);
                if (token.type != DialogueToken.Type.TEXT) {
                    processCommand(token);
                }
                currentTokenIndex++;
            }
            
            // Показываем полный текст
            charIndex = fullText.length();
            displayedText = fullText;
            isTypingComplete = true;
            isPaused = false; // отключаем любую паузу
        }
    }
    
    public void chooseOption(int index) {
        if (!active || session == null) return;
        if (!session.hasChoices()) return;
        if (index < 0 || index >= session.getChoices().size) return;
        
        session.choose(index);
        if (session.isFinished()) {
            closeDialogue();
        } else {
            parseTokens(session.getText());
            resetTyping();
        }
    }
    
    public void closeDialogue() {
        active = false;
        session = null;
        displayedText = "";
        fullText = "";
        isTypingComplete = false;
        isPaused = false;
        pauseTimer = 0f;
        shakeActive = false;
        colorActive = false;
        currentColor = "#FFFFFF";
    }
    
    // --- Геттеры ---
    public boolean isActive() { return active; }
    public String getDisplayedText() { return displayedText; }
    public boolean isTypingComplete() { return isTypingComplete; }
    public Array<DialogueChoice> getChoices() { 
        return session != null ? session.getChoices() : null; 
    }
    
    public float getShakeIntensity() { 
        return shakeActive ? shakeIntensity : 0f; 
    }
    
    public String getCurrentColor() { 
        return colorActive ? currentColor : "#FFFFFF"; 
    }
    
    public boolean isColorActive() { return colorActive; }
    public boolean isShakeActive() { return shakeActive; }
    public DialogueSession getSession() { return session; }
    
    public interface DialogueAction {
        void execute(String param);
    }
}