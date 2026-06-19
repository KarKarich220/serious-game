package com.mafiozi.seriousgame.dialogue;

import java.util.HashMap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class DialogueLoader {

	public static Dialogue loadDialogue(FileHandle jsonFile) {
	    JsonReader reader = new JsonReader();
	    JsonValue root = reader.parse(jsonFile);

	    Dialogue dialogue = new Dialogue();
	    dialogue.id = jsonFile.nameWithoutExtension();
	    dialogue.startNode = root.getString("startNode");
	    dialogue.nodes = new HashMap<>();

	    JsonValue nodesObj = root.get("nodes");
	    for (JsonValue nodeEntry : nodesObj.iterator()) {
	        DialogueNode node = new DialogueNode();
	        node.id = nodeEntry.getString("id");
	        node.text = nodeEntry.getString("text");
	        node.next = nodeEntry.getString("next", null);

	        node.choices = new Array<>();
	        JsonValue choicesArr = nodeEntry.get("choices");
	        if (choicesArr != null) {
	            for (JsonValue choiceVal : choicesArr.iterator()) {
	                DialogueChoice ch = new DialogueChoice();
	                ch.text = choiceVal.getString("text");
	                ch.nextNodeId = choiceVal.getString("nextNodeId");
	                node.choices.add(ch);
	            }
	        }

	        dialogue.nodes.put(node.id, node);
	    }
	    return dialogue;
	}
}