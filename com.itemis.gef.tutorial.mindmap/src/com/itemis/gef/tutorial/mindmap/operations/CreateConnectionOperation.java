/**
 * 
 */
package com.itemis.gef.tutorial.mindmap.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.mvc.operations.ITransactionalOperation;

import com.itemis.gef.tutorial.mindmap.model.MindMapConnection;
import com.itemis.gef.tutorial.mindmap.parts.MindMapNodePart;
import com.itemis.gef.tutorial.mindmap.parts.SimpleMindMapPart;

/**
 * Operation to create a new connection and add it to a {@link SimpleMindMapPart}
 * 
 * @author hniederhausen
 *
 */
public class CreateConnectionOperation extends AbstractOperation implements ITransactionalOperation {

	private MindMapNodePart target;
	private MindMapNodePart source;
	private SimpleMindMapPart parent;
	private MindMapConnection newConn;

	public CreateConnectionOperation(SimpleMindMapPart parent, MindMapNodePart source, MindMapNodePart target) {
		super("Create new connection");

		this.parent = parent;
		this.source = source;
		this.target = target;

		this.newConn = new MindMapConnection();
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		newConn.connect(source.getContent(), target.getContent());
		parent.addContentChild(newConn, parent.getContentChildrenUnmodifiable().size());
		
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		parent.removeContentChild(newConn);
		return Status.OK_STATUS;
	}

	@Override
	public boolean isContentRelevant() {
		// changing the model
		return true;
	}

	@Override
	public boolean isNoOp() {
		// if source and target are the same, we don't create the node
		return source.equals(target);
	}

}
