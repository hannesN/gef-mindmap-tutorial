# Step 7 - Undo/Redo operations

Before we step into creating new nodes and connection, we will
take a detour and add two buttons to our window, which trigger used to
undo and redo operations

## Extending the scene

First we need to create a new scene. Look at the following code from `SimpleMindMapApplication`:

```java
/**
 * Creating JavaFX widgets and set them to the stage.
 */
private void hookViewers() {
	// creating parent pane for Canvas and button pane
	BorderPane pane = new BorderPane();

	pane.setTop(createButtonBar());
	pane.setCenter(getContentViewer().getCanvas());

	pane.setMinWidth(800);
	pane.setMinHeight(600);
	
	Scene scene = new Scene(pane);
	primaryStage.setScene(scene);
}
```

The new stage consists of a `BorderPane` . In its center is get content viewers canvas. The top contains the button bar we are creating now.

## Creating the button bar

The following creates two disabled buttons, labeled undo and redo.

```java
/**
 * Creates the undo/redo buttons
 * 
 * @return
 */
private Node createButtonBar() {
	Button undoButton = new Button("Undo");
	undoButton.setDisable(true);
	
	Button redoButton = new Button("Redo");
	redoButton.setDisable(true);
		
	return new HBox(10, undoButton, redoButton);
}
```
	
## Listening to the operation history

The domain contains out operation history. Every policy triggers an operation, which will be added to the history. Thus we need to listen to it.

Add an `OperationHistoryListener` to the history, which activates the buttons based on the state of the history. If you also use Lambdas, the method should look like this now:

```java
private Node createButtonBar() {

	Button undoButton = new Button("Undo");
	undoButton.setDisable(true);
	
	Button redoButton = new Button("Redo");
	redoButton.setDisable(true);
	
	// add listener to operation history in our domain 
	// and enable/disable buttons
	domain.getOperationHistory().addOperationHistoryListener((e) -> {
		IUndoContext ctx = domain.getUndoContext();
		undoButton.setDisable(!e.getHistory().canUndo(ctx));
		redoButton.setDisable(!e.getHistory().canRedo(ctx));
	});

	return new HBox(10, undoButton, redoButton);
}
```
	
Now the undo-button activates, when we move or resize a node. Now we need to add listeners to the buttons, to execute an undo or redo operation.

Here is the final `createButtonBar`:

```java
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
```
	
That's it. Thanks to GEF there wasn't much work to do this time.