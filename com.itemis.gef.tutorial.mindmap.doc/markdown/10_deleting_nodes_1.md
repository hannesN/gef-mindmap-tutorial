# Step 10 - Deleting Nodes 1

In this Tutorial we will create a context menu with an entry to delete a node. In addition we will create an operation to remove the node.

## The Context Menu

The context menu should appear on click of the secondary button. But only if we click on a MindMapNode. To accomplish that we create another on clock policy and bind it to the `MindMapNodePart`.

The code is mostly JavaFX-Code, which I won't explain in detail.

Here is the code of the policy:

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

We check if the secondary (usually right) mouse button is pressed and if so, show the context menu. If the *Delete Node* item is selected,
we instantiate `DeleteNodeOperation` and execute it.


Now we bind the policy to the MindMapNodeParts in the `SimpleMindMapModule`. Add the following to `bindMindMapNodePartAdapters`:

	// bind the context menu policy to the part
	adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ShowMindMapNodeContextMenuOnClickPolicy.class);

## Delete Node Operations

The `DeleteNodeOperation` is a bit more complicated than the other operation. Because a node is the anchor of a connection, we need to delete all connections of the node as well. We do that by creating a composite operation.

A composite operation is a collection of operations, which are executed one after the other. The undo operation is either done in a reverse order (`ReverseUndoCompositeOperation`) or in the execution order (`ForwardUndoCompositeOperation`).
We want to delete all the connections and finally delete the node. But for undo we need the reverse order, first putting the node back and after that all the connections. If we would start with the connection, we wouldn't have a node to connect to.

The composite operation takes a list of operation. This means we need a operation to remove connections and another to remove the node. We could reuse the `DeleteConnectionOperation`, so we put it in the operations package. The operation to delete the node however is a private subclass of our composite. Only the composite operation should be public API.

Let's create the operations.

### Delete Connections Operation

This is again is a straight forward implementation. We get the `MindMapConnection` and the `SimpleMindMapPart` and disconnect the connection from its nodes and remove it from the part.

Here is the code:

	package com.itemis.gef.tutorial.mindmap.operations;
	
	import org.eclipse.core.commands.ExecutionException;
	import org.eclipse.core.commands.operations.AbstractOperation;
	import org.eclipse.core.runtime.IAdaptable;
	import org.eclipse.core.runtime.IProgressMonitor;
	import org.eclipse.core.runtime.IStatus;
	import org.eclipse.core.runtime.Status;
	import org.eclipse.gef.mvc.operations.ITransactionalOperation;
	
	import com.itemis.gef.tutorial.mindmap.model.MindMapConnection;
	import com.itemis.gef.tutorial.mindmap.model.MindMapNode;
	import com.itemis.gef.tutorial.mindmap.parts.SimpleMindMapPart;
	
	public class DeleteConnectionOperation extends AbstractOperation implements ITransactionalOperation {
	
		private final MindMapConnection connection;
		private final MindMapNode source;
		private final MindMapNode target;
		private final SimpleMindMapPart parent;
		
		private int childIdx;
		
		public DeleteConnectionOperation(MindMapConnection connection, SimpleMindMapPart parent) {
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

## Delete Node Operation

The Delete Node operation is a `ReverseUndoCompositeOperation`. The method `prepare` generates the list of operations to execute.
the first to operations change the `SelectionModel`and `FocusModel` to be sure, we don't have any feedback leftovers after removing the node.
   
After that for each connection, connected with the node, a `DeleteConnectionOperation`is instantiated. Finally we create the `InternalDeleteNodeOperation`which removed the node from the mind map.
   
Here is the code:

	package com.itemis.gef.tutorial.mindmap.operations;
	
	import java.util.Collections;
	import java.util.List;
	
	import org.eclipse.core.commands.ExecutionException;
	import org.eclipse.core.commands.operations.AbstractOperation;
	import org.eclipse.core.runtime.IAdaptable;
	import org.eclipse.core.runtime.IProgressMonitor;
	import org.eclipse.core.runtime.IStatus;
	import org.eclipse.core.runtime.Status;
	import org.eclipse.gef.mvc.operations.ChangeFocusOperation;
	import org.eclipse.gef.mvc.operations.ChangeSelectionOperation;
	import org.eclipse.gef.mvc.operations.ITransactionalOperation;
	import org.eclipse.gef.mvc.operations.ReverseUndoCompositeOperation;
	import org.eclipse.gef.mvc.viewer.IViewer;
	
	import com.google.common.collect.Lists;
	import com.itemis.gef.tutorial.mindmap.model.MindMapConnection;
	import com.itemis.gef.tutorial.mindmap.model.MindMapNode;
	import com.itemis.gef.tutorial.mindmap.parts.MindMapNodePart;
	import com.itemis.gef.tutorial.mindmap.parts.SimpleMindMapPart;
	
	import javafx.scene.Node;
	
	public class DeleteNodeOperation extends ReverseUndoCompositeOperation {
	
		public DeleteNodeOperation(SimpleMindMapPart parent, MindMapNodePart nodePart) {
			super("Delete Node");
			prepareOperation(parent, nodePart);
		}
	
		private void prepareOperation(SimpleMindMapPart parent, MindMapNodePart nodePart) {
			IViewer<Node> viewer = parent.getRoot().getViewer();
			
			// removing the selections and focus from, to be sure we don't delete any focused
			// elements
			add(new ChangeSelectionOperation<Node>(viewer, Collections.emptyList()));
			add(new ChangeFocusOperation<>(viewer, null));
			
			
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


	
The `InternalDeleteNodeOperation` is almost identical to the `DeleteConnectionOperation`.

Start the application, you should be able to delete a node now.
In a future tutorial we will add a handle to the selection feedback, to delete the node on click.


