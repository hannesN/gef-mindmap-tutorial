package com.itemis.gef.tutorial.mindmap.policies;

import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.AbstractInteractionPolicy;
import org.eclipse.gef.mvc.fx.policies.CreationPolicy;
import org.eclipse.gef.mvc.fx.policies.IOnClickPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.common.collect.HashMultimap;
import com.itemis.gef.tutorial.mindmap.model.MindMapNode;
import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel;
import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel.Type;
import com.itemis.gef.tutorial.mindmap.parts.SimpleMindMapPart;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * Policy, which listens to primary clicks and creates a new node if the {@link ItemCreationModel} is in the right state.
 * 
 * @author hniederhausen
 *
 */
public class CreateNewNodeOnClickPolicy extends AbstractInteractionPolicy implements IOnClickPolicy {

	@Override
	public void click(MouseEvent e) {
		if (!e.isPrimaryButtonDown()) {
			return; // wrong mouse button
		}

		IViewer viewer = getHost().getRoot().getViewer();
		ItemCreationModel creationModel = viewer.getAdapter(ItemCreationModel.class);
		if (creationModel == null) {
			throw new IllegalStateException("No ItemCreationModel bound to viewer!");
		}

		if (creationModel.getType() != Type.Node) {
			// don't want to create a node
			return;
		}
		IVisualPart<? extends Node> part = viewer.getRootPart().getChildrenUnmodifiable().get(0);
		
		if (part instanceof SimpleMindMapPart) {
			// calculate the mouse coordinates
			// determine coordinates of new nodes origin in model coordinates
			Point2D mouseInLocal = part.getVisual().sceneToLocal(e.getSceneX(), e.getSceneY());
	
			MindMapNode newNode = new MindMapNode();
			newNode.setTitle("New node");
			newNode.setDescription("no description");
			newNode.setColor(Color.GREENYELLOW);
			newNode.setBounds(new Rectangle(mouseInLocal.getX(), mouseInLocal.getY(), 50, 30));

			// GEF provides the CreatePolicy and operations to add a new element to the model
			IRootPart<? extends Node> root = getHost().getRoot();
			// get the policy bound to the IRootPart
			CreationPolicy creationPolicy = root.getAdapter(CreationPolicy.class);
			// initialize the policy
			init(creationPolicy);
			// create a IContentPart for our new model. We don't use the returned content-part 
			creationPolicy.create(newNode,
					(SimpleMindMapPart) part,
					HashMultimap.<IContentPart<? extends Node>, String> create());
			// execute the creation
			commit(creationPolicy);
		}
		
		// clear the creation selection
		creationModel.setType(Type.None);
		
	}
}
