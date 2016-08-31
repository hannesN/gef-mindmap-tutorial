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

	package com.itemis.gef.tutorial.mindmap.parts.feedback;
	
	import java.util.List;
	import java.util.Map;
	
	import org.eclipse.gef.mvc.behaviors.IBehavior;
	import org.eclipse.gef.mvc.parts.IFeedbackPart;
	import org.eclipse.gef.mvc.parts.IFeedbackPartFactory;
	import org.eclipse.gef.mvc.parts.IVisualPart;
	
	import com.google.common.collect.Lists;
	import com.google.inject.Inject;
	import com.google.inject.Injector;
	import com.itemis.gef.tutorial.mindmap.parts.MindMapNodePart;
	
	import javafx.scene.Node;
	
	public class CreateFeedbackPartFactory implements IFeedbackPartFactory<Node> {
	
		@Inject
		Injector injector;
	
		@Override
		public List<IFeedbackPart<Node, ? extends Node>> createFeedbackParts(
				List<? extends IVisualPart<Node, ? extends Node>> targets, IBehavior<Node> contextBehavior,
				Map<Object, Object> contextMap) {
	
			List<IFeedbackPart<Node, ? extends Node>> parts = Lists.newArrayList();
	
			if (targets.isEmpty())
				return parts; // shouldn't happen, just to be sure
	
			// we just expect one target
			IVisualPart<Node, ? extends Node> target = targets.get(0);
	
			if (target instanceof MindMapNodePart) {
				// a MindMapNode target is the source of a connection so we create
				// the connection feedback
				CreateConnectionFeedbackPart part = injector.getInstance(CreateConnectionFeedbackPart.class);
				parts.add(part);
			}
	
			return parts;
		}
	}

The factory expects a `MindMapNodePart` - the source of our connection and creates a `ConnectionFeedBackPart`.

## ConnectionFeedBackPart

The `ConnectionFeedBackPart`is responsible to manage the feedbacks visual and anchors. We create a MindMapConnectionVisual and anchor it to the source part lie we do in the `MindMapConnectionPart`. The end point however is positioned by a newly created anchor: the `MousePositionAnchor`.

Here is the code:

	package com.itemis.gef.tutorial.mindmap.parts.feedback;
	
	import org.eclipse.gef.common.adapt.AdapterKey;
	import org.eclipse.gef.fx.anchors.IAnchor;
	import org.eclipse.gef.fx.anchors.StaticAnchor;
	import org.eclipse.gef.geometry.planar.Point;
	import org.eclipse.gef.mvc.parts.AbstractFeedbackPart;
	import org.eclipse.gef.mvc.parts.IVisualPart;
	
	import com.google.common.reflect.TypeToken;
	import com.google.inject.Provider;
	import com.itemis.gef.tutorial.mindmap.visuals.MindMapConnectionVisual;
	
	import javafx.event.EventHandler;
	import javafx.scene.Node;
	import javafx.scene.input.MouseEvent;
	
	public class CreateConnectionFeedbackPart extends AbstractFeedbackPart<Node, Node> {
	
		@Override
		protected Node createVisual() {
			return new MindMapConnectionVisual();
		}
	
		@Override
		protected void doRefreshVisual(Node visual) {
		}
	
		@Override
		public void attachToAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
			// find a anchor provider, which must be registered in the module
			// be aware to use the right interfaces (Proviser is used a lot)
			@SuppressWarnings("serial")
			Provider<? extends IAnchor> adapter = anchorage
					.getAdapter(AdapterKey.get(new TypeToken<Provider<? extends IAnchor>>() {
					}));
			if (adapter == null) {
				throw new IllegalStateException("No adapter  found for <" + anchorage.getClass() + "> found.");
			}
			// set the start anchor
			IAnchor anchor = adapter.get();
			getVisual().setStartAnchor(anchor);
	
			MousePositionAnchor endAnchor = new MousePositionAnchor(new Point(0, 0));
			endAnchor.init();
			getVisual().setEndAnchor(endAnchor);
	
		}
		
		@Override
		protected void detachFromAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
			getVisual().setStartPoint(getVisual().getStartPoint());
			((MousePositionAnchor) getVisual().getEndAnchor()).dispose();
			getVisual().setEndPoint(getVisual().getEndPoint());
		}
		
		@Override
		public MindMapConnectionVisual getVisual() {
			return (MindMapConnectionVisual) super.getVisual();
		}
		
		private class MousePositionAnchor extends StaticAnchor implements EventHandler<MouseEvent>{
	
			public MousePositionAnchor(Point referencePositionInScene) {
				super(referencePositionInScene);
			}
			
			public void init() {
				// listen to any mouse move and reposition the anchor
				getRoot().getVisual().getScene().addEventHandler(MouseEvent.MOUSE_MOVED, this);
			}
			
			@Override
			public void handle(MouseEvent event) {
				Point v = new Point(event.getSceneX(), event.getSceneY());
				referencePositionProperty().setValue(v);
			}
			
			public void dispose() {
				// listen to any mouse move and reposition the anchor
				getRoot().getVisual().getScene().removeEventHandler(MouseEvent.MOUSE_MOVED, this);
			}
			
		}
	}

