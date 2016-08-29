# Step 4 - Creating the GEF Parts

In this Tutorial, we will create a project, which uses the GEF-MVC framework. We will create a new project, create a new application and configure it vie a dependency injection.

The main part of this tutorial, we will create parts for our model. The parts are the controller in the MVC pattern. They are controlled by policies and behaviors which are injected via the framework.
Confused? Don't worry, in the end of the tutorial, you will understand.  

## Create a new project

Now we will create a new project called `com.itemis.gef.tutorial.mindmap`.
Go to the tab `Manifest.MF` and copy the following text into the file.

	Require-Bundle: org.eclipse.gef.common;bundle-version="5.0.0",
	 org.eclipse.gef.fx;bundle-version="5.0.0",
	 org.eclipse.gef.geometry;bundle-version="5.0.0",
	 org.eclipse.gef.mvc;bundle-version="5.0.0",
	 org.eclipse.gef.mvc.fx;bundle-version="5.0.0",
	 org.eclipse.core.commands;bundle-version="3.8.0",
	 org.eclipse.core.runtime;bundle-version="3.12.0",
	 com.itemis.gef.tutorial.mindmap.model;bundle-version="1.0.0",
	 com.itemis.gef.tutorial.mindmap.visuals;bundle-version="1.0.0"
	Import-Package: com.google.common.collect;version="15.0.0",
	 com.google.common.reflect;version="15.0.0",
	 com.google.inject;version="1.3.0",
	 com.google.inject.binder;version="1.3.0",
	 com.google.inject.multibindings;version="1.3.0",
	 javax.inject;version="1.0.0"

Alternatively, you can set the dependencies on the `Dependencies` tab manually.

## Creating the parts

For each of our models, we need apart. A part is used connects the visual with the model, and implements changes to them.

### SimpleMindMapPart

If you recall, we didn't create a visual for `SimpleMindMap`. We will use a JavaFX `Group` as container for our nodes.

See the following code:

	package com.itemis.gef.tutorial.mindmap.parts;

	import java.util.List;
	
	import org.eclipse.gef.mvc.fx.parts.AbstractFXContentPart;
	import org.eclipse.gef.mvc.parts.IVisualPart;
	
	import com.google.common.collect.HashMultimap;
	import com.google.common.collect.Lists;
	import com.google.common.collect.SetMultimap;
	import com.itemis.gef.tutorial.mindmap.model.AbstractMindMapItem;
	import com.itemis.gef.tutorial.mindmap.model.SimpleMindMap;
	
	import javafx.scene.Group;
	import javafx.scene.Node;
	
	public class SimpleMindMapPart extends AbstractFXContentPart<Group> {
	
		@Override
		public SimpleMindMap getContent() {
			return (SimpleMindMap) super.getContent();
		}
	
		@Override
		protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
			return HashMultimap.create();
		}
	
		@Override
		protected List<? extends Object> doGetContentChildren() {
			return Lists.newArrayList(getContent().getChildElements());
		}
	
		@Override
		protected Group createVisual() {
			// the visual is just a container for our child visuals (nodes and
			// connections)
			return new Group();
		}
	
		@Override
		protected void addChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
			getVisual().getChildren().add(child.getVisual());
		}
	
		@Override
		protected void removeChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
			getVisual().getChildren().remove(child.getVisual());
		}
	
		@Override
		protected void doAddContentChild(Object contentChild, int index) {
			if (contentChild instanceof AbstractMindMapItem) {
				getContent().addChildElement((AbstractMindMapItem) contentChild, index);
			} else {
				throw new IllegalArgumentException("contentChild has invalid type: " + contentChild.getClass());
			}
		}
	
		@Override
		protected void doRemoveContentChild(Object contentChild) {
			if (contentChild instanceof AbstractMindMapItem) {
				getContent().removeChildElement((AbstractMindMapItem) contentChild);
			} else {
				throw new IllegalArgumentException("contentChild has invalid type: " + contentChild.getClass());
			}
		}
	
		@Override
		protected void doRefreshVisual(Group visual) {
			// no refreshing necessary, just a Group
		}
	}

	
This part uses a `javafx.scene.Group` node as visual representation, which is created in the method called `createVisual`. The rest of the methods is
used to manage changes in the model or view. The model is called the content of the part. If you wonder, where the content is set, we'll come to that later.

 
### MindMapNodePart

