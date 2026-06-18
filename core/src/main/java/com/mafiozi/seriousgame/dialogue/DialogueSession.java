package com.mafiozi.seriousgame.dialogue;

import com.badlogic.gdx.utils.Array;

public class DialogueSession {
    private Dialogue dialogue;
    private DialogueNode currentNode;

    public DialogueSession(Dialogue dialogue) {
        this.dialogue = dialogue;
        this.currentNode = dialogue.nodes.get(dialogue.startNode);
    }

    public String getText() {
        return currentNode.text;
    }

    public Array<DialogueChoice> getChoices() {
        return currentNode.choices;
    }

    public void next() {
        if (currentNode.next != null) {
            currentNode = dialogue.nodes.get(currentNode.next);
        }
    }

    public void choose(int index) {
        DialogueChoice choice = currentNode.choices.get(index);
        currentNode = dialogue.nodes.get(choice.nextNodeId);
        // Автоматически проходим цепочку next, пока не встретим узел с выбором или конец
        followNextChain();
    }

    public void advanceByNext() {
        if (hasNext()) {
            currentNode = dialogue.nodes.get(currentNode.next);
            // И снова автоматический проход
            followNextChain();
        }
    }

    public boolean isFinished() {
        return currentNode.next == null && currentNode.choices.size == 0;
    }
    
    public boolean hasNext() {
        return currentNode.next != null && !currentNode.next.isEmpty();
    }

    // Если нужно узнать, есть ли выбор
    public boolean hasChoices() {
        return currentNode.choices != null && currentNode.choices.size > 0;
    }
    
    private void followNextChain() {
    	while (currentNode.choices.size == 0 && currentNode.next != null) {
            currentNode = dialogue.nodes.get(currentNode.next);
        }
    }
}