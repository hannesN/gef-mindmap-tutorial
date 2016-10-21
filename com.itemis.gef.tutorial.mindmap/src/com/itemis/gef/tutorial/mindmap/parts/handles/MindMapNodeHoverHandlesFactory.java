package com.itemis.gef.tutorial.mindmap.parts.handles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.mvc.fx.behaviors.IBehavior;
import org.eclipse.gef.mvc.fx.parts.DefaultHoverHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.IHandlePart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

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
public class MindMapNodeHoverHandlesFactory extends DefaultHoverHandlePartFactory {

	@Inject
	private Injector injector;

	@Override
	public List<IHandlePart<? extends Node>> createHandleParts(
			List<? extends IVisualPart<? extends Node>> targets, IBehavior contextBehavior,
			Map<Object, Object> contextMap) {
		List<IHandlePart<? extends Node>> handleParts = Lists.newArrayList();

		handleParts.addAll(super.createHandleParts(targets, contextBehavior, contextMap));

		if (targets.size() > 0) {
			// if we have more than one target we add take the first, like the
			// super method does
			handleParts.addAll(createHandles(targets.get(0)));
		}

		return handleParts;
	}

	private List<IHandlePart<? extends Node>> createHandles(IVisualPart<? extends Node> target) {
		List<IHandlePart<? extends Node>> handles = new ArrayList<>();

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
