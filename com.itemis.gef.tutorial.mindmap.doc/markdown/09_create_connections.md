# Step 9 - Create Connections

Creating connection is similar to creating a new node, but not quite the same. The main difference is, while we could create new nodes by just one click, we need at least two clicks for the connection: one on the source node and one on the target.

This means we need an `IFXOnClickPolicy`  which stores the target of the click on the first click and creates the connection on the second click. But instead of adding a source attribute to the future `CreateConnectionOnClickPolicy` we will store the first selection in the `ItemCreationModel`.

## Extending the ItemCreationModel

We add another type to the enumeration: `Connection` and add a new property: `sourceProperty`.

Here is the new class:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step9_create_connections/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/models/ItemCreationModel.java"></script>

## CreateConnectionOperation

The implementation of the operation is straight forward. Again we extend from `AbstractOperation` and implement `ITransactionalOperation`.

Here is the whole code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step9_create_connections/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/operations/CreateConnectionOperation.java"></script>

## CreateConnectionOnClickPolicy

We also need a policy, which instantiates and executes the operation. This policy will be bound to the `MindMapNodePart` in the module.

Here is the code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step9_create_connections/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/policies/CreateNewNodeOnClickPolicy.java"></script>

Now we bind the policy to the MindMapNodePart in the method `bindMindMapNodePartAdapters`.
Add:
	
```java
// bind create connection policy
adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CreateNewConnectiononClickPolicy.class);
```
	
Finally, we need to add a Button to our tool palette. Just modify `createToolPalette` to look like this:

```java
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
```
	
I added some height settings, so the second button looks not completely lost.

That's it. Try it. You can now create connections. 