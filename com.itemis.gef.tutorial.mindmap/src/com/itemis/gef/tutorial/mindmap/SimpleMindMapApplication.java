package com.itemis.gef.tutorial.mindmap;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.mvc.fx.domain.FXDomain;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.models.ContentModel;

import com.google.inject.Guice;
import com.itemis.gef.tutorial.mindmap.model.SimpleMindMap;
import com.itemis.gef.tutorial.mindmap.model.SimpleMindMapExampleFactory;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for our Simple Mind Map Editor, creating ans rendering a JavaFX Window.
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
		Scene scene = new Scene(getContentViewer().getCanvas());
		primaryStage.setScene(scene);
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

}
