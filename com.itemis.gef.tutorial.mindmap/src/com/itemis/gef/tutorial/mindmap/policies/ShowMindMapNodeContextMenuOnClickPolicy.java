package com.itemis.gef.tutorial.mindmap.policies;

import java.util.ArrayList;
import java.util.Optional;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.mvc.fx.policies.IFXOnClickPolicy;
import org.eclipse.gef.mvc.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.parts.IRootPart;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.policies.AbstractInteractionPolicy;
import org.eclipse.gef.mvc.policies.DeletionPolicy;

import com.google.common.reflect.TypeToken;
import com.itemis.gef.tutorial.mindmap.operations.SetMindMapNodeColorOperation;
import com.itemis.gef.tutorial.mindmap.operations.SetMindMapNodeDescriptionOperation;
import com.itemis.gef.tutorial.mindmap.operations.SetMindMapNodeTitleOperation;
import com.itemis.gef.tutorial.mindmap.parts.MindMapConnectionPart;
import com.itemis.gef.tutorial.mindmap.parts.MindMapNodePart;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * This policy shows a context menu for MindMapNodeParts, providing some editing
 * functionality.
 * 
 * @author hniederhausen
 *
 */
public class ShowMindMapNodeContextMenuOnClickPolicy extends AbstractInteractionPolicy<Node>
		implements IFXOnClickPolicy {

	@Override
	public void click(MouseEvent event) {
		if (!event.isSecondaryButtonDown()) {
			return; // only listen to secondary buttons
		}

		MenuItem deleteNodeItem = new MenuItem("Delete Node");
		deleteNodeItem.setOnAction((e) -> {
			IRootPart<Node, ? extends Node> root = getHost().getRoot();
			@SuppressWarnings("serial")
			DeletionPolicy<Node> delPolicy = root.getAdapter(new TypeToken<DeletionPolicy<Node>>() {
			});
			init(delPolicy);

			// get all achoreds and check if we have a connection part
			for (IVisualPart<Node, ? extends Node> a : new ArrayList<>(getHost().getAnchoredsUnmodifiable())) {
				if (a instanceof MindMapConnectionPart) {
					// now delete the parts (couldn't do it before, because of a
					// concurrent modification exception)
					delPolicy.delete((MindMapConnectionPart) a);
				}
			}

			// and finally remove the node part
			delPolicy.delete(getHost());
			commit(delPolicy);
		});

		Menu textMenu = createChangeTextsMenu();
		Menu colorMenu = createChangeColorMenu();

		ContextMenu ctxMenu = new ContextMenu(textMenu, colorMenu, deleteNodeItem);
		// show the menu at the mouse position
		ctxMenu.show((Node) event.getTarget(), event.getScreenX(), event.getScreenY());
	}

	private Menu createChangeTextsMenu() {
		Menu textsMenu = new Menu("Change");

		MindMapNodePart host = (MindMapNodePart) getHost();

		MenuItem titleItem = new MenuItem("Title ...");
		titleItem.setOnAction((e) -> {
			try {
				String newTitle = showDialog(host.getContent().getTitle(), "Enter new Title...");
				ITransactionalOperation op = new SetMindMapNodeTitleOperation(host, newTitle);
				host.getRoot().getViewer().getDomain().execute(op, null);
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}

		});

		MenuItem descrItem = new MenuItem("Description ...");
		descrItem.setOnAction((e) -> {
			try {
				String newDescription = showDialog(host.getContent().getDescription(), "Enter new Description...");
				ITransactionalOperation op = new SetMindMapNodeDescriptionOperation(host, newDescription);
				host.getRoot().getViewer().getDomain().execute(op, null);
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
		});

		textsMenu.getItems().addAll(titleItem, descrItem);

		return textsMenu;
	}

	private String showDialog(String defaultValue, String title) {
		TextInputDialog dialog = new TextInputDialog(defaultValue);
		dialog.setTitle(title);
		dialog.setGraphic(null);
		dialog.setHeaderText("");

		Optional<String> result = dialog.showAndWait();
		String entered = defaultValue;

		if (result.isPresent()) {

			entered = result.get();
		}
		return entered;
	}

	private Menu createChangeColorMenu() {
		Menu colorMenu = new Menu("Change Color");
		Color[] colors = { Color.ALICEBLUE, Color.BURLYWOOD, Color.YELLOW, Color.RED, Color.CHOCOLATE,
				Color.GREENYELLOW, Color.WHITE };
		String[] names = { "ALICEBLUE", "BURLYWOOD", "YELLOW", "RED", "CHOCOLATE", "GREENYELLOW", "WHITE" };

		for (int i = 0; i < colors.length; i++) {
			colorMenu.getItems().add(getColorMenuItem(names[i], colors[i]));
		}
		return colorMenu;
	}

	private MenuItem getColorMenuItem(String name, Color color) {
		Rectangle graphic = new Rectangle(20, 20);
		graphic.setFill(color);
		graphic.setStroke(Color.BLACK);
		MenuItem item = new MenuItem(name, graphic);
		item.setOnAction((e) -> submitColor(color));
		return item;
	}

	private void submitColor(Color color) {
		SetMindMapNodeColorOperation op = new SetMindMapNodeColorOperation(getHost(), color);

		try {
			getHost().getRoot().getViewer().getDomain().execute(op, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public MindMapNodePart getHost() {
		return (MindMapNodePart) super.getHost();
	}
}
