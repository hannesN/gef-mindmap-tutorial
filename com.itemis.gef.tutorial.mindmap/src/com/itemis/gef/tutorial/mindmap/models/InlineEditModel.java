package com.itemis.gef.tutorial.mindmap.models;

import org.eclipse.gef.mvc.parts.IVisualPart;

import javafx.scene.Node;

/**
 * The InlineEditModel is used to store temporary information while direct editing a part.
 * 
 * @author hniederhausen
 *
 */
public class InlineEditModel {

	private IVisualPart<Node, ? extends Node> host;
	
	private IInlineEditableField currentEditableField;
	
	
	public void setHost(IVisualPart<Node, ? extends Node> host) {
		this.host = host;
	}

	public IVisualPart<Node, ? extends Node> getHost() {
		return host;
	}
	
	public void setCurrentEditableField(IInlineEditableField currentEditableField) {
		this.currentEditableField = currentEditableField;
	}

	public IInlineEditableField getCurrentEditableField() {
		return currentEditableField;
	}
	
	
}
