package com.itemis.gef.tutorial.mindmap.parts.handles;

import org.eclipse.gef.mvc.fx.parts.AbstractHandlePart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import com.google.common.collect.SetMultimap;
import com.itemis.gef.tutorial.mindmap.parts.MindMapNodePart;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

/**
 * Root part for the handles of a {@link MindMapNodePart} which will be anchored
 * at the right side.
 * 
 * All the handles for the mindmap node are added to this part.
 * 
 * @author hniederhausen
 *
 */
public class MindMapNodeHandleRootPart extends AbstractHandlePart<VBox> {

	@Override
	protected VBox doCreateVisual() {
		VBox vBox = new VBox();

		vBox.setPickOnBounds(true);
		return vBox;
	}

	@Override
	protected void doRefreshVisual(VBox visual) {
		// check if we have a host
		SetMultimap<IVisualPart<? extends Node>, String> anchorages = getAnchoragesUnmodifiable();
		if (anchorages.isEmpty()) {
			return;
		}

		IVisualPart<? extends Node> anchorage = anchorages.keys().iterator().next();
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
	protected void doAddChildVisual(IVisualPart<? extends Node> child, int index) {
		getVisual().getChildren().add(index, child.getVisual());
	}

	@Override
	protected void doRemoveChildVisual(IVisualPart<? extends Node> child, int index) {
		getVisual().getChildren().remove(index);
	}
}
