package com.itemis.gef.tutorial.mindmap.policies;

import java.util.ArrayList;

import org.eclipse.gef.mvc.fx.policies.IFXOnClickPolicy;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IRootPart;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.policies.AbstractInteractionPolicy;
import org.eclipse.gef.mvc.policies.DeletionPolicy;

import com.google.common.reflect.TypeToken;
import com.itemis.gef.tutorial.mindmap.parts.MindMapConnectionPart;

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

	@SuppressWarnings("serial")
	@Override
	public void click(MouseEvent event) {
		if (!event.isSecondaryButtonDown()) {
			return; // only listen to secondary buttons
		}
		
		MenuItem deleteNodeItem = new MenuItem("Delete Node");
		deleteNodeItem.setOnAction((e) -> {
			IRootPart<Node, ? extends Node> root = getHost().getRoot();
			DeletionPolicy<Node> delPolicy = root.getAdapter(new TypeToken<DeletionPolicy<Node>>() {});
			init(delPolicy);
			
			// get all achoreds and check if we have a connection part
			for (IVisualPart<Node, ? extends Node> a : new ArrayList<>(getHost().getAnchoredsUnmodifiable()))  {
				if (a instanceof MindMapConnectionPart) {
					// now delete the parts (couldn't do it before, because of a concurrent modification exception)
					delPolicy.delete((IContentPart<Node, ? extends Node>) a);
				}
			}
			
			// and finally remove the node part
			delPolicy.delete((IContentPart<Node, ? extends Node>) getHost());
			commit(delPolicy);
		});
		
		ContextMenu ctxMenu = new ContextMenu(deleteNodeItem);
		// show the menu at the mouse position
		ctxMenu.show((Node) event.getTarget(), event.getScreenX(), event.getScreenY());
	}
}
