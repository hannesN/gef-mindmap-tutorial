package com.itemis.gef.tutorial.mindmap.rcp.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.widgets.Shell;
public class OpenHandler {

	@Inject
	EPartService partService;
	@Inject
	EModelService modelService;
	@Inject
	MApplication app;

	
	@Execute
	public void execute(Shell shell){
//		FileDialog dialog = new FileDialog(shell);
//		String name = dialog.open();
//		if (name==null)
//			return; // cancel pressed
		
		String partId = "com.itemis.gef.tutorial.ui.e4.e4parts.parts.SimpleMindMapEditor";
		
		MPartStack stack = (MPartStack)modelService.find("com.itemis.gef.tutorial.mindmap.rcp.partstack.sample", app);
		
		
		MPart part = partService.createPart(partId);
		
		part.getPersistedState().put("filename", "/Users/hniederhausen/Desktop/test.smm");
		
		stack.getChildren().add(part);
		
		partService.showPart(part, PartState.CREATE);
		
				
	}
}
