package com.itemis.gef.tutorial.mindmap.policies;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.mvc.fx.policies.IFXOnClickPolicy;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.policies.AbstractInteractionPolicy;
import org.eclipse.gef.mvc.viewer.IViewer;

import com.itemis.gef.tutorial.mindmap.operations.DeleteNodeOperation;
import com.itemis.gef.tutorial.mindmap.parts.MindMapNodePart;
import com.itemis.gef.tutorial.mindmap.parts.SimpleMindMapPart;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

/**
 * This policy shows a context menu for MindMapNodeParts, providing some editing functionality.
 * 
 * @author hniederhausen
 *
 */
public class ShowMindMapNodeContextMenuOnClickPolicy extends AbstractInteractionPolicy<Node> implements IFXOnClickPolicy {

	@Override
	public void click(MouseEvent event) {
		if (!event.isSecondaryButtonDown()) {
			return; // only listen to secondary buttons
		}
		
		MenuItem deleteNodeItem = new MenuItem("Delete Node");
		deleteNodeItem.setOnAction((e) -> {
			// getting the SimpleMindMapPart
			IViewer<Node> viewer = getHost().getRoot().getViewer();
			IVisualPart<Node, ? extends Node> part = viewer.getRootPart().getChildrenUnmodifiable().get(0);
			
			if (part instanceof SimpleMindMapPart) {
				// Creating the operation and executing it in the domain
				try {
					DeleteNodeOperation op = new DeleteNodeOperation((SimpleMindMapPart) part, (MindMapNodePart) getHost());
					viewer.getDomain().execute(op, null);
				} catch (ExecutionException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		ContextMenu ctxMenu = new ContextMenu(deleteNodeItem);
		// show the menu at the mouse position
		ctxMenu.show((Node) event.getTarget(), event.getScreenX(), event.getScreenY());
	}
}
