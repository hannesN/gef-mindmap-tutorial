# Step 6 - Moving and Resizing a node

## Move a node

To move a node, we need two policies, already provided by GEF. open the `SimpleMindMapModule` and add
the following two lines to `bindMindMapNodePartAdapters`

```java
adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXTransformPolicy.class);
adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXTranslateSelectedOnDragPolicy.class);
```

The first policy manages transformations of the part in general. Keep in mind, that some transformations, like rotation are dependent on
the order of execution. The `FXTransformPolicy`takes care of that. In fact it is used by the second policy.
The `FXTranslateSelectedOnDragPolicy` listens to drag events on the bound part, and if the mouse buttons is released, executes an operation, which stores the translation.

You can start the application and already move the node around. However, it will snap back to it's original position as soon you release the mouse button and move the mouse to another element. The changes in the HoverModel trigger a refresh and the part will use the position in the parts content. What we need to do is store the new position in our `MindMapNode`.

this can be implemented easily. We extend the MindMapNodePart, by implementing the interface: `ITransformableContentPart<Node, MindMapNodeVisual>`. This adds one new method to the class. It looks like this:

```java
public class MindMapNodePart extends AbstractFXContentPart<MindMapNodeVisual> implements  ITransformableContentPart<Node, MindMapNodeVisual> {

    // old existing code
	
	@Override
	public void transformContent(AffineTransform transform) {
		// storing the  new position
		Rectangle bounds = getContent().getBounds();
		bounds = bounds.getTranslated(transform.getTranslateX(), transform.getTranslateY());
		getContent().setBounds(bounds);
	}
} 
```

That is actually all. the `FXTranslateSelectedOnDragPolicy` checks whether its host (the `MindMapNodePart` ) is implementing the `ITransformableContentPart` interface and if so
executes the method.


## Resize a node

Similar to moving a node around, we can resize a node.

But we need to create our own resize policy. We inherit from `org.eclipse.gef.mvc.fx.policies.FXResizePolicy` and override only one method. here is the code:

```java
package com.itemis.gef.tutorial.mindmap.policies;

import org.eclipse.gef.mvc.fx.policies.FXResizePolicy;
import com.itemis.gef.tutorial.mindmap.visuals.MindMapNodeVisual;
import javafx.scene.Node;

public class SimpleMindMapResizePolicy extends FXResizePolicy {

	@Override
	protected Node getVisualToResize() {
		MindMapNodeVisual visualToResize = (MindMapNodeVisual) super.getVisualToResize();
		return visualToResize.getShape();
	}
}
```

The MindMapNodeVisual itself is not resizable, the shape (the rounded rectangle) is, so we return this, instead of the visual.
In addition to the ResizePolicy, we need a way, to tell GEF to create the handles, we want to drag, to resize the node.
This is done via a `FXDefaultSelectionHandlePartFactory`which is already bound in the `MvcFXModul`. But this factory needs a
geometry provider, to position the handles. We use a `ShapeBoundsProvider` and bind it in addition to the  `MindMapResizePolicy`.

Here is the code: 

```java
/**
 * 
 * @param adapterMapBinder
 */
protected void bindMindMapNodePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
	
	// old code

	// specify the factory to create the geometry object for the selection handles
	role = AdapterKey.role(FXDefaultSelectionHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER);
	adapterMapBinder.addBinding(role).to(ShapeBoundsProvider.class);

	// bind the resize policy to the MindMapNodePart
	adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(SimpleMindMapResizePolicy.class);

}
```

If you start the application, you will see the handles at the corner of the node outline, but still won't be able to drag the little rectangles.
The rectangles are themselves GEF parts and the need their own policy to recognize a drag event. We bind the `FXResizeTranslateFirstAnchorageOnHandleDragPolicy` to them.

Add the following code to the module:

```java	
@Override
protected void configure() {
	// start the default configuration
	super.configure();

	bindMindMapNodePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), MindMapNodePart.class));

	// with this binding we create the handles
	bindFXSquareSegmentHandlePartPartAdapter(AdapterMaps.getAdapterMapBinder(binder(), FXSquareSegmentHandlePart.class));
}


/**
 * Binds the parts of the selection handles (the squares in the corner) to policies
 * @param adapterMapBinder
 */
protected void bindFXSquareSegmentHandlePartPartAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
	// 
	adapterMapBinder.addBinding(AdapterKey.defaultRole())
			.to(FXResizeTranslateFirstAnchorageOnHandleDragPolicy.class);
}
```

Now start the application. You will be able to drag a handle, but the size will be resetting, like the translation did.

To store the new size to the model, we need to implement another interface in the MindMapNodePart.

Here is the code:

```java
	public class MindMapNodePart extends AbstractFXContentPart<MindMapNodeVisual> 
											implements  ITransformableContentPart<Node, MindMapNodeVisual>, 
														IResizableContentPart<Node, MindMapNodeVisual> {
	
		// old code
	
		@Override
		public void resizeContent(Dimension size) {
			getContent().getBounds().setSize(size);		
		}
														
	}
```
	
With these few lines the new size will be stored in the model.