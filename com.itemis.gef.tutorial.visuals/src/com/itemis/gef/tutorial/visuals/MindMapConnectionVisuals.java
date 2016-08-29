package com.itemis.gef.tutorial.visuals;

import org.eclipse.gef.fx.nodes.Connection;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class MindMapConnectionVisuals extends Connection {

	public static class ArrowHead extends Polygon {
		public ArrowHead() {
			super(0, 0, 10, 3, 10, -3);
		}
	}

	public MindMapConnectionVisuals() {
		ArrowHead endDecoration = new ArrowHead();
		endDecoration.setFill(Color.BLACK);
		setEndDecoration(endDecoration);
	}
	
}
