package com.itemis.gef.tutorial.mindmap.models;

import javafx.event.Event;
import javafx.scene.Node;

/**
 * Interface to describe a field in a part, which is inline editable
 * 
 * @author hniederhausen
 *
 */
public interface IInlineEditableField {

	/**
	 * @return the name of the property to edit 
	 */
	String getPropertyName();

	/**
	 * @return the JavaFX visual showing the value in the parts Visual
	 */
	Node getReadOnlyNode();
	
	void setEditorNode(Node editorNode);

	Node getEditorNode();

	Object getNewValue();

	Object getOldValue();

	boolean isSubmitEvent(Event e);
}