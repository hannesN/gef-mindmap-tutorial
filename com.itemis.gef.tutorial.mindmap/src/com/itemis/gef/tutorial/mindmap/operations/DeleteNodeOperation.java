package com.itemis.gef.tutorial.mindmap.operations;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.mvc.fx.models.FocusModel;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.operations.ChangeFocusOperation;
import org.eclipse.gef.mvc.fx.operations.ChangeSelectionOperation;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.common.collect.Lists;
import com.itemis.gef.tutorial.mindmap.model.MindMapConnection;
import com.itemis.gef.tutorial.mindmap.model.MindMapNode;
import com.itemis.gef.tutorial.mindmap.parts.MindMapNodePart;
import com.itemis.gef.tutorial.mindmap.parts.SimpleMindMapPart;

/**
 * This operation removes a node, all its connections and clears the {@link SelectionModel} and {@link FocusModel}.
 * 
 * @author hniederhausen
 *
 */
public class DeleteNodeOperation extends ReverseUndoCompositeOperation {

	public DeleteNodeOperation(SimpleMindMapPart parent, MindMapNodePart nodePart) {
		super("Delete Node");
		prepareOperation(parent, nodePart);
	}

	private void prepareOperation(SimpleMindMapPart parent, MindMapNodePart nodePart) {
		IViewer viewer = parent.getRoot().getViewer();
		
		// removing the selections and focus from, to be sure we don't delete any focused
		// elements
		add(new ChangeSelectionOperation(viewer, Collections.emptyList()));
		add(new ChangeFocusOperation(viewer, null));
		
		
		List<MindMapConnection> connections = Lists.newArrayList(nodePart.getContent().getIncomingConnections());
		connections.addAll(nodePart.getContent().getOutgoingConnections());

		for (MindMapConnection con : connections) {
			add(new DeleteConnectionOperation(parent, con));
		}
		
		add(new InternalDeleteNodeOperation(parent, nodePart.getContent()));
	}

	
	private class InternalDeleteNodeOperation extends AbstractOperation implements ITransactionalOperation {

		private final SimpleMindMapPart parent;
		private final MindMapNode node;
		
		private int childIdx;

		public InternalDeleteNodeOperation(SimpleMindMapPart parent, MindMapNode node) {
			super("Delete Node");
			this.parent = parent;
			this.node = node;
		}

		@Override
		public boolean isContentRelevant() {
			// deleting items from the model
			return true;
		}

		@Override
		public boolean isNoOp() {
			return false;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			childIdx = parent.getContentChildrenUnmodifiable().indexOf(node);
			parent.removeContentChild(node);
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			parent.addContentChild(node, childIdx);
			return Status.OK_STATUS;
		}
		
	}
	
}
