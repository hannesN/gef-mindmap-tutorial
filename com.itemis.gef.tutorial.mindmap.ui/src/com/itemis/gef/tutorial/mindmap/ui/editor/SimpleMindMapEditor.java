package com.itemis.gef.tutorial.mindmap.ui.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.mvc.fx.ui.MvcFxUiModule;
import org.eclipse.gef.mvc.fx.ui.parts.AbstractFXEditor;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.models.ContentModel;
import org.eclipse.gef.mvc.viewer.IViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;

import com.google.inject.Guice;
import com.google.inject.util.Modules;
import com.itemis.gef.tutorial.mindmap.SimpleMindMapModul;
import com.itemis.gef.tutorial.mindmap.model.AbstractMindMapItem;
import com.itemis.gef.tutorial.mindmap.model.MindMapNode;
import com.itemis.gef.tutorial.mindmap.model.SimpleMindMap;
import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel;
import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel.Type;
import com.itemis.gef.tutorial.mindmap.visuals.MindMapNodeVisual;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * An Eclipse editor usable as extension.
 * 
 * @author hniederhausen
 *
 */
public class SimpleMindMapEditor extends AbstractFXEditor {

	public SimpleMindMapEditor() {
		super(Guice.createInjector(Modules.override(new SimpleMindMapModul()).with(new MvcFxUiModule())));
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// retrieve the viewer's content
		ContentModel contentModel = getDomain().getAdapter(IViewer.class).getAdapter(ContentModel.class);
		SimpleMindMap mindmap = (SimpleMindMap) contentModel.getContents().iterator().next();
		try { // serialize mindmap
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(mindmap);
			oos.close();
			// write to file
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
			file.setContents(new ByteArrayInputStream(out.toByteArray()), true, false, monitor);
			setDirty(false);
			firePropertyChange(PROP_DIRTY);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);

		SimpleMindMap mindmap = null; // read the given input file
		try {
			IFile file = ((IFileEditorInput) input).getFile();
			ObjectInputStream is = new ObjectInputStream(file.getContents());
			mindmap = (SimpleMindMap) is.readObject();
			is.close();
			setPartName(file.getName());

			// reset default color, because we didn't save the color
			for (AbstractMindMapItem item : mindmap.getChildElements()) {
				if (item instanceof MindMapNode) {
					((MindMapNode) item).setColor(Color.GREENYELLOW);
				}
			}

		} catch (EOFException e) {
			// create new SimpleMindMap...
			mindmap = new SimpleMindMap();
		} catch (Exception e) {
			throw new PartInitException("Could not load input", e);
		}
		ContentModel contentModel = getDomain().getAdapter(IViewer.class).getAdapter(ContentModel.class);
		contentModel.getContents().setAll(Collections.singletonList(mindmap));
	}

	/**
	 * Creating JavaFX widgets and set them to the stage.
	 */
	@Override
	protected void hookViewers() {

		final FXViewer contentViewer = getContentViewer();

		// creating parent pane for Canvas and button pane
		BorderPane pane = new BorderPane();

		pane.setCenter(contentViewer.getCanvas());
		pane.setRight(createToolPalette());


		Scene scene = new Scene(pane);
		getCanvas().setScene(scene);
	}

	private Node createToolPalette() {
		ItemCreationModel creationModel = getContentViewer().getAdapter(ItemCreationModel.class);

		MindMapNodeVisual graphic = new MindMapNodeVisual();
		graphic.setTitle("New Node");

		// the toggleGroup makes sure, we only select one
		ToggleGroup toggleGroup = new ToggleGroup();

		ToggleButton createNode = new ToggleButton("", graphic);
		createNode.setToggleGroup(toggleGroup);
		createNode.setMaxWidth(Double.MAX_VALUE);
		createNode.selectedProperty().addListener((e, oldVal, newVal) -> {
			Type type = Type.None;
			if (newVal) {
				type = Type.Node;
			}
			creationModel.setType(type);
		});

		ToggleButton createConn = new ToggleButton("New Connection");
		createConn.setToggleGroup(toggleGroup);
		createConn.setMaxWidth(Double.MAX_VALUE);
		createConn.setMinHeight(50);
		createConn.selectedProperty().addListener((e, oldVal, newVal) -> {
			Type type = Type.None;
			if (newVal) {
				type = Type.Connection;
			}
			creationModel.setType(type);
		});

		// now listen to changes in the model, and deactivate buttons, if
		// necessary
		creationModel.getTypeProperty().addListener((e, oldVal, newVal) -> {
			if (oldVal == newVal) {
				return;
			}
			switch (newVal) {
			case Node:
			case Connection:
				break;
			case None:
			default:
				// unselect the button
				toggleGroup.getSelectedToggle().setSelected(false);
				break;

			}
		});

		return new VBox(20, createNode, createConn);
	}
}
