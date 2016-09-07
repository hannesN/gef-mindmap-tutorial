# Step 2 - Creating the model

GEF is implementing the MVC pattern, so what we need first is our Model.

Our simple mind map only consists of two elements: a `MindMapNode` and a `MindMapConnection`. Both are collected in a `SimpleMindmap`. To collect all elements of the mind map,
we create an abstract class `AbstractMindMapItem` which will be the base class of all elements.

## Create the project

To create the model, we first we need a project.

Use the context menu of the project explorer and choose *New -> Other*. Select *Plug-in Development -> Plug-in Project* and click *Next*.
Enter the name `com.itemis.gef.tutorial.mindmap.model` in the next page and press *Next*. Fill out the next page according to the screenshot.

![Model Project infos](images/modelproject_infos.png "The content of the model project in the New Project... wizard")


After the creation of the project, open the file `MANIFEST.MF` in the folder `META-INF`, go to the tab *Dependencies* and add the following packages into the *imported Packages* list:
 * com.google.common.collect
 * org.eclipse.gef.geometry.planar

Without this setting, eclipse can't find the classes in this packages we want to use in our model. The first package provides some helper classes from the Google Guava project. The latter specifies a `Rectangle`class, which we use to store the position and size of a `MindMapNode`.

The classes of the model will be in the package: `com.itemis.gef.tutorial.mindmap.model`.

## Create the abstract class

Every element in our mind map is a subclass of `AbstractMindMapItem`. It has a `PropertyChangeSupport` and two methods to add and remove listeners to the support. 

Create the class by  using the context menu of the project, select *New->Class* and enter the name. Make sure that the package name is set to `com.itemis.gef.tutorial.mindmap.model`.

Press finish and the java editor will open with a skeleton of our class. Here is the code of the complete implementation:
<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step2_model/com.itemis.gef.tutorial.mindmap.model/src/com/itemis/gef/tutorial/mindmap/model/AbstractMindMapItem.java"></script>

 
## Create the SimpleMindMap

The `SimpleMindMap` is the parent of our nodes and connections.

Let's create the class *SimpleMindMap* in the package `com.itemis.gef.turorial.model` and implement it.

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step2_model/com.itemis.gef.tutorial.mindmap.model/src/com/itemis/gef/tutorial/mindmap/model/SimpleMindMap.java"></script>
	
The code is self explanatory. The simple mind map just consist of a list of `AbstractMindMapItem`. Although we could add a `SimpleMindMap`as child, we will not support that right now.

## Create the MindMapNode

Although the connections are stored in the `SimpleMindMap` instance, we want to be able to get the incoming and outgoing connections of a `MindMapNode`.
For each we create a property typed `java.util.List<MindMapConnection>`. To be able to use the type, we need to create the connection class with the new class dialog. make sure it is in the right package. Leave the editor open, we will implement the class in the next section.

Now create another class: `SimpleMindMapNode` and add the following code:
<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step2_model/com.itemis.gef.tutorial.mindmap.model/src/com/itemis/gef/tutorial/mindmap/model/MindMapNode.java"></script>

A `MindMapNode` has a title and description property. These are the two semantic properties for our mind map.
The other two properties are storing visual information. The first is the color (which is the background color of our node) and the second is the bounding box,
which defines the size and the position of the node in the mind map.

We also inform any listener on changes of the model, via the `PropertyChangeSupport` of the base class.

## Create the MindMapConnection

We already created the class `MindMapConnection` now its time to fill it with methods and properties

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step2_model/com.itemis.gef.tutorial.mindmap.model/src/com/itemis/gef/tutorial/mindmap/model/MindMapConnection.java"></script>
	
The Connection just has it's source and it's target node and two helper methods to `connect`  and `disconnect`  two nodes.

## Creating a factory

To test our application, we want to use a test model. This model will be created by the `SimpleMindMapExampleFactory`.

Here is the code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step2_model/com.itemis.gef.tutorial.mindmap.model/src/com/itemis/gef/tutorial/mindmap/model/SimpleMindMapExampleFactory.java"></script>

The first method create a mind map with just one node. The other create a more complex map.

## Exporting our model

Finally we need to edit the `MANIFEST.MF` file again. Go to the tab *Runtime*.
In the *Runtime* tab we specify, what packages of our plug-in are visible by other plug-ins, depending on the model. We need to export out model, there for  and press *Add* next to left list and choose the package `com.itemis.gef.tutorial.mindmap.model` in the appearing dialog. Now save the file.

The final `MANIFEST.MF` should look like this:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step2_model/com.itemis.gef.tutorial.mindmap.model/META-INF/MANIFEST.MF"></script>
