package com.itemis.gef.tutorial.mindmap.policies;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.mvc.fx.policies.IFXOnClickPolicy;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.policies.AbstractInteractionPolicy;
import org.eclipse.gef.mvc.viewer.IViewer;

import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel;
import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel.Type;
import com.itemis.gef.tutorial.mindmap.operations.CreateConnectionOperation;
import com.itemis.gef.tutorial.mindmap.parts.MindMapNodePart;
import com.itemis.gef.tutorial.mindmap.parts.SimpleMindMapPart;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class CreateNewConnectiononClickPolicy extends AbstractInteractionPolicy<Node> implements IFXOnClickPolicy {

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
			CreateConnectionOperation op = new CreateConnectionOperation((SimpleMindMapPart) part, source, target);
			try {
				viewer.getDomain().execute(op, null);
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
		}
		
		// finally reset creationModel
		creationModel.setSource(null);
		creationModel.setType(Type.None);
	}

}
