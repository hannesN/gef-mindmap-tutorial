# Step 5 - Models, Policies and Behaviors


## Models

GEF uses models to manage the changes in the editor. We already saw the ContentModel in the viewer class, to set our mind map to show.
Other models bound to the viewer are:

* `SelectionModel` used to store the selection in the graphical editor
* `HoverModel` used to store, which part the mouse is hovering above
* `FocusModel` used to store the current focused part

These models are bound in the `MvcFXModule`. Later we will create our own Models to create connections and nodes.



## Policies and Behaviors

Models, be it  the SimpleMindMap model or the models above are modified via polices and behaviors.

### Policies

Policies are changes in the model, which are wrapped in an undoable operation. Policies are passive, meaning they are triggered by tools in the interaction process, e.g. a mouse click.

### Behaviors

Behaviors change the model directly, without any transaction. They are actively listing for relevant changes and act accordingly.


## Add a hover and selection behavior to the Simple Mind Map Editor

GEF already provides implementations for hovering and selection feedbacks. We just need to add them to our module:

```java
@Override
protected void bindAbstractContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
	super.bindAbstractContentPartAdapters(adapterMapBinder);

	// binding the FXHoverOnHoverPolicy to every part
	// if a mouse is moving above a part it is set i the HoverModel
	adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXHoverOnHoverPolicy.class);

	// add the focus and select policy to every part, listening to clicks
	// and changing the focus and selection model
	adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXFocusAndSelectOnClickPolicy.class);
}
```

This bind the policies responsible to every instance of AbstractPath. The policies update the corresponding models.

```java
@Override
protected void bindContentViewerRootPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
	super.bindContentViewerRootPartAdapters(adapterMapBinder);

	// binding a Hover Behavior to the root part. it will react to
	// HoverModel changes and render the hover part
	adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXHoverBehavior.class);
}
```
The `FXHoverBehavior` listens to changes in the `HoverModel` and updates the graphical representation.

With these little changes you are able to select a node. In the next step we will move nodes.
  