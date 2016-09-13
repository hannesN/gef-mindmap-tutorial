package com.itemis.gef.tutorial.ui.e4.e4parts.parts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.commands.operations.DefaultOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.fx.swt.canvas.IFXCanvasFactory;
import org.eclipse.gef.mvc.fx.domain.FXDomain;
import org.eclipse.gef.mvc.fx.ui.MvcFxUiModule;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.models.ContentModel;
import org.eclipse.gef.mvc.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.viewer.IViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.itemis.gef.tutorial.mindmap.SimpleMindMapModul;
import com.itemis.gef.tutorial.mindmap.model.AbstractMindMapItem;
import com.itemis.gef.tutorial.mindmap.model.MindMapNode;
import com.itemis.gef.tutorial.mindmap.model.SimpleMindMap;
import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel;
import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel.Type;
import com.itemis.gef.tutorial.mindmap.visuals.MindMapNodeVisual;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SimpleMindMapEditor {
	@Inject()
	private MDirtyable dirty;

	@Inject
	private ESelectionService selectionService;

	@com.google.inject.Inject
	private FXDomain domain;

	@com.google.inject.Inject
	private IFXCanvasFactory canvasFactory;

	private FXCanvas canvas = null;

	private IOperationHistoryListener operationHistoryListener;
	
	private String fileName;

	@Persist
	public void doSave() {

		// retrieve the viewer's content
		ContentModel contentModel = getDomain().getAdapter(IViewer.class).getAdapter(ContentModel.class);
		SimpleMindMap mindmap = (SimpleMindMap) contentModel.getContents().iterator().next();
		try { // serialize mindmap
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(mindmap);
			oos.close();
			// write to file
		
			FileOutputStream fos = new FileOutputStream(fileName);
			fos.write(out.toByteArray());
			fos.close();
			dirty.setDirty(false);	
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}

	/**
	 * Activates the editor by activating its {@link FXDomain}.
	 */
	protected void activate() {
		domain.activate();
	}

	/**
	 * Uses the {@link IFXCanvasFactory} to create the {@link FXCanvas} that
	 * allows the interoperability between SWT and JavaFX.
	 *
	 * @param parent
	 *            The parent {@link Composite} in which the {@link FXCanvas} is
	 *            created.
	 * @return The {@link FXCanvas} created by the {@link IFXCanvasFactory}.
	 */
	protected FXCanvas createCanvas(final Composite parent) {
		return canvasFactory.createCanvas(parent, SWT.NONE);
	}

	@PersistState
	public void persistState(MPart part) {
		System.out.println(part.getPersistedState());
	}

	public void createPartControl(final Composite parent, final MPart part) {
		// create viewer and canvas only after toolkit has been initialized
		canvas = createCanvas(parent);

		// hook viewer controls and selection forwarder
		hookViewers();

		// // register selection provider (if we want to a provide selection)
		// if (selectionProviderFactory != null) {
		// selectionProvider = selectionProviderFactory.create(this);
		// getSite().setSelectionProvider(selectionProvider);
		// }

		// activate domain
		activate();
	}

	private void createContent(MPart part) {
		// loading/creating model

		SimpleMindMap mindmap = null; // read the given input file
		try {
			fileName = part.getPersistedState().get("filename");
			FileInputStream fis = new FileInputStream(new File(fileName));
			ObjectInputStream is = new ObjectInputStream(fis);
			mindmap = (SimpleMindMap) is.readObject();
			is.close();
			part.setLabel(fileName);

			// reset default color, because we didn't save the color
			for (AbstractMindMapItem item : mindmap.getChildElements()) {
				if (item instanceof MindMapNode) {
					((MindMapNode) item).setColor(Color.GREENYELLOW);
				}
			}

		} catch (Exception e) {
			// create new SimpleMindMap...
			e.printStackTrace();
			mindmap = new SimpleMindMap();
		} 
		ContentModel contentModel = getDomain().getAdapter(IViewer.class).getAdapter(ContentModel.class);
		contentModel.getContents().setAll(Collections.singletonList(mindmap));
	}

	/**
	 * Creates an {@link IPropertySheetPage} using the injected
	 * {@link IPropertySheetPageFactory}, if present.
	 *
	 * @return An {@link IPropertySheetPage}, or <code>null</code> in case no
	 *         factory was injected.
	 */
	// protected IPropertySheetPage createPropertySheetPage() {
	// if (propertySheetPageFactory != null) {
	// return propertySheetPageFactory.create(this);
	// }
	// return null;
	// }

	/**
	 * Deactivates the editor by deactivating its {@link FXDomain}.
	 */
	protected void deactivate() {
		domain.deactivate();
	}

	@PreDestroy
	public void dispose() {
		// deactivate domain
		deactivate();

		// unhook selection forwarder
		unhookViewers();

		// unregister operation history listener
		domain.getOperationHistory().removeOperationHistoryListener(operationHistoryListener);
		operationHistoryListener = null;

		domain.dispose();
		domain = null;

		canvasFactory = null;
		if (!canvas.isDisposed()) {
			canvas.dispose();
		}
		canvas = null;
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") final Class key) {
		// Provide a default selection provider (subclasses may overwrite by
		// handling the key and returning a different implementation
		// replace with binding
		// if (ISelectionProvider.class.equals(key)) {
		// return selectionProvider;
		// }
		// // contribute to Properties view
		// else if (IPropertySheetPage.class.equals(key)) {
		// if (propertySheetPage == null) {
		// propertySheetPage = createPropertySheetPage();
		// }
		// return propertySheetPage;
		// } else if (UndoRedoActionGroup.class.equals(key)) {
		// // used by action bar contributor
		// return undoRedoActionGroup;
		// } else
		if (IUndoContext.class.equals(key)) {
			return domain.getUndoContext();
		} else if (IOperationHistory.class.equals(key)) {
			return domain.getOperationHistory();
		}

		return null;
	}

	/**
	 * Returns the {@link FXCanvas} that was previously created by the
	 * {@link IFXCanvasFactory} which was previously injected into this editor.
	 *
	 * @return The {@link FXCanvas} that was previously created by the
	 *         {@link IFXCanvasFactory}.
	 */
	protected FXCanvas getCanvas() {
		return canvas;
	}

	/**
	 * Returns the {@link FXViewer} of the {@link FXDomain} which was previously
	 * injected into this editor.
	 *
	 * @return The {@link FXViewer} of the {@link FXDomain} which was previously
	 *         injected into this editor.
	 */
	protected FXViewer getContentViewer() {
		return domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
	}

	/**
	 * Returns the {@link FXDomain} that was previously injected into this
	 * editor.
	 *
	 * @return The {@link FXDomain} that was previously injected into this
	 *         editor.
	 */
	public FXDomain getDomain() {
		return domain;
	}

	/**
	 * Returns the {@link ISelectionProvider} used by this
	 * {@link AbstractFXEditor}. May be <code>null</code> in case no injection
	 * provider is used.
	 *
	 * @return {@link ISelectionProvider}
	 */
	// public ISelectionProvider getSelectionProvider() {
	// return selectionProvider;
	// }

	/**
	 * Hooks all viewers that are part of this editor into the {@link FXCanvas}.
	 */
	/**
	 * Creating JavaFX widgets and set them to the stage.
	 */
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

	@PostConstruct
	public void init(Composite parent, MPart part) {

		// register selection provider (if we want to a provide selection)
		// if (selectionProvider != null) {
		// site.setSelectionProvider(selectionProvider);
		// }

		// TODO inject guice dependencies (gef)

		Module module = Modules.override(new SimpleMindMapModul()).with(new E4MvcFXUiModule());
		Injector injector = Guice.createInjector(module);
		injector.injectMembers(this);

		operationHistoryListener = new IOperationHistoryListener() {
			@Override
			public void historyNotification(final OperationHistoryEvent event) {
				IUndoableOperation operation = event.getOperation();
				if (event.getEventType() == OperationHistoryEvent.OPERATION_ADDED
						&& event.getHistory().getUndoHistory(operation.getContexts()[0]).length > 0) {
					if (!(operation instanceof ITransactionalOperation)
							|| ((ITransactionalOperation) operation).isContentRelevant()) {
						dirty.setDirty(true);
					}
				}
			}
		};
		//
		// undoRedoActionGroup = new UndoRedoActionGroup(getSite(),
		// (IUndoContext) getAdapter(IUndoContext.class), true);
		// undoRedoActionGroup.fillActionBars(site.getActionBars());

		getDomain().getOperationHistory().addOperationHistoryListener(operationHistoryListener);

		createPartControl(parent, part);

		createContent(part);
	}

	@PersistState
	public void setPersistedState(Map<String, String> state) {
		System.out.println("Stuff");
	}

	@Focus
	public void setFocus() {
		canvas.setFocus();
	}

	/**
	 * Unhooks all viewers that are part of this editor.
	 */
	protected void unhookViewers() {
		// TODO: What about taking the visuals out of the canvas?
	}

	private class E4MvcFXUiModule extends MvcFxUiModule {

		@Override
		protected void configure() {
			super.configure();
			bindDirty();
			bindESelectionService();
		}

		private void bindESelectionService() {
			binder().bind(ESelectionService.class).toInstance(selectionService);
		}

		protected void bindDirty() {
			binder().bind(MDirtyable.class).toInstance(dirty);
		}

		@Override
		protected void bindIOperationHistory() {
			binder().bind(IOperationHistory.class).toInstance(new DefaultOperationHistory());
		}
	}
}
