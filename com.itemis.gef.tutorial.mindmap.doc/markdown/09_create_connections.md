# Step 9 - Create Connections

Creating connection is similar to creating a new node, but not quite the same. The main difference is, while we could create new nodes by just one click, we need at least two clicks for the connection: one on the source node and one on the target.

This means we need an IFXOnClickPolicy which stores the target of the click on the first click and creates the connection on the second click. But instead of adding a source attribute to the future `CreateConnectionOnClickPolicy` we will store the first selection in the `ItemCreationModel`.

## Extending the ItemCreationModel

We add another type to the enumeration: `Connection` and add a new property: `sourceProperty`.

Here is the new class:

	public class ItemCreationModel {
	
		public enum Type {
			None,
			Node,
			Connection
		};
		
		private ObjectProperty<Type> typeProperty = new SimpleObjectProperty<ItemCreationModel.Type>(Type.None);
	
		private ObjectProperty<MindMapNodePart> sourceProperty;
	
		public ObjectProperty<Type> getTypeProperty() {
			return typeProperty;
		}
	
		public Type getType() {
			return typeProperty.getValue();
		}
	
		public void setType(Type type) {
			this.typeProperty.setValue(type);
		}
	
		public void setSource(MindMapNodePart source) {
			this.sourceProperty.setValue(source);;
		}
		
		public MindMapNodePart getSource() {
			return sourceProperty.getValue();
		}
		
		public ObjectProperty<MindMapNodePart> getSourceProperty() {
			return sourceProperty;
		}
		
	}

## CreateConnectionOperation

The implementation of the operation is straight forward. Again we extend from `AbstractOperation` and implement `ITransactionalOperation`.

Here is the whole code:

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

## CreateConnectionOnClickPolicy

We also need a policy, which instantiates and executes the operation. This policy will be bound to the `MindMapNodePart` in the module.

here is the code:

	package com.itemis.gef.tutorial.mindmap.policies;
	
	import org.eclipse.core.commands.ExecutionException;
	import org.eclipse.gef.geometry.planar.Rectangle;
	import org.eclipse.gef.mvc.domain.IDomain;
	import org.eclipse.gef.mvc.fx.policies.IFXOnClickPolicy;
	import org.eclipse.gef.mvc.parts.IVisualPart;
	import org.eclipse.gef.mvc.policies.AbstractInteractionPolicy;
	import org.eclipse.gef.mvc.viewer.IViewer;
	
	import com.itemis.gef.tutorial.mindmap.model.MindMapNode;
	import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel;
	import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel.Type;
	import com.itemis.gef.tutorial.mindmap.operations.CreateNodeOperation;
	import com.itemis.gef.tutorial.mindmap.parts.SimpleMindMapPart;
	
	import javafx.geometry.Point2D;
	import javafx.scene.Node;
	import javafx.scene.input.MouseEvent;
	import javafx.scene.paint.Color;
	
	/**
	 * Policy, which listens to primary clicks and creates a new node if the {@link ItemCreationModel} is in the right state.
	 * 
	 * @author hniederhausen
	 *
	 */
	public class CreateNewNodeOnClickPolicy extends AbstractInteractionPolicy<Node> implements IFXOnClickPolicy {
	
		@Override
		public void click(MouseEvent e) {
			if (!e.isPrimaryButtonDown()) {
				return; // wrong mouse button
			}
	
			IViewer<Node> viewer = getHost().getRoot().getViewer();
			ItemCreationModel creationModel = viewer.getAdapter(ItemCreationModel.class);
			if (creationModel == null) {
				throw new IllegalStateException("No ItemCreationModel bound to viewer!");
			}
	
			if (creationModel.getType() != Type.Node) {
				// don't want to create a node
				return;
			}
			IVisualPart<Node, ? extends Node> part = viewer.getRootPart().getChildrenUnmodifiable().get(0);
			
			if (part instanceof SimpleMindMapPart) {
				// calculate the mouse coordinates
				// determine coordinates of new nodes origin in model coordinates
				Point2D mouseInLocal = part.getVisual().sceneToLocal(e.getSceneX(), e.getSceneY());
		
				MindMapNode newNode = new MindMapNode();
				newNode.setTitle("New node");
				newNode.setDescription("no description");
				newNode.setColor(Color.GREENYELLOW);
				newNode.setBounds(new Rectangle(mouseInLocal.getX(), mouseInLocal.getY(), 50, 30));
	
				IDomain<Node> domain = viewer.getDomain();
				try {
					domain.execute(new CreateNodeOperation((SimpleMindMapPart) part, newNode), null);
				} catch (ExecutionException e1) {
					e1.printStackTrace();
				}
			}
			
			// clear the creation selection
			creationModel.setType(Type.None);
			
		}
	}


Now we bind the policy to the MindMapNodePart in the method `bindMindMapNodePartAdapters`.
Add:
	
	// bind create connection policy
	adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CreateNewConnectiononClickPolicy.class);
	
	
Finally, we need to add a Button to our tool palette. Just modify `createToolPalette` to look like this:

	private Node createToolPalette() {
		ItemCreationModel creationModel = getContentViewer().getAdapter(ItemCreationModel.class);
			
		MindMapNodeVisual graphic = new MindMapNodeVisual();
		graphic.setTitle("New Node");
		
		// the toggleGroup makes sure, we only select one 
		ToggleGroup toggleGroup = new ToggleGroup();
		
		ToggleButton createNode = new ToggleButton("", graphic);
		createNode.setToggleGroup(toggleGroup);
		createNode.setMaxWidth(Double.MAX_VALUE);
		createNode.selectedProperty().addListener((e, oldVal, newVal) -> {
			Type type =Type.None;
			if (newVal) {
				type = Type.Node;
			}
			creationModel.setType(type);
		});
	
		ToggleButton createConn = new ToggleButton("New Connection");
		createConn.setToggleGroup(toggleGroup);
		createConn.setMaxWidth(Double.MAX_VALUE);
		createConn.setMinHeight(50);
		createConn.selectedProperty().addListener((e, oldVal, newVal) -> {
			Type type = Type.None;
			if (newVal) {
				type = Type.Connection;
			}
			creationModel.setType(type);
		});
	
		
		// now listen to changes in the model, and deactivate buttons, if necessary
		creationModel.getTypeProperty().addListener((e, oldVal, newVal) -> {
			if (oldVal==newVal) {
				return;
			}
			switch (newVal) {
			case Node:
			case Connection:
				break;
			case None:
			default:
				// unselect the button
				toggleGroup.getSelectedToggle().setSelected(false);
				break;
			
			}
		});
		
		return new VBox(20, createNode, createConn);
	}
	
Note that I added some height settings, so the second button looks not completely lost.

That's it. Try it. You can now create connections. 