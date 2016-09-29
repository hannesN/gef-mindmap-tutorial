package com.itemis.gef.tutorial.mindmap.policies;

import java.util.List;

import org.eclipse.gef.common.collections.ObservableMultiset;
import org.eclipse.gef.mvc.fx.policies.IFXOnClickPolicy;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IRootPart;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.policies.AbstractInteractionPolicy;
import org.eclipse.gef.mvc.policies.DeletionPolicy;
import org.eclipse.gef.mvc.viewer.IViewer;

import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
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
			delPolicy.init();
			
			// get all achoreds and check if we have a connection part
			List<MindMapConnectionPart> connectionPartList = Lists.newArrayList();
			for (IVisualPart<Node, ? extends Node> a : getHost().getAnchoredsUnmodifiable())  {
				if (a instanceof MindMapConnectionPart) {
					connectionPartList.add((MindMapConnectionPart) a);
				}
			}
			// now delete the parts (couldn't do it before, because of a concurrent modification exception)
			connectionPartList.forEach((c)->{
				delPolicy.delete(c);
			});
			
			// and finally remove the node part
			delPolicy.delete((IContentPart<Node, ? extends Node>) getHost());
			delPolicy.commit();
		});
		
		ContextMenu ctxMenu = new ContextMenu(deleteNodeItem);
		// show the menu at the mouse position
		ctxMenu.show((Node) event.getTarget(), event.getScreenX(), event.getScreenY());
	}
}
