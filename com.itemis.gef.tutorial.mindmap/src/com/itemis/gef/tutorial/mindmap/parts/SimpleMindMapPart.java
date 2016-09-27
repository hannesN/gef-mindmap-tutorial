package com.itemis.gef.tutorial.mindmap.parts;

import java.util.List;

import org.eclipse.gef.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef.mvc.parts.IVisualPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;
import com.itemis.gef.tutorial.mindmap.model.AbstractMindMapItem;
import com.itemis.gef.tutorial.mindmap.model.SimpleMindMap;

import javafx.scene.Group;
import javafx.scene.Node;


/**
 * 
 * The {@link SimpleMindMapPart} is responsible to create a visual for the {@link SimpleMindMap} and
 * manage the children of the mind map. 
 * 
 * @author hniederhausen
 *
 */
public class SimpleMindMapPart extends AbstractFXContentPart<Group> {

	@Override
	public SimpleMindMap getContent() {
		return (SimpleMindMap) super.getContent();
	}

	@Override
	protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
		return HashMultimap.create();
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		return Lists.newArrayList(getContent().getChildElements());
	}

	@Override
	protected Group createVisual() {
		// the visual is just a container for our child visuals (nodes and
		// connections)
		return new Group();
	}

	@Override
	protected void addChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		getVisual().getChildren().add(child.getVisual());
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		getVisual().getChildren().remove(child.getVisual());
	}

	@Override
	protected void doAddContentChild(Object contentChild, int index) {
		if (contentChild instanceof AbstractMindMapItem) {
			getContent().addChildElement((AbstractMindMapItem) contentChild, index);
		} else {
			throw new IllegalArgumentException("contentChild has invalid type: " + contentChild.getClass());
		}
	}

	@Override
	protected void doRemoveContentChild(Object contentChild) {
		if (contentChild instanceof AbstractMindMapItem) {
			getContent().removeChildElement((AbstractMindMapItem) contentChild);
		} else {
			throw new IllegalArgumentException("contentChild has invalid type: " + contentChild.getClass());
		}
	}

	@Override
	protected void doRefreshVisual(Group visual) {
		// no refreshing necessary, just a Group
	}
}
