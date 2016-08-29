package com.itemis.gef.tutorial.visuals;

import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.RoundedRectangle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MindMapNodeVisual extends Group {
	private Text titleText;
	private TextFlow descriptionFlow;
	private Text descriptionText;

	private GeometryNode<RoundedRectangle> shape;

	private VBox labelGroup;

	private StackPane stackPane;
	
	public MindMapNodeVisual() {
		shape = new GeometryNode<>(new RoundedRectangle(0, 0, 70, 30, 8, 8));
		shape.setFill(Color.LIGHTGREEN);
		shape.setStroke(Color.BLACK);

		labelGroup = new VBox(5);
		labelGroup.setPadding(new Insets(10, 20, 10, 20));

		// create label
		titleText = new Text();
		titleText.setTextOrigin(VPos.TOP);

		descriptionText = new Text();
		descriptionText.setTextOrigin(VPos.TOP);

		
		descriptionFlow = new TextFlow(descriptionText);
		descriptionFlow.setMaxWidth(150);
		labelGroup.getChildren().addAll(titleText, descriptionFlow);

		stackPane = new StackPane();
		stackPane.setPrefWidth(150);
		stackPane.getChildren().addAll(shape, labelGroup);
		
		getChildren().addAll(stackPane);
		
		shape.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double val = (double) newValue;
				stackPane.setPrefWidth(val);
				descriptionFlow.setMaxWidth(val);
			}
		});
	}

	public Node getShape() {
		return shape;
	}
	
	public void resizeShape(double width, double height) {
		descriptionText.setWrappingWidth(width-30);
		shape.resize(width, height);
	}

	public void setColor(Color color) {
		shape.setFill(color);
	}

	public void setTitle(String title) {
		this.titleText.setText(title);
	}

	public void setDescription(String description) {
		this.descriptionText.setText(description);
	}

	public Text getTitleText() {
		return titleText;
	}

	public Text getDescriptionText() {
		return descriptionText;
	}
}