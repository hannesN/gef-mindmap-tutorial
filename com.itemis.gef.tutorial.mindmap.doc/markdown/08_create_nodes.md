# Step 8 - Create new Nodes

We are able to move and to resize nodes now. What about creating new ones?

In this tutorial, we will add another button bar at the right side of the window and fill it with ToggleButtons. These is our tooling palette.

In this tutorial we will add a new kind of model to content viewer of our domain: a `ItemCreationModel`. This is the model to manage the creation states.

## The ItemCreationModel

First we create the `ItemCreationModel`. For now it only has one property. An enumeration of the type of MindMap item we want to create.

Right now we will only support MindMapNodes. please refer to the JavaFX documentation to get more information about the ObjectProperty-type.

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step8_create_nodes/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/models/ItemCreationModel.java"></script> 

Next we need to bind the model with the content viewer. Go to the `SimpleMindMapModul` and add the following methods:

```java
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
```
	
Now we need to call those methods. Add `bindItemCreationModel()` to the method `configure`.

`bindItemCreationModelAsContentViewerAdapter` should be called in the method `bindContentViewerAdapters`.
Add the following code to the module:

```java
@Override
protected void bindContentViewerAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
	super.bindContentViewerAdapters(adapterMapBinder);
	// bind the model to the content viewer
	bindItemCreationModelAsContentViewerAdapter(adapterMapBinder);
}
```
	
Now you be able to get the `ItemCreationModel`  by `getContentViewer().getAdapter(ItemCreationModel.class)`.

## Create another Buttonbar

Again, we change the hookViwers method, by adding more buttons (well one for now). This time we use a ToggleButton which sets the type of the `ItemCreationModel` .
The new code is the following:

```java
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
```
	
The second listener is unselecting the togglebutton, when the type is set to `Type.None`  in the `ItemCreationModel`.  

## CreateMindMapNodeOperation

Now we will create our first operation. Operations are atomic changes in the model, which can be undone.

We will create an operation, which creates a new Node and adds it to the mind map. This operation is then executed by a policy, which we will create in the next section.

For our operation, we need to inherit from the class `AbstractOperation` and in addition implement the interface  `ITransactionalOperation`.

Here is the code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step8_create_nodes/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/operations/CreateNodeOperation.java"></script> 
	
## CreateMindMapNodeOnClickPolicy

The `CreateMindMapNodeOnClickPolicy`will be bound to the root part, which is the `FXRootpart`. It implements the `IFXOnClickPolicy` and will be
notified whenever the user is clicking on an empty space. The policy checks, if the left mouse button was pressed, if so, whether the `ItemCreationModel` s type is node and if these conditions are met, we create a new node, find the `SimpleMindMapPart` in the child list of the root part and execute our create operation via the viewers domain.

Here is the code: 

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step8_create_nodes/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/policies/CreateNewNodeOnClickPolicy.java"></script> 
	
The last step is, to bin this policy to the root edit part.

Go to the `SimpleMindMapModule` and add the following line to the method `bindContentViewerRootPartAdapters`

```java
adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CreateNewNodeOnClickPolicy.class);
```
	
That's it, now you should be able to create new nodes and also undo the creation.

The next tutorial will extend the ItemCreationModel to create Connections.
