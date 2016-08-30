package com.itemis.gef.tutorial.mindmap;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.mvc.fx.domain.FXDomain;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.models.ContentModel;

import com.google.inject.Guice;
import com.itemis.gef.tutorial.mindmap.model.SimpleMindMap;
import com.itemis.gef.tutorial.mindmap.model.SimpleMindMapExampleFactory;
import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel;
import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel.Type;
import com.itemis.gef.tutorial.mindmap.visuals.MindMapNodeVisual;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Entry point for our Simple Mind Map Editor, creating ans rendering a JavaFX
 * Window.
 * 
 * @author hniederhausen
 *
 */
public class SimpleMindMapApplication extends Application {

	private Stage primaryStage;
	private FXDomain domain;

	@Override
	public void start(Stage primaryStage) throws Exception {

		SimpleMindMapModul module = new SimpleMindMapModul();
		this.primaryStage = primaryStage;
		// create domain using guice
		this.domain = Guice.createInjector(module).getInstance(FXDomain.class);
		
		// create viewers
		hookViewers();

		// set-up stage
		primaryStage.setResizable(true);
		primaryStage.setWidth(800);
		primaryStage.setHeight(600);
		primaryStage.setTitle("GEF Simple Mindmap");
		primaryStage.sizeToScene();
		primaryStage.show();

		// activate domain
		domain.activate();

		// load contents
		populateViewerContents();

	}

	/**
	 * Creates the example mind map and sets it as content to the viewer.
	 */
	private void populateViewerContents() {
		SimpleMindMapExampleFactory fac = new SimpleMindMapExampleFactory();

		SimpleMindMap mindMap = fac.createComplexExample();

		FXViewer viewer = getContentViewer();

		viewer.getAdapter(ContentModel.class).getContents().setAll(mindMap);

	}

	/**
	 * Returns the content viewer of the domain
	 * 
	 * @return
	 */
	private FXViewer getContentViewer() {
		FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		return viewer;
	}

	/**
	 * Creating JavaFX widgets and set them to the stage.
	 */
	private void hookViewers() {
		// creating parent pane for Canvas and button pane
		BorderPane pane = new BorderPane();

		pane.setTop(createButtonBar());
		pane.setCenter(getContentViewer().getCanvas());
		pane.setRight(createToolPalette());

		pane.setMinWidth(800);
		pane.setMinHeight(600);
		
		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);
	}
	
	private Node createToolPalette() {
		ItemCreationModel creationModel = getContentViewer().getAdapter(ItemCreationModel.class);
		
		MindMapNodeVisual graphic = new MindMapNodeVisual();
		graphic.setTitle("New Node");
		
		// the toggleGroup makes sure, we only select one 
		ToggleGroup toggleGroup = new ToggleGroup();
		
		ToggleButton createNode = new ToggleButton("", graphic);
		createNode.setToggleGroup(toggleGroup);
		createNode.selectedProperty().addListener((e, oldVal, newVal) -> {
			Type type =Type.None;
			if (newVal) {
				type = Type.Node;
			}
			creationModel.setType(type);
		});

		
		// now listen to changes in the model, and deactivate buttons, if necessary
		creationModel.getTypeProperty().addListener((e, oldVal, newVal) -> {
			if (oldVal==newVal) {
				return;
			}
			switch (newVal) {
			case Node:
				break;
			case None:
			default:
				// unselect the button
				toggleGroup.getSelectedToggle().setSelected(false);
				break;
			
			}
		});
		
		return new VBox(20, createNode);
	}

	/**
	 * Creates the undo/redo buttons
	 * 
	 * @return
	 */
	private Node createButtonBar() {

		Button undoButton = new Button("Undo");
		undoButton.setDisable(true);
		undoButton.setOnAction((e) -> {
			try {
				domain.getOperationHistory().undo(domain.getUndoContext(), null, null);
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
		});

		Button redoButton = new Button("Redo");
		redoButton.setDisable(true);
		redoButton.setOnAction((e) -> {
			try {
				domain.getOperationHistory().redo(domain.getUndoContext(), null, null);
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
		});

		// add listener to operation history in our domain 
		// and enable/disable buttons
		domain.getOperationHistory().addOperationHistoryListener((e) -> {
			IUndoContext ctx = domain.getUndoContext();
			undoButton.setDisable(!e.getHistory().canUndo(ctx));
			redoButton.setDisable(!e.getHistory().canRedo(ctx));
		});

		return new HBox(10, undoButton, redoButton);
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

}
