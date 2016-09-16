package com.itemis.gef.tutorial.mindmap.parts;

import java.util.List;

import com.itemis.gef.tutorial.mindmap.models.IInlineEditableField;

/**
 * Parts, who are supposed to provide an inline editing functionality must implement this interface.
 * 
 * @author hniederhausen
 *
 */
public interface IInlineEditablePart {
	/**
	 * Returns a list of fields in the part, which are inline editable.
	 * 
	 * @return a list of InlineEditableField
	 */
	public List<IInlineEditableField> getEditableFields();

	/**
	 * Starts the editing of the entry, by exchanging the read only node with a suitable
	 * editor. The editor will be added to the field
	 * 
	 * @param field the original visual in the part
	 */
	public void startEditing(IInlineEditableField field);
	
	/**
	 * Cancels the editing and switches back to the read only node.
	 * @param field
	 */
	public void endEditing(IInlineEditableField field);
	
	/**
	 * Submits the new values from the editor and switches back to the read only editor
	 * 
	 * @param entry the original visual in the part
	 * @param value the value to store in the field
	 */
	public void submitEditingValue(IInlineEditableField field, Object value);
}
