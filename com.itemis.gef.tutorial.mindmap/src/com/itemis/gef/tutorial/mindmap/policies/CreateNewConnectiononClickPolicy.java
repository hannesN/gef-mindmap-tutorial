package com.itemis.gef.tutorial.mindmap.policies;

import org.eclipse.gef.mvc.fx.policies.IFXOnClickPolicy;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IRootPart;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.policies.AbstractInteractionPolicy;
import org.eclipse.gef.mvc.policies.CreationPolicy;
import org.eclipse.gef.mvc.viewer.IViewer;

import com.google.common.collect.HashMultimap;
import com.google.common.reflect.TypeToken;
import com.itemis.gef.tutorial.mindmap.model.MindMapConnection;
import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel;
import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel.Type;
import com.itemis.gef.tutorial.mindmap.parts.MindMapNodePart;
import com.itemis.gef.tutorial.mindmap.parts.SimpleMindMapPart;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class CreateNewConnectiononClickPolicy extends AbstractInteractionPolicy<Node> implements IFXOnClickPolicy {

	@SuppressWarnings("serial")
	@Override
	public void click(MouseEvent e) {
		
		if (!e.isPrimaryButtonDown()) {
			return; 
		}
		
		IViewer<Node> viewer = getHost().getRoot().getViewer();
		ItemCreationModel creationModel = viewer.getAdapter(ItemCreationModel.class);
		if (creationModel.getType()!=Type.Connection) {
			return; // don't want to create a connection
		}
		
		if (creationModel.getSource()==null) {
			// the host is the source
			creationModel.setSource((MindMapNodePart) getHost());
			return; // wait for the next click
		}
		
		// okay, we have a pair
		MindMapNodePart source = creationModel.getSource();
		MindMapNodePart target = (MindMapNodePart) getHost();
		
		IVisualPart<Node, ? extends Node> part = getHost().getRoot().getChildrenUnmodifiable().get(0);
		
		if (part instanceof SimpleMindMapPart) {
			
			MindMapConnection newConn = new MindMapConnection();
			newConn.connect(source.getContent(), target.getContent());

			// GEF provides the CreatePolicy and operations to add a new element
			// to the model
			IRootPart<Node, ? extends Node> root = getHost().getRoot();
			// get the policy bound to the IRootPart
			CreationPolicy<Node> creationPolicy = root.getAdapter(new TypeToken<CreationPolicy<Node>>() {
			});
			// initialize the policy
			init(creationPolicy);
			// create a IContentPart for our new model. We don't use the
			// returned content-part
			creationPolicy.create(newConn, (SimpleMindMapPart) part,
					HashMultimap.<IContentPart<Node, ? extends Node>, String>create());
			// execute the creation
			commit(creationPolicy);
			
		}
		
		// finally reset creationModel
		creationModel.setSource(null);
		creationModel.setType(Type.None);
	}

}
