package com.itemis.gef.tutorial.mindmap.parts.handles;

import java.util.Set;

import org.eclipse.gef.common.collections.SetMultimapChangeListener;
import org.eclipse.gef.mvc.fx.parts.AbstractFXHandlePart;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.viewer.IViewer;

import javafx.scene.Node;

/**
 * Abstract Part for the handle parts (copied form the FXLogo Example).
 * 
 * registers a listener to the parent, to register and unregister the handle from the viewer.
 * 
 * With out this, the viewer won't find the policies bound to the handle parts.
 * 
 * @author anyssen
 * @author hniederhausen
 *
 */
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