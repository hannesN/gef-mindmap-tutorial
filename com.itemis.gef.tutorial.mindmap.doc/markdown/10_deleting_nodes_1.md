# Step 10 - Deleting Nodes 1

In this step we will create a context menu with an entry to delete a node. In addition we will create an operation to remove the node.

## The Context Menu

The context menu should appear on click of the secondary button. But only if we click on a MindMapNode. To accomplish that we create another on clock policy and bind it to the `MindMapNodePart`.

The code is mostly JavaFX-Code, which I won't explain in detail.

Here is the code of the policy:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/tree/step10_deleting_node/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/policies/ShowMindMapNodeContextMenuOnClickPolicy.java"></script>

We check if the secondary (usually right) mouse button is pressed and if so, show the context menu. If the *Delete Node* item is selected,
we instantiate `DeleteNodeOperation` and execute it.


Now we bind the policy to the MindMapNodeParts in the `SimpleMindMapModule`. Add the following to `bindMindMapNodePartAdapters`:

```java
// bind the context menu policy to the part
adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ShowMindMapNodeContextMenuOnClickPolicy.class);
```

## Delete Node Operations

The `DeleteNodeOperation` is a bit more complicated than the other operation. Because a node is the anchor of a connection, we need to delete all connections of the node as well. We do that by creating a composite operation.

A composite operation is a collection of operations, which are executed one after the other. The undo operation is either done in a reverse order (`ReverseUndoCompositeOperation`) or in the execution order (`ForwardUndoCompositeOperation`).
We want to delete all the connections and finally delete the node. But for undo we need the reverse order, first putting the node back and after that all the connections. If we would start with the connection, we wouldn't have a node to connect to.

The composite operation takes a list of operation. This means we need a operation to remove connections and another to remove the node. We could reuse the `DeleteConnectionOperation`, so we put it in the operations package. The operation to delete the node however is a private subclass of our composite. Only the composite operation should be public API.

Let's create the operations.

### Delete Connections Operation

This is again is a straight forward implementation. We get the `MindMapConnection` and the `SimpleMindMapPart` and disconnect the connection from its nodes and remove it from the part.

Here is the code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/tree/step10_deleting_node/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/operations/DeleteConnectionOperation.java"></script>

## Delete Node Operation

The Delete Node operation is a `ReverseUndoCompositeOperation`. The method `prepare` generates the list of operations to execute.
the first to operations change the `SelectionModel`and `FocusModel` to be sure, we don't have any feedback leftovers after removing the node.
   
After that for each connection, connected with the node, a `DeleteConnectionOperation`is instantiated. Finally we create the `InternalDeleteNodeOperation`which removed the node from the mind map.
   
Here is the code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/tree/step10_deleting_node/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/operations/DeleteNodeOperation.java"></script>
	
The `InternalDeleteNodeOperation` is almost identical to the `DeleteConnectionOperation`.

Start the application, you should be able to delete a node now.



