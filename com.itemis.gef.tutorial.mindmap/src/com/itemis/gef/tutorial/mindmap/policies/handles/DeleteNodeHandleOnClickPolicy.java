package com.itemis.gef.tutorial.mindmap.policies.handles;

import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.common.collections.ObservableSetMultimap;
import org.eclipse.gef.mvc.domain.IDomain;
import org.eclipse.gef.mvc.fx.policies.IFXOnClickPolicy;
import org.eclipse.gef.mvc.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.parts.IRootPart;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.policies.AbstractInteractionPolicy;
import org.eclipse.gef.mvc.viewer.IViewer;

import com.itemis.gef.tutorial.mindmap.operations.DeleteNodeOperation;
import com.itemis.gef.tutorial.mindmap.parts.MindMapNodePart;
import com.itemis.gef.tutorial.mindmap.parts.SimpleMindMapPart;

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

		IVisualPart<Node, ? extends Node> key = getAnchoredPart();

		IVisualPart<Node, ? extends Node> targetPart = key;
		if (targetPart instanceof MindMapNodePart) {
			// delete the part
			SimpleMindMapPart parent = (SimpleMindMapPart) targetPart.getParent();

			ITransactionalOperation op = new DeleteNodeOperation(parent, (MindMapNodePart) targetPart);

			try {

				IRootPart<Node, ? extends Node> root = targetPart.getRoot();
				IViewer<Node> viewer = root.getViewer();
				IDomain<Node> domain = viewer.getDomain();
				domain.execute(op, null);
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}

		}

	}

	/**
	 * Find the anchored part
	 * 
	 * @return
	 */
	protected IVisualPart<Node, ? extends Node> getAnchoredPart() {
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
		return key;
	}

}
