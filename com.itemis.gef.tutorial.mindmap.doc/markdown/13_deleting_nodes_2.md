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
 
	package com.itemis.gef.tutorial.mindmap.parts.handles;
	
	import org.eclipse.gef.mvc.fx.parts.AbstractFXHandlePart;
	import org.eclipse.gef.mvc.parts.IVisualPart;
	
	import com.google.common.collect.SetMultimap;
	import com.itemis.gef.tutorial.mindmap.parts.MindMapNodePart;
	
	import javafx.geometry.Bounds;
	import javafx.geometry.Point2D;
	import javafx.scene.Node;
	import javafx.scene.Parent;
	import javafx.scene.layout.VBox;
	
	public class MindMapNodeHandleRootPart extends AbstractFXHandlePart<VBox> {
	
		@Override
		protected VBox createVisual() {
			VBox vBox = new VBox();
	
			vBox.setPickOnBounds(true);
			return vBox;
		}
	
		@Override
		protected void doRefreshVisual(VBox visual) {
			// check if we have a host
			SetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = getAnchoragesUnmodifiable();
			if (anchorages.isEmpty()) {
				return;
			}
	
			IVisualPart<Node, ? extends Node> anchorage = anchorages.keys().iterator().next();
			Node hostVisual = anchorage.getVisual();
		
			// we have the visual, position next to it
			Bounds hostBounds = hostVisual.getBoundsInParent();
			Parent parent = hostVisual.getParent();
			if (parent != null) {
				hostBounds = parent.localToScene(hostBounds);
			}
			Point2D location = getVisual().getParent().sceneToLocal(hostBounds.getMaxX(), hostBounds.getMinY());
			getVisual().setLayoutX(location.getX());
			getVisual().setLayoutY(location.getY());
		}
	
		@Override
		protected void addChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
			getVisual().getChildren().add(index, child.getVisual());
		}
	
		@Override
		protected void removeChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
			getVisual().getChildren().remove(index);
		}
	}

	 
The root part creates `VBox` as visual, which will contain the images for our handles. The method `doRefreshVisual` get the anchored visual and calculates the position to be at the top tight corner of the anchored visual.

Before we create the `DeleteMindMapNodeHandlePart` we create an abstract class to be the base class for all our handles. This class task is to manage registering and unregistering the handle to the part. Without this functionality, the bound policies of our handles wouldn't be called. The code is copied from the GEF FX Logo example.

Here is the class:

	package com.itemis.gef.tutorial.mindmap.parts.handles;
	
	import java.util.Set;
	
	import org.eclipse.gef.common.collections.SetMultimapChangeListener;
	import org.eclipse.gef.mvc.fx.parts.AbstractFXHandlePart;
	import org.eclipse.gef.mvc.parts.IVisualPart;
	import org.eclipse.gef.mvc.viewer.IViewer;
	
	import javafx.scene.Node;
	
	public abstract class AbstractMindMapHandlePart<T extends Node> extends AbstractFXHandlePart<T> {
	
		private boolean registered = false;
		
		private final SetMultimapChangeListener<IVisualPart<Node, ? extends Node>, String> parentAnchoragesChangeListener = new SetMultimapChangeListener<IVisualPart<Node, ? extends Node>, String>() {
	
			private IViewer<Node> getViewer(Set<? extends IVisualPart<Node, ? extends Node>> anchorages) {
				for (IVisualPart<Node, ? extends Node> anchorage : anchorages) {
					if (anchorage.getRoot() != null && anchorage.getRoot().getViewer() != null) {
						return anchorage.getRoot().getViewer();
					}
				}
				return null;
			}
			@Override
			public void onChanged(
					org.eclipse.gef.common.collections.SetMultimapChangeListener.Change<? extends IVisualPart<Node, ? extends Node>, ? extends String> change) {
				IViewer<Node> oldViewer = getViewer(change.getPreviousContents().keySet());
				IViewer<Node> newViewer = getViewer(change.getSetMultimap().keySet());
				if (registered && oldViewer != null && oldViewer != newViewer) {
					unregister(oldViewer);
				}
				if (!registered && newViewer != null && oldViewer != newViewer) {
					register(newViewer);
				}
			}
		};
	
		public AbstractMindMapHandlePart() {
			super();
		}
	
		@Override
		protected void register(IViewer<Node> viewer) {
			if (registered) {
				return;
			}
			super.register(viewer);
			registered = true;
		}
	
		@Override
		public void setParent(IVisualPart<Node, ? extends Node> newParent) {
			if (getParent() != null) {
				getParent().getAnchoragesUnmodifiable().removeListener(parentAnchoragesChangeListener);
			}
			if (newParent != null) {
				newParent.getAnchoragesUnmodifiable().addListener(parentAnchoragesChangeListener);
			}
			super.setParent(newParent);
		}
	
		@Override
		protected void unregister(IViewer<Node> viewer) {
			if (!registered) {
				return;
			}
			super.unregister(viewer);
			registered = false;
		}
	
		@Override
		protected void doRefreshVisual(Node visual) {
			// nothing to do
		}
	
	}
	
	
