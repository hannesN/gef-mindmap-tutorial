# Step 3 - Defining the Visuals

## What are Visuals?

Visuals are graphical representations of our model. They represent the view in the MCV-pattern.

## Create and Configure the project
We create a new Plug-in project called `com.itemis.gef.tutorial.visuals`.
After the creation, open the `MANIFEST.MF` and go to the *dependencies* tab.

Again, we import the package: `com.google.common.collect`

In addition, we have some plugin dependencies. Add the following plugins to the list *Required Plug-ins*:

 * org.eclipse.gef.geometry
 * org.eclipse.gef.fx
 * org.eclipse.gef.common
 * org.eclipse.gef.geometry.convert.fx

Set the minimum version to 5.0.0.

## Create the connection visual

The first visual is the Connection visual. The class `MindMapConnectionVisuals` inherits from the class
`org.eclipse.gef.fx.nodes.Connection`and specifies an arrow head in the constructor.

Here is the code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step3_visuals/com.itemis.gef.tutorial.mindmap.visuals/src/com/itemis/gef/tutorial/mindmap/visuals/MindMapConnectionVisual.java"></script>


## Create the node visual

The second visual is a bit more complex. basically our node is a rectangle with rounded corners and two text elements inside.

With some JavaFX knowledge, you should understand the following code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step3_visuals/com.itemis.gef.tutorial.mindmap.visuals/src/com/itemis/gef/tutorial/mindmap/visuals/MindMapNodeVisual.java"></script>
 	
The `descriptionText` is embedded in a textflow, so it will warp correctly, when the node is resized.


## How does it look?

To test our visuals, we create a little JavaFX Application and create some examples.

Here is the code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step3_visuals/com.itemis.gef.tutorial.mindmap.visuals/src/com/itemis/gef/tutorial/mindmap/visuals/MindMapVisualApplication.java"></script>


The class is a subclass of `javafx.application.Application` which manages the whole creation of a window and the event cycles.
When it is ready to render, the method `start`is called and we need to create a `Scene` for the given `Stage` (please refer to the JavaFX documentation for more details).

First we create two nodes, after that we connect them with a MindMapConnectionVisual. To do that, we need to create anchors, which are used to calculate the end of the connection. A `DynamicAnchor` calculates the ends based on the bounds of another node. We use a ChopBoxStrategiy, which means, the Anchor is at the bounding box of our node and is directed to the center of the node.

To start the application, just use the context menu on the java file and chose: *Run as->Java Application*. If you are on a Mac, kill the application in the console view, go to the *Run Configurations* end uncheck the box *-XstartOnFirstThread argument when launching with SWT* in the  *Arguments* tab.


The example should look like this:

![Rendered visuals](images/visualapplication_shot.png "Screenshot of MindMapVisualApplication")

## Exporting the package

Similar to the model, we need to export our visual package to be able to use it outside the plug-in.
Open the file `MANIFEST.MF` and go to the tab *Runtime*.
Press *Add* and add the package `com.itemis.gef.tutorial.mindmap.visuals`.

Your final file should be locking like this:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step3_visuals/com.itemis.gef.tutorial.mindmap.visuals/META-INF/MANIFEST.MF"></script>
