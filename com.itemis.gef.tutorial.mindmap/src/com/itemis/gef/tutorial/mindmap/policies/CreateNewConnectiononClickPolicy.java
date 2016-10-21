package com.itemis.gef.tutorial.mindmap.policies;

import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.AbstractInteractionPolicy;
import org.eclipse.gef.mvc.fx.policies.CreationPolicy;
import org.eclipse.gef.mvc.fx.policies.IOnClickPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.common.collect.HashMultimap;
import com.itemis.gef.tutorial.mindmap.model.MindMapConnection;
import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel;
import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel.Type;
import com.itemis.gef.tutorial.mindmap.parts.MindMapNodePart;
import com.itemis.gef.tutorial.mindmap.parts.SimpleMindMapPart;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class CreateNewConnectiononClickPolicy extends AbstractInteractionPolicy implements IOnClickPolicy {

	@Override
	public void click(MouseEvent e) {
		
		if (!e.isPrimaryButtonDown()) {
			return; 
		}
		
		IViewer viewer = getHost().getRoot().getViewer();
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
		
		IVisualPart<? extends Node> part = getHost().getRoot().getChildrenUnmodifiable().get(0);
		
		if (part instanceof SimpleMindMapPart) {
			
			MindMapConnection newConn = new MindMapConnection();
			newConn.connect(source.getContent(), target.getContent());

			// GEF provides the CreatePolicy and operations to add a new element
			// to the model
			IRootPart<? extends Node> root = getHost().getRoot();
			// get the policy bound to the IRootPart
			CreationPolicy creationPolicy = root.getAdapter(CreationPolicy.class);
			// initialize the policy
			init(creationPolicy);
			// create a IContentPart for our new model. We don't use the
			// returned content-part
			creationPolicy.create(newConn, (SimpleMindMapPart) part,
					HashMultimap.<IContentPart<? extends Node>, String>create());
			// execute the creation
			commit(creationPolicy);
			
		}
		
		// finally reset creationModel
		creationModel.setSource(null);
		creationModel.setType(Type.None);
	}

}