With those lines in the abstract class, the implementation of the takes only a few lines. Here is the code: 

	package com.itemis.gef.tutorial.mindmap.parts.handles;
	
	import java.net.URL;
	
	import org.eclipse.gef.fx.nodes.HoverOverlayImageView;
	
	import javafx.scene.image.Image;
	
	public class DeleteMindMapNodeHandlePart extends AbstractMindMapHandlePart<HoverOverlayImageView> {
	
		public static final String IMG_DELETE = "/delete_obj.gif";
		public static final String IMG_DELETE_DISABLED = "/delete_obj_disabled.gif";
	
		
		@Override
		protected HoverOverlayImageView createVisual() {
			URL overlayImageResource = DeleteMindMapNodeHandlePart.class.getResource(IMG_DELETE);
			if (overlayImageResource == null) {
				throw new IllegalStateException("Cannot find resource <" + IMG_DELETE + ">.");
			}
			Image overlayImage = new Image(overlayImageResource.toExternalForm());
	
			URL baseImageResource = DeleteMindMapNodeHandlePart.class.getResource(IMG_DELETE_DISABLED);
			if (baseImageResource == null) {
				throw new IllegalStateException("Cannot find resource <" + IMG_DELETE_DISABLED + ">.");
			}
			Image baseImage = new Image(baseImageResource.toExternalForm());
	
			HoverOverlayImageView blendImageView = new HoverOverlayImageView();
			blendImageView.baseImageProperty().set(baseImage);
			blendImageView.overlayImageProperty().set(overlayImage);
			return blendImageView;
		}
	
	}

	
The visual is a JavaFX `HoverOverlayImageView`. The images themselves are stored in the classpath. Create a new folder *images* in the project and add it to the build path, choosing *Build Path->Add to Build Path* in the context menu of the folder.

## The policy

Next we create the policy to delete a node. It is also a `IFXOnClickPolicy`.
The host of the policy would be the `DeleteMindMapNodeHandlePart`. To get the `MindMapNode` to delete we traverse to the anchored `MindMapNodePart`via the parent of our handle: the `MindMapHandleRootPart`. After retrieving the node we execute the `DeleteNodeOperation`.
 
Here is the code:

	package com.itemis.gef.tutorial.mindmap.policies.handles;
	
	import java.util.Set;
	
	import org.eclipse.core.commands.ExecutionException;
	import org.eclipse.gef.common.collections.ObservableSetMultimap;
	import org.eclipse.gef.mvc.domain.IDomain;
	import org.eclipse.gef.mvc.fx.policies.IFXOnClickPolicy;
	import org.eclipse.gef.mvc.operations.ITransactionalOperation;
	import org.eclipse.gef.mvc.parts.IRootPart;
	import org.eclipse.gef.mvc.parts.IVisualPart;
	import org.eclipse.gef.mvc.policies.AbstractInteractionPolicy;
	import org.eclipse.gef.mvc.viewer.IViewer;
	
	import com.itemis.gef.tutorial.mindmap.operations.DeleteNodeOperation;
	import com.itemis.gef.tutorial.mindmap.parts.MindMapNodePart;
	import com.itemis.gef.tutorial.mindmap.parts.SimpleMindMapPart;
	
	import javafx.scene.Node;
	import javafx.scene.input.MouseEvent;
	
	public class DeleteNodeHandleOnClickPolicy extends AbstractInteractionPolicy<Node> implements IFXOnClickPolicy {
	
		@Override
		public void click(MouseEvent e) {
	
			if (!e.isPrimaryButtonDown()) {
				return;
			}
	
			IVisualPart<Node, ? extends Node> key = getAnchoredPart();
	
			IVisualPart<Node, ? extends Node> targetPart = key;
			if (targetPart instanceof MindMapNodePart) {
				// delete the part
				SimpleMindMapPart parent = (SimpleMindMapPart) targetPart.getParent();
	
				ITransactionalOperation op = new DeleteNodeOperation(parent, (MindMapNodePart) targetPart);
	
				try {
	
					IRootPart<Node, ? extends Node> root = targetPart.getRoot();
					IViewer<Node> viewer = root.getViewer();
					IDomain<Node> domain = viewer.getDomain();
					domain.execute(op, null);
				} catch (ExecutionException e1) {
					e1.printStackTrace();
				}
			}
		}
	
		protected IVisualPart<Node, ? extends Node> getAnchoredPart() {
			// get the clicked handle part
			IVisualPart<Node, ? extends Node> host = getHost();
	
			// get the handle root part
			IVisualPart<Node, ? extends Node> rootPart = host.getParent();
	
			// get the anchorages of the root
			ObservableSetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = rootPart
					.getAnchoragesUnmodifiable();
	
			// retrieve the keyset containing the parts the root is anchored to
			Set<IVisualPart<Node, ? extends Node>> keySet = anchorages.keySet();
	
			// take the first (and only) one
			IVisualPart<Node, ? extends Node> key = keySet.iterator().next();
	
			// return the key aka MindMapNodePart
			return key;
		}
	
	}
	
