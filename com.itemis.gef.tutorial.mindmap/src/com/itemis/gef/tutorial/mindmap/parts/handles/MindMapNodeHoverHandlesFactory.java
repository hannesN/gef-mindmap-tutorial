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

/**
 * The {@link MindMapNodeHoverHandlesFactory} creates the handles to modify the
 * {@link MindMapNodePart}s.
 * 
 * @author hniederhausen
 *
 */
public class MindMapNodeHoverHandlesFactory extends FXDefaultHoverHandlePartFactory {

	@Inject
	private Injector injector;

	@Override
	public List<IHandlePart<Node, ? extends Node>> createHandleParts(
			List<? extends IVisualPart<Node, ? extends Node>> targets, IBehavior<Node> contextBehavior,
			Map<Object, Object> contextMap) {
		List<IHandlePart<Node, ? extends Node>> handleParts = Lists.newArrayList();

		handleParts.addAll(super.createHandleParts(targets, contextBehavior, contextMap));

		if (targets.size() > 0) {
			// if we have more than one target we add take the first, like the
			// super method does
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
