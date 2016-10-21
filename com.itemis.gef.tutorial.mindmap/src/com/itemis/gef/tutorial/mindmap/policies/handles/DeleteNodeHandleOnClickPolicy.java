package com.itemis.gef.tutorial.mindmap.policies.handles;

import java.util.ArrayList;
import java.util.Set;

import org.eclipse.gef.common.collections.ObservableSetMultimap;
import org.eclipse.gef.mvc.fx.policies.IFXOnClickPolicy;
import org.eclipse.gef.mvc.parts.IRootPart;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.policies.AbstractInteractionPolicy;
import org.eclipse.gef.mvc.policies.DeletionPolicy;

import com.google.common.reflect.TypeToken;
import com.itemis.gef.tutorial.mindmap.parts.MindMapConnectionPart;
import com.itemis.gef.tutorial.mindmap.parts.MindMapNodePart;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The click policy for the DeleteMindMapNodeHandlePart.
 * 
 * @author hniederhausen
 *
 */
public class DeleteNodeHandleOnClickPolicy extends AbstractInteractionPolicy<Node> implements IFXOnClickPolicy {

	@Override
	public void click(MouseEvent e) {
		if (!e.isPrimaryButtonDown()) {
			return;
		}

		MindMapNodePart node = getAnchoredPart();

		// delete the part
		IRootPart<Node, ? extends Node> root = getHost().getRoot();
		@SuppressWarnings("serial")
		DeletionPolicy<Node> delPolicy = root.getAdapter(new TypeToken<DeletionPolicy<Node>>() {
		});
		init(delPolicy);

		// get all achoreds and check if we have a connection part
		for (IVisualPart<Node, ? extends Node> a : new ArrayList<>(node.getAnchoredsUnmodifiable())) {
			if (a instanceof MindMapConnectionPart) {
				// now delete the parts (couldn't do it before, because of a
				// concurrent modification exception)
				delPolicy.delete((MindMapConnectionPart) a);
			}
		}

		// and finally remove the node part
		delPolicy.delete(node);
		commit(delPolicy);
	}

	/**
	 * Find the anchored part
	 * 
	 * @return
	 */
	protected MindMapNodePart getAnchoredPart() {
		// get the clicked handle part
		IVisualPart<Node, ? extends Node> host = getHost();

		// get the handle root part
		IVisualPart<Node, ? extends Node> rootPart = host.getParent();

		// get the anchorages of the root
		ObservableSetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = rootPart
				.getAnchoragesUnmodifiable();

		// retrieve the keyset containing the parts the root is anchored to
		Set<IVisualPart<Node, ? extends Node>> keySet = anchorages.keySet();

		// take the first (and only) one
		IVisualPart<Node, ? extends Node> key = keySet.iterator().next();

		// return the key aka MindMapNodePart
		return (MindMapNodePart) key;
	}

}