## The factories

To show the handles on hover and on selection, we need to create a factory for each event. The `MvcFXModule` binds to default factories, which we will extend. 

The code is basically the same, the only difference the base class. So here is the code for the `MindMapNodeHoverHandlesFactory`.

	package com.itemis.gef.tutorial.mindmap.parts.handles;
	
	import java.util.ArrayList;
	import java.util.List;
	import java.util.Map;
	
	import org.eclipse.gef.mvc.behaviors.IBehavior;
	import org.eclipse.gef.mvc.fx.parts.FXDefaultHoverHandlePartFactory;
	import org.eclipse.gef.mvc.parts.IHandlePart;
	import org.eclipse.gef.mvc.parts.IVisualPart;
	
	import com.google.common.collect.Lists;
	import com.google.inject.Inject;
	import com.google.inject.Injector;
	import com.itemis.gef.tutorial.mindmap.parts.MindMapNodePart;
	
	import javafx.scene.Node;
	
	public class MindMapNodeHoverHandlesFactory extends FXDefaultHoverHandlePartFactory {
	
		@Inject
		private Injector injector;
	
		@Override
		public List<IHandlePart<Node, ? extends Node>> createHandleParts(
				List<? extends IVisualPart<Node, ? extends Node>> targets, IBehavior<Node> contextBehavior,
				Map<Object, Object> contextMap) {
			List<IHandlePart<Node, ? extends Node>> handleParts = Lists.newArrayList(); 
			
			handleParts.addAll(super.createHandleParts(targets, contextBehavior, contextMap));
			
			if (targets.size()>0) {
				// if we have more than one target we add take the first, like the super method does
				handleParts.addAll(createHandles(targets.get(0)));
			}
			
			return handleParts;
		}
	
		private List<IHandlePart<Node, ? extends Node>> createHandles(IVisualPart<Node, ? extends Node> target) {
			List<IHandlePart<Node, ? extends Node>> handles = new ArrayList<>();
	
			if (target instanceof MindMapNodePart) {
				// create root handle part
	
				MindMapNodeHandleRootPart parentHp = injector.getInstance(MindMapNodeHandleRootPart.class);
	
				DeleteMindMapNodeHandlePart delHp = injector.getInstance(DeleteMindMapNodeHandlePart.class);
				parentHp.addChild(delHp);
	
				handles.add(parentHp);
	
			}
			return handles;
		}
	}

First  `createHandleParts` of the super class is called, creating handles, like the rectangles to resize.  `createHandles` creates the  `MindMapNodeHandleRootPart` and adds the delete handle to its children list. 

The `MindMapNodeSelectionHandlesFactory` is identical. Just change the class declaration to:

	public class MindMapNodeSelectionHandlesFactory extends FXDefaultSelectionHandlePartFactory {
	
## Binding the factories and policy

As mentioned above the `MvcFXModue` already bind a hover and a selection factory. This is done in two methods: `bindSelectionHandlePartFactoryAsContentViewerAdapter` and `bindHoverHandlePartFactoryAsContentViewerAdapter`.
Overriding these bindings means overriding these methods.

Here are the overriding methods:
 
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

Finally we need to bind the `DeleteNodeHandleOnClickPolicy` to the `DeleteMindMapNodeHandlePart`. Add another method to the module:

	protected void bindDeleteMindMapNodeHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(DeleteNodeHandleOnClickPolicy.class);
	}
	
And call it in `configure`:


	@Override
	protected void configure() {
		
		// old code
		
		bindDeleteMindMapNodeHandlePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), DeleteMindMapNodeHandlePart.class));
	}
	
Now you can create new handles by adding them in the factories and bind the parts to a policy to execute new operations.