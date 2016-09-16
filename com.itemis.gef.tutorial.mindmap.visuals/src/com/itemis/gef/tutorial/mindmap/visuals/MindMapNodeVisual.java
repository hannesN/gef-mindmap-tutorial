package com.itemis.gef.tutorial.mindmap.visuals;

import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.RoundedRectangle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
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
	
	//*** Direct Editing Code */
	
	public Node startEditing(String propertyName) {
		TextInputControl inputControl = null;
		int idx = 0;
		double width = shape.getBoundsInLocal().getWidth();
		double height = titleText.getBoundsInLocal().getHeight();
		if (propertyName.equals("title")) {
			inputControl = new TextField(titleText.getText());
		} else if (propertyName.equals("description")) {
			inputControl = new TextArea(descriptionText.getText());
			idx = 1;
			height = shape.getBoundsInLocal().getHeight();
		} else {
			throw new IllegalArgumentException("Invalid entry");
		}
		
		inputControl.setPrefSize(width, height);

		ObservableList<Node> children = labelGroup.getChildren();
		children.remove(idx);
		children.add(idx, inputControl);

		return inputControl;
	}

	public void endEditing(String propertyName) {
		int idx = 0;
		Node elementToAdd = null;
		if (propertyName.equals("title")) {
			elementToAdd = titleText;
		} else if (propertyName.equals("description")) {
			elementToAdd = descriptionFlow;
			idx=1;
		} else {
			throw new IllegalArgumentException("Invalid entry");
		}

		ObservableList<Node> children = labelGroup.getChildren();
		children.remove(idx);
		children.add(idx, elementToAdd);
	}
}