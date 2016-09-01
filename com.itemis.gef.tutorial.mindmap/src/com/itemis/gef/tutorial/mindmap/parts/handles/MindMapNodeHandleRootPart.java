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

/**
 * Root part for the handles of a {@link MindMapNodePart} which will be anchored
 * at the right side.
 * 
 * All the handles for the mindmap node are added to this part.
 * 
 * @author hniederhausen
 *
 */
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
