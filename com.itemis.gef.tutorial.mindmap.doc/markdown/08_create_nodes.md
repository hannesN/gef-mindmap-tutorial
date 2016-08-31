# Step 8 - Create new Nodes

We are able to move and to resize nodes now. What about creating new ones?

In this tutorial, we will add another button bar at the right side of the window and fill it with ToggleButtons. These is our tooling palette.

In this tutorial we will add a new kind of model to content viewer of our domain: a `ItemCreationModel`. This is the model to manage the creation states.

## The ItemCreationModel

First we create the `ItemCreationModel`. For now it only has one property. An enumeration of the type of MindMap item we want to create.

Right now we will only support MindMapNodes. please refer to the JavaFX documentation to get more information about the ObjectProperty-type.

	package com.itemis.gef.tutorial.mindmap.models;
	
	import javafx.beans.property.ObjectProperty;
	import javafx.beans.property.SimpleObjectProperty;
	
	public class ItemCreationModel {
	
		enum Type {
			None,
			Node
		};
		
		private ObjectProperty<Type> typeProperty = new SimpleObjectProperty<ItemCreationModel.Type>(Type.None);
	
		public ObjectProperty<Type> getTypeProperty() {
			return typeProperty;
		}
	
		public Type getType() {
			return typeProperty.getValue();
		}
	
		public void setType(Type type) {
			this.typeProperty.setValue(type);
		}
	
	}

Next we need to bind the model with the content viewer. Go to the `SimpleMindMapModul` and add the following methods:

	/**
	 * Binds the 
	 * @param adapterMapBinder
	 */
	protected void bindItemCreationModelAsContentViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		AdapterKey<ItemCreationModel> key = AdapterKey.get(ItemCreationModel.class);
		adapterMapBinder.addBinding(key).to(ItemCreationModel.class);
	}
	
	/**
	 * Scoping the ItemCreationModel in the FXViewer class
	 */
	protected void bindItemCreationModel() {
		binder().bind(ItemCreationModel.class).in(AdaptableScopes.typed(FXViewer.class));
	}
	
	
Now we need to call those methods. Add `bindItemCreationModel()` to the method `configure`.

`bindItemCreationModelAsContentViewerAdapter` should be called in the method `bindContentViewerAdapters`.
Add the following code to the module:

	@Override
	protected void bindContentViewerAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindContentViewerAdapters(adapterMapBinder);
		// bind the model to the content viewer
		bindItemCreationModelAsContentViewerAdapter(adapterMapBinder);
	}
	
Now you be able to get the `ItemCreationModel`  by `getContentViewer().getAdapter(ItemCreationModel.class)`.

## Create another Buttonbar

Again, we change the hookViwers method, by adding more buttons (well one for now). This time we use a ToggleButton which sets the type of the `ItemCreationModel` .
The new code is the following:

	private void hookViewers() {
		// creating parent pane for Canvas and button pane
		BorderPane pane = new BorderPane();
	
		pane.setTop(createButtonBar());
		pane.setCenter(getContentViewer().getCanvas());
		pane.setRight(createToolPalette());
	
		pane.setMinWidth(800);
		pane.setMinHeight(600);
		
		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);
	}
	
	private Node createToolPalette() {
		ItemCreationModel creationModel = getContentViewer().getAdapter(ItemCreationModel.class);
		
		MindMapNodeVisual graphic = new MindMapNodeVisual();
		graphic.setTitle("New Node");
		
		// the toggleGroup makes sure, we only select one 
		ToggleGroup toggleGroup = new ToggleGroup();
		
		ToggleButton createNode = new ToggleButton("", graphic);
		createNode.setToggleGroup(toggleGroup);
		createNode.selectedProperty().addListener((e, oldVal, newVal) -> {
			Type type =Type.None;
			if (newVal) {
				type = Type.Node;
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
				break;
			case None:
			default:
				// unselect the button
				toggleGroup.getSelectedToggle().setSelected(false);
				break;
			
			}
		});
		
		
		return new VBox(20, createNode);
	}
	
The second listener is unselecting the togglebutton, when the type is set to `Type.None`  in the `ItemCreationModel`.  

## CreateMindMapNodeOperation

Now we will create our first operation. Operations are atomic changes in the model, which can be undone.

We will create an operation, which creates a new Node and adds it to the mind map. This operation is then executed by a policy, which we will create in the next section.

For our operation, we need to inherit from the class `AbstractOperation` and in addition implement the interface  `ITransactionalOperation`.

Here is the code:

	package com.itemis.gef.tutorial.mindmap.operations;
	
	import org.eclipse.core.commands.ExecutionException;
	import org.eclipse.core.commands.operations.AbstractOperation;
	import org.eclipse.core.runtime.IAdaptable;
	import org.eclipse.core.runtime.IProgressMonitor;
	import org.eclipse.core.runtime.IStatus;
	import org.eclipse.core.runtime.Status;
	import org.eclipse.gef.geometry.planar.Rectangle;
	import org.eclipse.gef.mvc.operations.ITransactionalOperation;
	
	import com.itemis.gef.tutorial.mindmap.model.MindMapNode;
	import com.itemis.gef.tutorial.mindmap.parts.SimpleMindMapPart;
	
	import javafx.scene.paint.Color;
	
	public class CreateNodeOperation extends AbstractOperation implements ITransactionalOperation {
	
		private final SimpleMindMapPart part;
	
		private MindMapNode newNode;
		private double posX;
		private double posY;
	
		public CreateNodeOperation(SimpleMindMapPart part, double posX, double posY) {
			super("Create new MindMap Node");
			this.part = part;
			this.posX = posX;
			this.posY = posY;
		}
	
		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
	
			newNode = new MindMapNode();
			newNode.setTitle("New node");
			newNode.setDescription("no description");
			newNode.setColor(Color.GREENYELLOW);
			newNode.setBounds(new Rectangle(posX, posY, 50, 30));
	
			part.addContentChild(newNode, part.getContentChildrenUnmodifiable().size());
	
			return Status.OK_STATUS;
		}
	
		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			part.addContentChild(newNode, part.getContentChildrenUnmodifiable().size());
			return Status.OK_STATUS;
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
	
	
## CreateMindMapNodeOnClickPolicy

The `CreateMindMapNodeOnClickPolicy`will be bound to the root part, which is the `FXRootpart`. It implements the `IFXOnClickPolicy` and will be
notified whenever the user is clicking on an empty space. The policy checks, if the left mouse button was pressed, if so, whether the `ItemCreationModel` s type is node and if these conditions are met, we create a new node, find the `SimpleMindMapPart` in the child list of the root part and execute our create operation via the viewers domain.

Here is the code: 

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

	
The last step is, to bin this policy to the root edit part.

Go to the `SimpleMindMapModule` and add the following line to the method `bindContentViewerRootPartAdapters`

	adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CreateNewNodeOnClickPolicy.class);
	
That's it, now you should be able to create new nodes and also undo the creation.

The next tutorial will extend the ItemCreationModel to create Connections.
