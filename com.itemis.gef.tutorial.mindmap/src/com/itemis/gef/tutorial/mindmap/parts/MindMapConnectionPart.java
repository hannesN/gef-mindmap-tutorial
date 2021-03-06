package com.itemis.gef.tutorial.mindmap.parts;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.mvc.fx.parts.AbstractContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;
import com.itemis.gef.tutorial.mindmap.model.MindMapConnection;
import com.itemis.gef.tutorial.mindmap.visuals.MindMapConnectionVisual;

import javafx.scene.Node;

/**
 * The mind map connection part is used the controller for th {@link MindMapConnection}.
 * It create the {@link MindMapConnectionVisual} including the anchors for the connection.
 * 
 * @author hniederhausen
 *
 */
public class MindMapConnectionPart extends AbstractContentPart<Connection> {
		
		public static final String START_ROLE = "START";
		public static final String END_ROLE = "END";
		
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
		protected Connection doCreateVisual() {
			return new MindMapConnectionVisual();
		}

		@Override
		protected void doRefreshVisual(Connection visual) {
			// nothing to do here
		}
		
		@Override
		protected void doAttachToAnchorageVisual(IVisualPart<? extends Node> anchorage, String role) {
			
			// find a anchor provider, which must be registered in the module
			// be aware to use the right interfaces (Provider is used a lot)
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
		protected void doDetachFromAnchorageVisual(IVisualPart<? extends Node> anchorage, String role) {
			// Positions of a connection (e.g. start & end) are always determined by
			// an anchor. The setXPoint() API sets the anchor at position X to a
			// StaticAnchor that always returns the passed-in position.

			if (role.equals(START_ROLE)) {
				getVisual().setStartPoint(getVisual().getStartPoint());
			} else if (role.equals(END_ROLE)) {
				getVisual().setEndPoint(getVisual().getEndPoint());
			} else {
				throw new IllegalArgumentException("Invalid role: "+role);
			}
		}
}
