# Step 13 - Deleting Nodes 2

In this tutorial, we add a button on the top right corner to the `MindMapNodeparts`, called a handle. We already saw handles, when adding the resize functionality. The squares in the corner are handles as well.

To create a handle, e need the following classes:
 
 * parts for the handle
 * a handle factory producing the parts
 * a on click policy to execute an operation
 
## The parts
 
 First we create the parts for the handles. We will have a root root part with a `VBox` visual, which is the container for our buttons.
 This part is called  `MindMapNodeHandleRootPart`.
 
 Here is the code:
 
 <script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step13_delete_handles/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/parts/handles/MindMapNodeHandleRootPart.java"></script>
	 
The root part creates `VBox` as visual, which will contain the images for our handles. The method `doRefreshVisual` get the anchored visual and calculates the position to be at the top tight corner of the anchored visual.

Before we create the `DeleteMindMapNodeHandlePart` we create an abstract class to be the base class for all our handles. This class task is to manage registering and unregistering the handle to the part. Without this functionality, the bound policies of our handles wouldn't be called. The code is copied from the GEF FX Logo example.

Here is the class:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step13_delete_handles/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/parts/handles/AbstractMindMapHandlePart.java"></script>
	
With those lines in the abstract class, the implementation of the takes only a few lines. Here is the code: 


<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step13_delete_handles/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/parts/handles/DeleteMindMapNodeHandlePart.java"></script>

	
The visual is a JavaFX `HoverOverlayImageView`. The images themselves are stored in the classpath. Create a new folder *images* in the project and add it to the build path, choosing *Build Path->Add to Build Path* in the context menu of the folder.

## The policy

Next we create the policy to delete a node. It is also a `IFXOnClickPolicy`.
The host of the policy would be the `DeleteMindMapNodeHandlePart`. To get the `MindMapNode` to delete we traverse to the anchored `MindMapNodePart`via the parent of our handle: the `MindMapHandleRootPart`. After retrieving the node we execute the `DeleteNodeOperation`.
 
Here is the code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step13_delete_handles/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/policies/handles/DeleteNodeHandleOnClickPolicy.java"></script>


## The factories

To show the handles on hover and on selection, we need to create a factory for each event. The `MvcFXModule` binds to default factories, which we will extend. 

The code is basically the same, the only difference the base class. So here is the code for the `MindMapNodeHoverHandlesFactory`.

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step13_delete_handles/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/parts/handles/MindMapNodeHoverHandlesFactory.java"></script>

First  `createHandleParts` of the super class is called, creating handles, like the rectangles to resize.  `createHandles` creates the  `MindMapNodeHandleRootPart` and adds the delete handle to its children list. 

The `MindMapNodeSelectionHandlesFactory` is identical. Just change the class declaration to:

```java
public class MindMapNodeSelectionHandlesFactory extends FXDefaultSelectionHandlePartFactory {
```
	
## Binding the factories and policy

As mentioned above the `MvcFXModue` already bind a hover and a selection factory. This is done in two methods: `bindSelectionHandlePartFactoryAsContentViewerAdapter` and `bindHoverHandlePartFactoryAsContentViewerAdapter`.
Overriding these bindings means overriding these methods.

Here are the overriding methods:
 
 ```java
@Override
protected void bindSelectionHandlePartFactoryAsContentViewerAdapter(
		MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
	// overriding default factory with our own
	AdapterKey<?> role = AdapterKey.role(SelectionBehavior.SELECTION_HANDLE_PART_FACTORY);
	adapterMapBinder.addBinding(role).to(MindMapNodeSelectionHandlesFactory.class);
}

@Override
protected void bindHoverHandlePartFactoryAsContentViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
	// overriding default factory with our own
	AdapterKey<?> role = AdapterKey.role(HoverBehavior.HOVER_HANDLE_PART_FACTORY);
	adapterMapBinder.addBinding(role).to(MindMapNodeHoverHandlesFactory.class);
}
```

Finally we need to bind the `DeleteNodeHandleOnClickPolicy` to the `DeleteMindMapNodeHandlePart`. Add another method to the module:

```java
protected void bindDeleteMindMapNodeHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
	adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(DeleteNodeHandleOnClickPolicy.class);
}
```
	
And call it in `configure`:

```java
@Override
protected void configure() {
	
	// old code
	
	bindDeleteMindMapNodeHandlePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), DeleteMindMapNodeHandlePart.class));
}
```
	
Now you can create new handles by adding them in the factories and bind the parts to a policy to execute new operations.
