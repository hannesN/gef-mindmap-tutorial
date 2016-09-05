# Step 2 - Creating the model

GEF is implementing the MVC pattern, so what we need first is our Model.

Our simple mind map only consists of two elements: a `MindMapNode` and a `MindMapConnection`. Both are collected in a `SimpleMindmap`. To collect all elements of the mind map,
we create an abstract class `AbstractMindMapItem` which will be the super class of nodes and connections.

## Create the project

To create the model, we first we need a project.

Create a new Plugin-project called `com.itemis.gef.tutorial.mindmap.model`. Set the *content* as in the screenshot below.

![Model Project infos](images/modelproject_infos.png "The content of the model project in the New Project... wizard")


After the creation of the project, open the file `MANIFEST.MF` in the folder, go to the tab *Dependencies* and add the following package into the *imported Packages* list:
 * com.google.common.collect
 * org.eclipse.gef.geometry.planar

Without this setting, eclipse can't find the classes in this packages we want to use in our model.

## Create the abstract class

Every element in our mind map is a subclass of `AbstractMindMapItem`. 


<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step2_model/com.itemis.gef.tutorial.mindmap.model/src/com/itemis/gef/tutorial/mindmap/model/AbstractMindMapItem.java"></script>


The `PropertyChangeSupport` is used to listen to model changes.
 
 
## Create the SimpleMindMap

The `SimpleMindMap` is the parent of our 

Let's create the class *SimpleMindMap* in the package `com.itemis.gef.turorial.model`. Here is the code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step2_model/com.itemis.gef.tutorial.mindmap.model/src/com/itemis/gef/tutorial/mindmap/model/SimpleMindMap.java"></script>
	
The code is self explanatory. The simple mind map just consist of a list of MindMapItems. Although we could add a `SimpleMindMap`as child, we will not support that right now.

You might wonder, why the node and the connections are'nt in separate lists. We will come to that in a later tutorial. 

## Create the MindMapNode

First we need to create an empty class: `MindMapConnection` so we can use it in our references. After thatcreate the class `MindMapNode`.

Here is the code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step2_model/com.itemis.gef.tutorial.mindmap.model/src/com/itemis/gef/tutorial/mindmap/model/MindMapNode.java"></script>

This class is also quite easy to understand. A `MindMapNode` has a title and description property. These are the two semantic properties for our mindmap.
The other two properties are storing visual information. The first is the color (which is teh background color of our node) and the second is the bounding box,
which defines the size and the position of the node in the mind map.

In addition we store references of connections, which either are incoming or outgoing connections. These references are used to find the connection to delete, when we are removing a node.

We also inform any listener on changes of the model, via the `PropertyChangeSupport`.

## Create the MindMapConnection

The last model class we create is the `MindMapConnection`.

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step2_model/com.itemis.gef.tutorial.mindmap.model/src/com/itemis/gef/tutorial/mindmap/model/MindMapConnection.java"></script>
	
The Connection just has it's source and it's target node and two helper methods to connect to nodes.

## Creating a factory

To test our application, we want to use some test model. This model will be created by the `SimpleMindMapExampleFactory`.

Here is the code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step2_model/com.itemis.gef.tutorial.mindmap.model/src/com/itemis/gef/tutorial/mindmap/model/SimpleMindMapExampleFactory.java"></script>

The first method create a mind map with just one node. The other create a moe complex map.

## Exporting our model

Finally we need to edit the `MANIFEST.MF` file again. Go to the tab *Runtime* and press *Add* next to left list. Choose the package `com.itemis.gef.tutorial.mindmap.model` and save the file.
Now other projects can use our model. 

the final `MANIFEST.MF` should look like this:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step2_model/com.itemis.gef.tutorial.mindmap.model/META-INF/MANIFEST.MF"></script>
