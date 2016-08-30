package com.itemis.gef.tutorial.mindmap.parts;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef.mvc.parts.IResizableContentPart;
import org.eclipse.gef.mvc.parts.ITransformableContentPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.itemis.gef.tutorial.mindmap.model.MindMapNode;
import com.itemis.gef.tutorial.mindmap.visuals.MindMapNodeVisual;

import javafx.scene.Node;
import javafx.scene.transform.Affine;

/**
 * the {@link MindMapNodePart} is responsible to create and update the {@link MindMapNodeVisual} for
 * a instance of the {@link MindMapNode}.
 * 
 * @author hniederhausen
 *
 */
public class MindMapNodePart extends AbstractFXContentPart<MindMapNodeVisual> implements  ITransformableContentPart<Node, MindMapNodeVisual>, IResizableContentPart<Node, MindMapNodeVisual> {

	@Override
	public MindMapNode getContent() {
		return (MindMapNode) super.getContent();
	}

	@Override
	protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
		// Nothing to anchor to
		return HashMultimap.create();
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		// we don't have any children.
		return Collections.emptyList();
	}

	@Override
	protected MindMapNodeVisual createVisual() {
		return new MindMapNodeVisual();
	}

	@Override
	protected void doRefreshVisual(MindMapNodeVisual visual) {

		// updateing the visuals texts and position
		
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

	@Override
	public void transformContent(AffineTransform transform) {
		// storing the  new position
		Rectangle bounds = getContent().getBounds();
		bounds = bounds.getTranslated(transform.getTranslateX(), transform.getTranslateY());
		getContent().setBounds(bounds);
	}

	@Override
	public void resizeContent(Dimension size) {
		// storing the new size
		getContent().getBounds().setSize(size);		
	}


}
