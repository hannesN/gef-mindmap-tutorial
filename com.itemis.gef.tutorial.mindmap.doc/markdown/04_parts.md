# Step 4 - Creating the GEF Parts

In this Tutorial, we will create a project, which uses the GEF-MVC framework. We will create a new project, create a new application and configure it vie a dependency injection.

The main part of this tutorial, we will create parts for our model. The parts are the controller in the MVC pattern. They are controlled by policies and behaviors which are injected via the framework.
Confused? Don't worry, in the end of the tutorial, you will understand.  

## Create a new project

Now we will create a new project called `com.itemis.gef.tutorial.mindmap`.
Go to the tab `Manifest.MF` and copy the following text into the file.

	Require-Bundle: org.eclipse.gef.common;bundle-version="5.0.0",
	 org.eclipse.gef.fx;bundle-version="5.0.0",
	 org.eclipse.gef.geometry;bundle-version="5.0.0",
	 org.eclipse.gef.mvc;bundle-version="5.0.0",
	 org.eclipse.gef.mvc.fx;bundle-version="5.0.0",
	 org.eclipse.core.commands;bundle-version="3.8.0",
	 org.eclipse.core.runtime;bundle-version="3.12.0",
	 com.itemis.gef.tutorial.mindmap.model;bundle-version="1.0.0",
	 com.itemis.gef.tutorial.mindmap.visuals;bundle-version="1.0.0"
	Import-Package: com.google.common.collect;version="15.0.0",
	 com.google.common.reflect;version="15.0.0",
	 com.google.inject;version="1.3.0",
	 com.google.inject.binder;version="1.3.0",
	 com.google.inject.multibindings;version="1.3.0",
	 javax.inject;version="1.0.0"

The final manifest should be look like this:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step4_parts/com.itemis.gef.tutorial.mindmap/META-INF/MANIFEST.MF"></script> 

Alternatively, you can set the dependencies on the `Dependencies` tab manually.

## Creating the parts

For each of our models, we need apart. A part is used connects the visual with the model, and implements changes to them.

### SimpleMindMapPart

If you recall, we didn't create a visual for `SimpleMindMap`. We will use a JavaFX `Group` as container for our nodes.

See the following code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step4_parts/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/parts/SimpleMindMapPart.java"></script>
	
This part uses a `javafx.scene.Group` node as visual representation, which is created in the method called `createVisual`. The rest of the methods is
used to manage changes in the model or view. The model is called the content of the part. If you wonder, where the content is set, we'll come to that later.

 
### MindMapNodePart

The `MindMapNodePart` represents one node in our map. It creates the `MindMapNodeVisual` and also refreshes the properties of the visual, if there are changes in the model.

Here is the code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step4_parts/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/parts/MindMapNodePart.java"></script>
	
### MindMapConnectionPart

The `MindMapConnectionpart` represents a connection. It creates a `MindMapConnectionVisual`  and configures the anchors. 

Here is the code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step4_parts/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/parts/MindMapConnectionPart.java"></script>

The method `doGetContentAnchorages` tells the framework, with which nodes the connection is connected. Each object gets a role. In our case we have a start anchorage (the source) and an end anchorage (the target of the connection).
For each anchorage the method `attachToAnchorageVisual` is called. First an anchor provider is retrieved from the part, via `getAdapter`, which eeds to be configured in the module (see below).
After that we set the anchors according to the role.

In `detachFromAnchorageVisual` we remove the anchors, by setting the start and end points to the last positions.  
	
## Creating the parts: Partsfactory

A `IContentPartFactory` is used to create a part and set its content. GEF takes an instance of the model and creates the part (which creates the visual).

Here is the code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step4_parts/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/parts/MindMapPartsFactory.java"></script>

The factory has only one method: `createContentPart`. We are only interested in the `content` right now. Based on the type of the content object, we create a new instance using the injector, which takes care of the dependency injection.


## The AnchorProvider

In the visuals tutorial we created a tiny mindmap by instantiating the visuals and anchors for the connection. This work will be done by the GEF MVC framework now. We already have parts which create the visuals, and also try to create
the connection anchors. To be able to do that, we need to provide an anchor provider.

Here is the code of the `SimpleMindMapAnchorProvider`:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step4_parts/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/parts/SimpleMindMapAnchorProvider.java"></script>

The core is the `get`-method. A factory is bound to an instance of `MindMapNodePart`. The get method retrieves the `Visual` of that part and creates a `DynamicAnchor` like we did in step 3.

TODO: Explain the calculation

## The Module

GEF uses guice to manage the dependency injection. With guice you specify teh dependencies in a separate file, called a `Module`.
GEF provides default module, which we can use as superclass and modify via overriding methods or add new one.

The GEF module use a name pattern to identify, to what class we are binding right now.

Right now our module is small, but will grow in size as we go on with the tutorials. Here is the code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step4_parts/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/SimpleMindMapModul.java"></script>

	
## The Application

Now it's time to see, what our mind amp looks like. We will creating a new Application implementation, called `SimpleMindMapApplication` in the mindmap project.

Here is the code:

<script src="http://gist-it.appspot.com/http://github.com/hannesN/gef-mindmap-tutorial/blob/step4_parts/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/SimpleMindMapApplication.java"></script>

Before we create our scene in `start`we instantiate our Module and create an instance of `FXDomain`. using Guice. Thanks to the default implementation of the `MvcFxModule`.
The method `getContentViewer` shows, how the dependencies are retrieved, with a role. This time, we want a bound FXViewer with the role `FXDomain.CONTENT_VIEWER_ROLE`.

After the creation of the domain, we call `hookViewers`. This method gets the JavaFX canvas and adds it to a scene, which will be set on the stage. later we will extend this method to surround our canvas with buttons
to create new nodes or connection, or provide undo/redo buttons.

After some more stage configuration, we have to activate our domain and then we can create our test model using the `SimpleMindMapExampleFactory`. After that retrieve the `ContentModel` from the viewer. Again this dependency was set
by the `MvcFxModule`. As the name already gives a way, the content model contains the model of the viewer. We add our mindmap to the model, and GEF will automatically create the parts and visuals and render them.

That's it. We have a GEF MVC Application.


## The final result

When you start the application (again, Mac Users, don't forget to uncheck *-XstartOnFirstThread argument when launching with SWT* in the  *Arguments* tab) youre window should look like the following screenshot.

![Rendered Parts](images/step4_result.png "Screenshot of Simple Mind Map")

Note: I changed the example in the `SimpleMindMapExampleFactory`.
 