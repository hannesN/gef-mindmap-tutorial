package com.itemis.gef.tutorial.mindmap.parts.handles;

import java.net.URL;

import org.eclipse.gef.fx.nodes.HoverOverlayImageView;

import javafx.scene.image.Image;

public class DeleteMindMapNodeHandlePart extends AbstractMindMapHandlePart<HoverOverlayImageView> {

	public static final String IMG_DELETE = "/delete_obj.gif";
	public static final String IMG_DELETE_DISABLED = "/delete_obj_disabled.gif";

	
	@Override
	protected HoverOverlayImageView doCreateVisual() {
		URL overlayImageResource = DeleteMindMapNodeHandlePart.class.getResource(IMG_DELETE);
		if (overlayImageResource == null) {
			throw new IllegalStateException("Cannot find resource <" + IMG_DELETE + ">.");
		}
		Image overlayImage = new Image(overlayImageResource.toExternalForm());

		URL baseImageResource = DeleteMindMapNodeHandlePart.class.getResource(IMG_DELETE_DISABLED);
		if (baseImageResource == null) {
			throw new IllegalStateException("Cannot find resource <" + IMG_DELETE_DISABLED + ">.");
		}
		Image baseImage = new Image(baseImageResource.toExternalForm());

		HoverOverlayImageView blendImageView = new HoverOverlayImageView();
		blendImageView.baseImageProperty().set(baseImage);
		blendImageView.overlayImageProperty().set(overlayImage);
		return blendImageView;
	}

}
