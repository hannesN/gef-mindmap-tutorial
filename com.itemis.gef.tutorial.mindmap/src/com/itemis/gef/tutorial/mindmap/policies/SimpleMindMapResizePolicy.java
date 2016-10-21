package com.itemis.gef.tutorial.mindmap.policies;

import org.eclipse.gef.mvc.fx.policies.ResizePolicy;

import com.itemis.gef.tutorial.mindmap.visuals.MindMapNodeVisual;

import javafx.scene.Node;

/**
 * Resize Policy for {@link MindMapNodeParts}.
 * 
 * @author hniederhausen
 *
 */
public class SimpleMindMapResizePolicy extends ResizePolicy {

	@Override
	protected Node getVisualToResize() {
		// MindMapNodeVisual is not resizeable, the shape inside it is, so we use this
		MindMapNodeVisual visualToResize = (MindMapNodeVisual) super.getVisualToResize();
		return visualToResize.getShape();
	}
}
