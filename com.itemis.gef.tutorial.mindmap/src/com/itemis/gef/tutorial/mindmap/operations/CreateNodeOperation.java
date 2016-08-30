package com.itemis.gef.tutorial.mindmap.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.mvc.operations.ITransactionalOperation;

import com.itemis.gef.tutorial.mindmap.model.MindMapNode;
import com.itemis.gef.tutorial.mindmap.parts.SimpleMindMapPart;

/**
 * The CreateNodeOperation creates a new node with default title and
 * description. it will be positioned at the location specified by constructor
 * parameters.
 * 
 * @author hniederhausen
 *
 */
public class CreateNodeOperation extends AbstractOperation implements ITransactionalOperation {

	private final SimpleMindMapPart part;

	private MindMapNode newNode;

	public CreateNodeOperation(SimpleMindMapPart part, MindMapNode newNode) {
		super("Create new MindMap Node");
		this.part = part;
		this.newNode = newNode;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		
		part.addContentChild(newNode, part.getContentChildrenUnmodifiable().size());

		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		part.removeContentChild(newNode);
		return Status.OK_STATUS;
	}

	@Override
	public boolean isContentRelevant() {
		// yes we change the model
		return true;
	}

	@Override
	public boolean isNoOp() {
		// can't happen
		return false;
	}
}
