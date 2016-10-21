package com.itemis.gef.tutorial.mindmap.policies.handles;

import java.util.ArrayList;
import java.util.Set;

import org.eclipse.gef.common.collections.ObservableSetMultimap;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.AbstractInteractionPolicy;
import org.eclipse.gef.mvc.fx.policies.DeletionPolicy;
import org.eclipse.gef.mvc.fx.policies.IOnClickPolicy;

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
public class DeleteNodeHandleOnClickPolicy extends AbstractInteractionPolicy implements IOnClickPolicy {

	@Override
	public void click(MouseEvent e) {
		if (!e.isPrimaryButtonDown()) {
			return;
		}

		MindMapNodePart node = getAnchoredPart();

		// delete the part
		IRootPart<? extends Node> root = getHost().getRoot();
		DeletionPolicy delPolicy = root.getAdapter(DeletionPolicy.class);

		init(delPolicy);

		// get all achoreds and check if we have a connection part
		for (IVisualPart<? extends Node> a : new ArrayList<>(node.getAnchoredsUnmodifiable())) {
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
		IVisualPart<? extends Node> host = getHost();

		// get the handle root part
		IVisualPart<? extends Node> rootPart = host.getParent();

		// get the anchorages of the root
		ObservableSetMultimap<IVisualPart<? extends Node>, String> anchorages = rootPart
				.getAnchoragesUnmodifiable();

		// retrieve the keyset containing the parts the root is anchored to
		Set<IVisualPart<? extends Node>> keySet = anchorages.keySet();

		// take the first (and only) one
		IVisualPart<? extends Node> key = keySet.iterator().next();

		// return the key aka MindMapNodePart
		return (MindMapNodePart) key;
	}

}
