package com.itemis.gef.tutorial.mindmap.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;

import com.itemis.gef.tutorial.mindmap.model.MindMapConnection;
import com.itemis.gef.tutorial.mindmap.model.MindMapNode;
import com.itemis.gef.tutorial.mindmap.parts.SimpleMindMapPart;

/**
 * This operation removes a connection from the given {@link SimpleMindMapPart}.
 * 
 * @author hniederhausen
 *
 */
public class DeleteConnectionOperation extends AbstractOperation implements ITransactionalOperation {

	private final MindMapConnection connection;
	private final MindMapNode source;
	private final MindMapNode target;
	private final SimpleMindMapPart parent;

	private int childIdx;

	public DeleteConnectionOperation(SimpleMindMapPart parent, MindMapConnection connection) {
		super("Delete Connection");
		this.connection = connection;
		this.source = connection.getSource();
		this.target = connection.getTarget();
		this.parent = parent;
	}

	@Override
	public boolean isContentRelevant() {
		// yes we are removing items from the model
		return true;
	}

	@Override
	public boolean isNoOp() {
		return false;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		connection.disconnect();
		// saving the index to put it back at the right position on undo
		childIdx = parent.getContentChildrenUnmodifiable().indexOf(connection);
		parent.removeContentChild(connection);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		connection.connect(source, target);
		parent.addContentChild(connection, childIdx);
		return Status.OK_STATUS;
	}
}