Here is the code:

	package com.itemis.gef.tutorial.mindmap.parts;
	
	import java.util.Collections;
	import java.util.List;
	
	import org.eclipse.gef.geometry.planar.Rectangle;
	import org.eclipse.gef.mvc.fx.parts.AbstractFXContentPart;
	import org.eclipse.gef.mvc.fx.policies.FXTransformPolicy;
	
	import com.google.common.collect.HashMultimap;
	import com.google.common.collect.SetMultimap;
	import com.itemis.gef.tutorial.mindmap.model.MindMapNode;
	import com.itemis.gef.tutorial.mindmap.visuals.MindMapNodeVisual;
	
	import javafx.scene.transform.Affine;
	
	public class MindMapNodePart extends AbstractFXContentPart<MindMapNodeVisual>  {
	
		@Override
		public MindMapNode getContent() {
			return (MindMapNode) super.getContent();
		}
	
		@Override
		protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
			return HashMultimap.create();
		}
	
		@Override
		protected List<? extends Object> doGetContentChildren() {
			return Collections.emptyList();
		}
	
		@Override
		protected MindMapNodeVisual createVisual() {
			return new MindMapNodeVisual();
		}
	
		@Override
		protected void doRefreshVisual(MindMapNodeVisual visual) {
	
			MindMapNode node = getContent();
			Rectangle rec = node.getBounds();
	
			visual.setTitle(node.getTite());
			visual.setDescription(node.getDescription());
			visual.setColor(node.getColor());
	
			visual.resizeShape(rec.getWidth(), rec.getHeight());
	
			Affine affine = getAdapter(FXTransformPolicy.TRANSFORM_PROVIDER_KEY).get();
			affine.setTx(rec.getX());
			affine.setTy(rec.getY());
	
		}
	}

	
### MindMapConnectionPart

Here is the code:

	package com.itemis.gef.tutorial.mindmap.parts;
	
	import java.util.Collections;
	import java.util.List;
	
	import org.eclipse.gef.common.adapt.AdapterKey;
	import org.eclipse.gef.fx.anchors.IAnchor;
	import org.eclipse.gef.fx.nodes.Connection;
	import org.eclipse.gef.mvc.fx.parts.AbstractFXContentPart;
	import org.eclipse.gef.mvc.parts.IVisualPart;
	
	import com.google.common.collect.HashMultimap;
	import com.google.common.collect.SetMultimap;
	import com.google.common.reflect.TypeToken;
	import com.google.inject.Provider;
	import com.itemis.gef.tutorial.mindmap.model.MindMapConnection;
	import com.itemis.gef.tutorial.mindmap.visuals.MindMapConnectionVisuals;
	
	import javafx.scene.Node;
	
	public class MindMapConnectionPart extends AbstractFXContentPart<Connection> {
			
			private static final String START_ROLE = "START";
			private static final String END_ROLE = "END";
			
			@Override
			public MindMapConnection getContent() {
				return (MindMapConnection) super.getContent();
			}
			
			@Override
			protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
				SetMultimap<Object, String> anchorages = HashMultimap.create();
	
				anchorages.put(getContent().getSource(), START_ROLE);
				anchorages.put(getContent().getTarget(), END_ROLE);
	
				return anchorages;
			}
	
			@Override
			protected List<? extends Object> doGetContentChildren() {
				return Collections.emptyList();
			}
	
			@Override
			protected Connection createVisual() {
				return new MindMapConnectionVisuals();
			}
	
			@Override
			protected void doRefreshVisual(Connection visual) {
				// nothing to do here
			}
			
			@Override
			protected void attachToAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
				
				// find a anchor provider, which must be registered in the module
				// be aware to use the right interfaces (Proviser is used a lot)
				@SuppressWarnings("serial")
				Provider<? extends IAnchor> adapter = anchorage.getAdapter(AdapterKey.get(new TypeToken<Provider<? extends IAnchor>>() {}));
				if (adapter == null) {
					throw new IllegalStateException("No adapter  found for <" + anchorage.getClass() + "> found.");
				}
				IAnchor anchor = adapter.get();
				
				if (role.equals(START_ROLE)) {
					getVisual().setStartAnchor(anchor);
				} else if (role.equals(END_ROLE)) {
					getVisual().setEndAnchor(anchor);
				} else {
					throw new IllegalArgumentException("Invalid role: "+role);
				}
			}
	
			@Override
			protected void detachFromAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
				if (role.equals(START_ROLE)) {
					getVisual().setStartPoint(getVisual().getStartPoint());
				} else if (role.equals(END_ROLE)) {
					getVisual().setEndPoint(getVisual().getEndPoint());
				} else {
					throw new IllegalArgumentException("Invalid role: "+role);
				}
			}
	}

	
## The Partsfactory

A `IContentPartFactory` is used to create a part and set its content. GEF takes an instance of the model and creates the part (which creates the visual)