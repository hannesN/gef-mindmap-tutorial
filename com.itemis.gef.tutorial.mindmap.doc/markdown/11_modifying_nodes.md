# Step 11 - Modifying nodes

Right now we can create and delete nodes, but how do we change the content of a node?

In this tutorial we will extend the context menu, to edit the title and description and also change the color.

## Changing the color

Like all model changes, the color change should be done via an operation. the code for an operation should be clear now, and shouldn't need any more explanation.
Here it is:

	package com.itemis.gef.tutorial.mindmap.operations;
	
	import org.eclipse.core.commands.ExecutionException;
	import org.eclipse.core.commands.operations.AbstractOperation;
	import org.eclipse.core.runtime.IAdaptable;
	import org.eclipse.core.runtime.IProgressMonitor;
	import org.eclipse.core.runtime.IStatus;
	import org.eclipse.core.runtime.Status;
	import org.eclipse.gef.mvc.operations.ITransactionalOperation;
	
	import com.itemis.gef.tutorial.mindmap.parts.MindMapNodePart;
	
	import javafx.scene.paint.Color;
	
	public class SetMindMapNodeColorOperation extends AbstractOperation implements ITransactionalOperation {
	
		private final MindMapNodePart nodePart;
		private final Color newColor;
		private final Color oldColor;
		
		
		
		public SetMindMapNodeColorOperation(MindMapNodePart nodePart, Color newColor) {
			super("Change color");
			this.nodePart = nodePart;
			this.newColor = newColor;
			this.oldColor = nodePart.getContent().getColor();
		}
	
		@Override
		public boolean isContentRelevant() {
			// yes we change the model
			return true;
		}
	
		@Override
		public boolean isNoOp() {
			return newColor.equals(oldColor);
		}
	
		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			nodePart.getContent().setColor(newColor);
			nodePart.refreshVisual();
			return Status.OK_STATUS;
		}
	
		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}
	
		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			nodePart.getContent().setColor(oldColor);
			nodePart.refreshVisual(); 
			return Status.OK_STATUS;
		}
	
	}

The line `nodePart.refreshVisual();` assures, that the new color is used at once. 

To execute the operation, we add a menu to the context to provide a selection of color he user can choose from.

Open the `ShowMindMapNodeContextMenuOnClickPolicy` and add the following methods:

	private Menu createChangeColorMenu() {
		Menu colorMenu = new Menu("Change Color");
		Color[] colors = {Color.ALICEBLUE, Color.BURLYWOOD, Color.YELLOW, Color.RED, Color.CHOCOLATE, Color.GREENYELLOW, Color.WHITE};
		String[] names = {"ALICEBLUE", "BURLYWOOD", "YELLOW", "RED", "CHOCOLATE", "GREENYELLOW", "WHITE"};
		
		for (int i=0; i<colors.length; i++) {
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
		if (getHost() instanceof MindMapNodePart) {
			MindMapNodePart host = (MindMapNodePart) getHost();
			
			SetMindMapNodeColorOperation op = new SetMindMapNodeColorOperation(host, color);
			
			try {
				host.getRoot().getViewer().getDomain().execute(op, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
	
	
Also modify `click`.

	public void click(MouseEvent event) {
		
		// old code, leave it
		
		Menu colorMenu = createChangeColorMenu();		
		ContextMenu ctxMenu = new ContextMenu(colorMenu, deleteNodeItem);
		// show the menu at the mouse position
		ctxMenu.show((Node) event.getTarget(), event.getScreenX(), event.getScreenY());
	}
	
# Editing the title and description

To edit the title and the description, again we need operations to execute the changes. Without any comment here are the two almost identical operations:

	package com.itemis.gef.tutorial.mindmap.operations;
	
	import org.eclipse.core.commands.ExecutionException;
	import org.eclipse.core.commands.operations.AbstractOperation;
	import org.eclipse.core.runtime.IAdaptable;
	import org.eclipse.core.runtime.IProgressMonitor;
	import org.eclipse.core.runtime.IStatus;
	import org.eclipse.core.runtime.Status;
	import org.eclipse.gef.mvc.operations.ITransactionalOperation;
	
	import com.itemis.gef.tutorial.mindmap.parts.MindMapNodePart;
	
	public class SetMindMapNodeTitleOperation extends AbstractOperation implements ITransactionalOperation {
	
		private final MindMapNodePart nodePart;
		private final String oldTitle;
		private final String newTitle;
	
		public SetMindMapNodeTitleOperation(MindMapNodePart nodePart, String newTitle) {
			super("Change color");
			this.nodePart = nodePart;
			this.newTitle = newTitle;
			this.oldTitle = nodePart.getContent().getTitle();
		}
	
		@Override
		public boolean isContentRelevant() {
			// yes we change the model
			return true;
		}
	
		@Override
		public boolean isNoOp() {
			return newTitle.equals(oldTitle);
		}
	
		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			nodePart.getContent().setTitle(newTitle);
			nodePart.refreshVisual();
			return Status.OK_STATUS;
		}
	
		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}
	
		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			nodePart.getContent().setTitle(oldTitle);
			nodePart.refreshVisual();
			return Status.OK_STATUS;
		}
	
	}

	
	package com.itemis.gef.tutorial.mindmap.operations;
	
	import org.eclipse.core.commands.ExecutionException;
	import org.eclipse.core.commands.operations.AbstractOperation;
	import org.eclipse.core.runtime.IAdaptable;
	import org.eclipse.core.runtime.IProgressMonitor;
	import org.eclipse.core.runtime.IStatus;
	import org.eclipse.core.runtime.Status;
	import org.eclipse.gef.mvc.operations.ITransactionalOperation;
	
	import com.itemis.gef.tutorial.mindmap.parts.MindMapNodePart;
	
	public class SetMindMapNodeDescriptionOperation extends AbstractOperation implements ITransactionalOperation {
	
		private final MindMapNodePart nodePart;
		private final String oldDescription;
		private final String newDescription;
	
		public SetMindMapNodeDescriptionOperation(MindMapNodePart nodePart, String newDescription) {
			super("Change color");
			this.nodePart = nodePart;
			this.newDescription = newDescription;
			this.oldDescription = nodePart.getContent().getDescription();
		}
	
		@Override
		public boolean isContentRelevant() {
			// yes we change the model
			return true;
		}
	
		@Override
		public boolean isNoOp() {
			return newDescription.equals(oldDescription);
		}
	
		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			nodePart.getContent().setDescription(newDescription);
			nodePart.refreshVisual();
			return Status.OK_STATUS;
		}
	
		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}
	
		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			nodePart.getContent().setDescription(oldDescription);
			nodePart.refreshVisual();
			return Status.OK_STATUS;
		}
	
	}
		
But how do we get the new values?

We will open a JavaFX TextDialog to get the new Text. Add the following methods to Open the `ShowMindMapNodeContextMenuOnClickPolicy` and add the following methods:

	
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
	
	
	
Also modify `click`.

	public void click(MouseEvent event) {
		
		// old code, leave it
		
		Menu textMenu = createChangeTextsMenu();
		Menu colorMenu = createChangeColorMenu();
		
		ContextMenu ctxMenu = new ContextMenu(textMenu, colorMenu, deleteNodeItem);
		// show the menu at the mouse position
		ctxMenu.show((Node) event.getTarget(), event.getScreenX(), event.getScreenY());
	} 
	
You will notice, that we only have a text field for editing the description. A multiline text area would be favorable and could be easily done, by implementing a text area dialog. However, this is out of the scope of the tutorial.

In the future we will implement an inline editing functionality to edit the texts.