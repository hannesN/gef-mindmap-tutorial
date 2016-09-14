# Step 15 - Automatic Layouting

In this step we will add another button on our button bar, which triggers an automatic layout operation, which we ill implement by using the algorithms from the bundle `org.eclipse.gef.layout`.

## Adding the dependencies

We will implement the operation in the project *com.itemis.gef.tutorial.mindmap*

First we need to add the following dependencies tp the *MANIFEST.MF*:

* `org.eclipse.gef.layout` and
* `org.eclipse.gef.graph`.

The final file should look like this:

<script src="http://gist-it.appspot.com/https://github.com/hannesN/gef-mindmap-tutorial/blob/step15_layouting_automatically/com.itemis.gef.tutorial.mindmap/META-INF/MANIFEST.MF"></script>

## Implementing the Operation

The `LayoutNodesOperation` gets the `SimpleMindMapPart` which children we want to layout. The layouting will move each node to a new position, according to the algorithm via `transformContent()`.

To calculate the movement we are calculating the translation for each map using GEFs layout algorithms. These algorithms (explained <a href="">here</a>) are using a graph for calculation. So the first step is to create an instance of `Graph` based on our mind map. A graph consists of instances of `Node` and `Edge`. 
After building the `Graph`, we create a `LayoutContext` and apply the layout.

After that, the delta of every `Node` to the location of the `MindMapNodePart` is used to translate the `MindMapNodePart`.

Here is the code:

<script src="http://gist-it.appspot.com/https://github.com/hannesN/gef-mindmap-tutorial/blob/step15_layouting_automatically/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/operations/LayoutNodesOperation.java"></script>


The calculation of the `deltaMap` is all done in `prepare`. The line 

```java
ctx.setLayoutAlgorithm(new RadialLayoutAlgorithm());
```

sets the layout algorithm. You can change it to any other implementation of `ILayoutAlgorithm` to change the behavior. Feel free to test the other algorithms GEF provides or even implement your own.

## Adding the button

Finally we need to add a button to the button bar. We will position it next to the redo-button and on click it will execute the `LayoutNodesOperation`.

Open `SimpleMindMapApplication` and add the following method:

```java
private void startLayoutoperation() {
	try {
		SimpleMindMapPart part = (SimpleMindMapPart) getContentViewer().getRootPart().getContentPartChildren().get(0);
		ITransactionalOperation op = new LayoutNodesOperation(part);
			
		domain.execute(op, null);
	} catch (ExecutionException e) {
		e.printStackTrace();
	}
}
```

And add the following code to `createButtonBar`:

```java
Button layoutButton = new Button("Layout");
layoutButton.setOnAction((e) -> {
	startLayoutoperation();
});

return new HBox(10, undoButton, redoButton, layoutButton);
```

That's it, we now have an automatic layouter.
	