As you can see, the `MousePositionAnchor` is a subclass of StaticAnchor, which has a fixed position. We update this position based on the mouse position, using an event listener. If the feedback is deactivated, the anchorages are detached and fixed positions will be set.

## The CreateFeedbackBehavior

A Behavior is a direct reaction to changes in a model, without triggering a operation. This is ideal for our feedback.

We create a class called `CreateFeedbackBehavior`.

First, here is the code:

	package com.itemis.gef.tutorial.mindmap.behaviors;
	
	import org.eclipse.gef.mvc.behaviors.AbstractBehavior;
	import org.eclipse.gef.mvc.parts.IFeedbackPartFactory;
	import org.eclipse.gef.mvc.viewer.IViewer;
	
	import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel;
	
	import javafx.scene.Node;
	
	public class CreateFeedbackBehavior extends AbstractBehavior<Node> {
	
		/**
		 * The adapter role for the {@link IFeedbackPartFactory} that is used to
		 * generate hover feedback parts.
		 */
		public static final String CREATE_FEEDBACK_PART_FACTORY = "CREATE_FEEDBACK_PART_FACTORY";
	
		@Override
		protected void doActivate() {
	
			ItemCreationModel model = getHost().getRoot().getViewer().getAdapter(ItemCreationModel.class);
			model.getSourceProperty().addListener((o, oldVal, newVal) -> {
				if (newVal == null) {
					clearFeedback(); // no source set, so no feedback
				} else {
					addFeedback(newVal); // we have source, start the feedback
				}
			});
	
			super.doActivate();
		}
	
		@Override
		protected void clearFeedback() {
			// TODO Auto-generated method stub
			super.clearFeedback();
		}
	
		@Override
		protected IFeedbackPartFactory<Node> getFeedbackPartFactory(IViewer<Node> viewer) {
			return getFeedbackPartFactory(viewer, CREATE_FEEDBACK_PART_FACTORY);
		}
	
	}
	
## Putting it all together


Now we need to bind the factory and the behavior.

The behavior needs to be bound at the root part. So add the following lines to `bindContentViewerRootPartAdapters`

	// adding the creation feedback behavior
	adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CreateFeedbackBehavior.class);

We also need to bind the bind the factory to the viewer, using the role in our behavior. Add the following to: `bindContentViewerAdapters`:

	// binding the creation feedback part factory using the role, we are using in the behavior
	AdapterKey<?> role = AdapterKey.role(CreateFeedbackBehavior.CREATE_FEEDBACK_PART_FACTORY);
	adapterMapBinder.addBinding(role).to(CreateFeedbackPartFactory.class);

