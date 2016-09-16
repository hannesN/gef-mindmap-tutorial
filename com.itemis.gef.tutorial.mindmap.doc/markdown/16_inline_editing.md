# Step 16 - Support Inline Editing

In this step we will provide an inline editing functionality to the `MindMapNodePart`. We will create a new model, similar to our `ItemCreationModel` which stores the currently edited field.

We also create a new interface, which indicates, that a part is supporting inline editing.

Let's start with the model.

## Creating the InlineEditModel

The `InlineEditModel` will store information, which field is currently edited. To store the information,
we specify an interface for an editable field: `IInlineEditableField`.

### Defining IInlineEditableField

The interface `IInlineEditableField` specifies methods we expect from implementations describing an editable field.

The informations are:

* the property name
* the JavaFX `Node` used in the visual to show the value of the property read only  
* the JavaFX `Node` used in the visual to edit the value of the property read only

It also provides methods to get the values of the property from the node. Delegating this to the field description, delegates the responsibility to retrieve those values from different kind of editors to the implementation of this interface.

The final method `isSubmitEvent` is used to check, whether the policy should submit the new value to the model and end the editing stage.

Here is the code:


<script src="http://gist-it.appspot.com/https://github.com/hannesN/gef-mindmap-tutorial/blob/step16_inline_editing/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/models/IInlineEditableField.java"></script>



### Implementing AbstractInlineEditableField

We also provide an abstract implementation for `IInlineEditableField`. `AbstractInlineEditableField` implements the methods to set and get the nodes and the property name. 

Here is the code:

<script src="http://gist-it.appspot.com/https://github.com/hannesN/gef-mindmap-tutorial/blob/step16_inline_editing/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/models/AbstractInlineEditableField.java"></script>

### Implementing InlineEditableTextField

The class `InlineEditableTextField` is a subclass of `AbstractInlineEditableField` and is a implementation for text fields. It expects a `javafx.scene.text.Text` as read-only visualization and an instance of `javafx.scene.control.TextInputControl` as editor. `TextInputControl`is the subclass of `TextEdit` and `TextArea` so we use this field for single and multiline editors.

Here is the code:

<script src="http://gist-it.appspot.com/https://github.com/hannesN/gef-mindmap-tutorial/blob/step16_inline_editing/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/models/InlineEditableTextField.java"></script>

We also store, whether the field should have a multi line editor. Based on this information, we check whether a `KeyEvent` is a valid submit event. In single line fields we submit, when <ENTER> is pressed. For multiline fields, we need <ALT>-<ENTER>.



### Implementing InlineEditModel

The model is our container with the information, which field of which host is edited right now. Therefore the implementation is straight forward. Have look at the code:

<script src="http://gist-it.appspot.com/https://github.com/hannesN/gef-mindmap-tutorial/blob/step16_inline_editing/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/models/InlineEditModel.java"></script>


### Bind the Model to the FXViewer

To be able to get the `InlineEditModel` via `getAdapter` we need to bind the adapter to the module.

Add the following methods to `SimpleMindMapModul`:

```java
protected void bindIInlineEditModelAsContentViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
	AdapterKey<InlineEditModel> key = AdapterKey.get(InlineEditModel.class);
	adapterMapBinder.addBinding(key).to(InlineEditModel.class);
}

/**
 * Scoping the InlineEditModel in the FXViewer class
 */
protected void bindInlineEditModel() {
	binder().bind(InlineEditModel.class).in(AdaptableScopes.typed(FXViewer.class));
}
```

And add the following line to the method `configure`:

```java
// scoping the inline edit model
bindInlineEditModel();
```

and finally jump to `bindContentViewerAdapters` and add:

```java
// bind the model to the content viewer
bindIInlineEditModelAsContentViewerAdapter(adapterMapBinder);
```

Okay, now we can use the model. 


## Extending the MindMapNodeVisual

The `MindMapNodeVisual` represents the view for out MindMapNode model. Therefore the switch from a read-only representation of the nodes properties to an editable should be done here.

We add two new methods, to start and end the editing task. These methods will be called by the `MindMapNodePart`, our controller.

The first method is

```java
public Node startEditing(String propertyName) {
	TextInputControl inputControl = null;
	int idx = 0;
	double width = shape.getBoundsInLocal().getWidth();
	double height = titleText.getBoundsInLocal().getHeight();
	if (propertyName.equals("title")) {
		inputControl = new TextField(titleText.getText());
	} else if (propertyName.equals("description")) {
		inputControl = new TextArea(descriptionText.getText());
		idx = 1;
		height = shape.getBoundsInLocal().getHeight();
	} else {
		throw new IllegalArgumentException("Invalid entry");
	}
	
	inputControl.setPrefSize(width, height);

	ObservableList<Node> children = labelGroup.getChildren();
	children.remove(idx);
	children.add(idx, inputControl);

	return inputControl;
}
```

`startEditing`takes the property name and creates the `TextInputControl`. Based on the property to edit, it creates a `TextField` for the title and a multiline `TextArea` for the description. It also sets the preferred size to the size of the field according to the bounds of the read-only visual.

Finally it replaces the read-only visual with the `TextInputControl` in the children list of the labelGroup.

