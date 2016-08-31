package com.itemis.gef.tutorial.mindmap.policies;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.domain.IDomain;
import org.eclipse.gef.mvc.fx.policies.IFXOnClickPolicy;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.policies.AbstractInteractionPolicy;
import org.eclipse.gef.mvc.viewer.IViewer;

import com.itemis.gef.tutorial.mindmap.model.MindMapNode;
import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel;
import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel.Type;
import com.itemis.gef.tutorial.mindmap.operations.CreateNodeOperation;
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
public class CreateNewNodeOnClickPolicy extends AbstractInteractionPolicy<Node> implements IFXOnClickPolicy {

	@Override
	public void click(MouseEvent e) {
		if (!e.isPrimaryButtonDown()) {
			return; // wrong mouse button
		}

		IViewer<Node> viewer = getHost().getRoot().getViewer();
		ItemCreationModel creationModel = viewer.getAdapter(ItemCreationModel.class);
		if (creationModel == null) {
			throw new IllegalStateException("No ItemCreationModel bound to viewer!");
		}

		if (creationModel.getType() != Type.Node) {
			// don't want to create a node
			return;
		}
		IVisualPart<Node, ? extends Node> part = viewer.getRootPart().getChildrenUnmodifiable().get(0);
		
		if (part instanceof SimpleMindMapPart) {
			// calculate the mouse coordinates
			// determine coordinates of new nodes origin in model coordinates
			Point2D mouseInLocal = part.getVisual().sceneToLocal(e.getSceneX(), e.getSceneY());
	
			MindMapNode newNode = new MindMapNode();
			newNode.setTitle("New node");
			newNode.setDescription("no description");
			newNode.setColor(Color.GREENYELLOW);
			newNode.setBounds(new Rectangle(mouseInLocal.getX(), mouseInLocal.getY(), 50, 30));

			IDomain<Node> domain = viewer.getDomain();
			try {
				domain.execute(new CreateNodeOperation((SimpleMindMapPart) part, newNode), null);
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
		}
		
		// clear the creation selection
		creationModel.setType(Type.None);
		
	}
}
