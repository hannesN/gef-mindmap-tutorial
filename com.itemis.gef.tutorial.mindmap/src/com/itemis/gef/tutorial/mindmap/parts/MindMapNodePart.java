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