```java
public void endEditing(String propertyName) {
	int idx = 0;
	Node elementToAdd = null;
	if (propertyName.equals("title")) {
		elementToAdd = titleText;
	} else if (propertyName.equals("description")) {
		elementToAdd = descriptionFlow;
		idx=1;
	} else {
		throw new IllegalArgumentException("Invalid entry");
	}

	ObservableList<Node> children = labelGroup.getChildren();
	children.remove(idx);
	children.add(idx, elementToAdd);
}
```

If `endEditing`is called, the read-only visual is set at its old position and the edit control is removed.

That's the adjustment of the visual. Let's start with the part.

## Declaring the Interface

Remember, the part is the controller in GEFs MVC pattern. Therefore the controller manages the starting, canceling and submitting the inline editing. 

The following interface defines the methods, we will need from a part to provide inline editing:

<script src="http://gist-it.appspot.com/https://github.com/hannesN/gef-mindmap-tutorial/blob/step16_inline_editing/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/parts/IInlineEditablePart.java"></script>

The interface s straight forward, the comments on the method definitions should suffice to understand, what implementations should do. So let's go to the `MindMapNodePart` implementing the interface. 

## Implementing the Interface

First we have to add the IInlineEditablePart interface to the implements statement of the class.

```java
public class MindMapNodePart extends AbstractFXContentPart<MindMapNodeVisual>
				implements ITransformableContentPart<Node, MindMapNodeVisual>, 
						   IResizableContentPart<Node, MindMapNodeVisual>,
						   IInlineEditablePart {
```

No we implement the four methods of our interface. Let's start with `getEditableFields`:

```java
@Override
public List<IInlineEditableField> getEditableFields() {
	
	List<IInlineEditableField> fields = Lists.newArrayList();
	
	fields.add(new InlineEditableTextField("title", getVisual().getTitleText(), false));
	fields.add(new InlineEditableTextField("description", getVisual().getDescriptionText(), true));
	
	return fields;
}
```

No magic there. We just create a new list and fill it with the two `InlineEditableTextField` for our two properties.

```java
@Override
public void startEditing(IInlineEditableField field) {

	Node editor = getVisual().startEditing(field.getPropertyName());
	field.setEditorNode(editor);

}

@Override
public void endEditing(IInlineEditableField field) {
	getVisual().endEditing(field.getPropertyName());
	field.setEditorNode(null);
}
```

The methods `startEditing` and `endEditing` delegate the creation of the editor to the visual and set the editor node to the field.

```java
@Override
public void submitEditingValue(IInlineEditableField field, Object value) {
	if ("title".equals(field.getPropertyName())) {
		getContent().setTitle((String) value);
	} else if ("description".equals(field.getPropertyName())) {
		getContent().setDescription((String) value);
	}
	doRefreshVisual(getVisual());
}
```

`submitEditing` takes the field to edit and the value to set. The latter is also used in the undo operation (see the next section), that's why we don't use the `field.getNewValue()`.

The model is changed according to the field property and we refresh the visual to set the new value.

## Create SubmitOperation

Every change in the model is executed via an operation. The inline editing of the title or description are no exception. So we have to create the operation. Here is the code:

<script src="http://gist-it.appspot.com/https://github.com/hannesN/gef-mindmap-tutorial/blob/step16_inline_editing/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/operations/SubmitInlineEditOperation.java"></script>
	
As you can see. This operation is delegating to `submitEditingValue` of `IInlineEditablePart`.

## Create a Policy

We will create a policy which react on a mouse click on a `MindMapNodePart`. Then we check, if a child of the parts visual is an editable field and if so call the parts `startEditing`.

After the editor node is created, the policy add some listeners, to check when to cancel or submit the changes.

### Implement the Policy

So let's see how it is done in detail. First have a look at the code:

<script src="http://gist-it.appspot.com/https://github.com/hannesN/gef-mindmap-tutorial/blob/step16_inline_editing/com.itemis.gef.tutorial.mindmap/src/com/itemis/gef/tutorial/mindmap/policies/InlineEditOnClickPolicy.java"></script>
	
The `InlineEditOnClickPolicy` implements an `IFXOnClickPolicy` providing one method: `click`.

First we check whether we have a click count of two. If so, we check, which child of the parts visual was clicked. If we've found a suitable field, we update the edit model and call `startEditing`.

The first listener we add listens to key releases. We cancel the editing, if <ESCAPE> is pressed. Else we use the field, to check whether the event is a submit event. If so, create our submit operation and execute it.

The second listener listens to focus changes. If the editor is loosing the focus we want to close it. Sadly clicking inside a text field also changes the focus property shortly from true to false. That's why we start a new thread, wait for 200 milliseconds and check the field again, before we end the editing. `Platform.runLater` assures, that we call `endEditing` in the UI-thread. 

### Bind the Policy

The final step is, to bind the `InlineEditOnClickPolicy` to the `MindMapNodePart`.

Just add the following lines to the method `bindMindMapNodePartAdapters`:

```java
// adding the inline edit policy to the part to listen to double clicks  on "fields"
adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(InlineEditOnClickPolicy.class);
```


### Try it

Now you can start the application, double click on a node and a text field will open. Keep in mind: to store the new value, press <ENTER> in the title field and <ALT>-<ENTER> in the description field. To abort, press escape or click anywhere outside the text input control.
