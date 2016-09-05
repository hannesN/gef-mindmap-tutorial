# Step 12 - Create Feedback

Right now, when we have chosen a creation tool in our palette, we don't see, what we are creating.

Especially when creating a connection you easily can forget, what node you just clicked.

In this tutorial, we will create a behavior, which listens to changes in our `ItemCreationModel` and generates a visual feedback.

To implement the behavior, we need:

* The behavior class
* A part representing the feedback element
* A part factory which creates the feedback parts

And of course we need to bind the classes to the correct elements.


## The FeedbackpartFactory

Let's begin with the feedback part factory.

First, here is the code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step12_creation_feedback/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/parts/feedback/CreateFeedbackPartFactory.java"></script>


The factory expects a `MindMapNodePart` - the source of our connection and creates a `ConnectionFeedBackPart`.

## ConnectionFeedBackPart

The `ConnectionFeedBackPart`is responsible to manage the feedbacks visual and anchors. We create a MindMapConnectionVisual and anchor it to the source part lie we do in the `MindMapConnectionPart`. The end point however is positioned by a newly created anchor: the `MousePositionAnchor`.

Here is the code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step12_creation_feedback/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/parts/feedback/CreateConnectionFeedbackPart.java"></script>

As you can see, the `MousePositionAnchor` is a subclass of StaticAnchor, which has a fixed position. We update this position based on the mouse position, using an event listener. If the feedback is deactivated, the anchorages are detached and fixed positions will be set.

## The CreateFeedbackBehavior

A Behavior is a direct reaction to changes in a model, without triggering a operation. This is ideal for our feedback.

We create a class called `CreateFeedbackBehavior`.

First, here is the code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step12_creation_feedback/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/behaviors/CreateFeedbackBehavior.java"></script>
	
## Putting it all together


Now we need to bind the factory and the behavior.

The behavior needs to be bound at the root part. So add the following lines to `bindContentViewerRootPartAdapters`

```java
// adding the creation feedback behavior
adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CreateFeedbackBehavior.class);
```
We also need to bind the bind the factory to the viewer, using the role in our behavior. Add the following to: `bindContentViewerAdapters`:

```java
// binding the creation feedback part factory using the role, we are using in the behavior
AdapterKey<?> role = AdapterKey.role(CreateFeedbackBehavior.CREATE_FEEDBACK_PART_FACTORY);
adapterMapBinder.addBinding(role).to(CreateFeedbackPartFactory.class);
```

