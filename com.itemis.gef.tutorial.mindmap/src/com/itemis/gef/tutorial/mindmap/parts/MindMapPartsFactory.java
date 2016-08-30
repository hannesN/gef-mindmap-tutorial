package com.itemis.gef.tutorial.mindmap.parts;

import java.util.Map;

import org.eclipse.gef.mvc.behaviors.IBehavior;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IContentPartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.itemis.gef.tutorial.mindmap.model.MindMapConnection;
import com.itemis.gef.tutorial.mindmap.model.SimpleMindMap;
import com.itemis.gef.tutorial.mindmap.model.MindMapNode;

import javafx.scene.Node;

/**
 * The {@link MindMapPartsFactory} creates a Part for the mind map models, based on the type of
 * the model instance.
 * 
 * @author hniederhausen
 *
 */
public class MindMapPartsFactory implements IContentPartFactory<Node> {
	
	@Inject
	private Injector injector;
	
	@Override
	public IContentPart<Node, ? extends Node> createContentPart(Object content, IBehavior<Node> contextBehavior,
			Map<Object, Object> contextMap) {

		
		if (content==null)
			throw new IllegalArgumentException("Content must not be null!");
		
		if (content instanceof SimpleMindMap) {
			return injector.getInstance(SimpleMindMapPart.class);
		} else if (content instanceof MindMapNode) {
			return injector.getInstance(MindMapNodePart.class);
		} else if (content instanceof MindMapConnection) {
			return injector.getInstance(MindMapConnectionPart.class);
		} else {
			throw new IllegalArgumentException("Unknown content type <"+content.getClass().getName()+">");
		}
		
	}
}
