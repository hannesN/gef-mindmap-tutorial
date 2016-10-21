package com.itemis.gef.tutorial.mindmap;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.inject.AdaptableScopes;
import org.eclipse.gef.common.adapt.inject.AdapterMaps;
import org.eclipse.gef.mvc.fx.MvcFxModule;
import org.eclipse.gef.mvc.fx.behaviors.HoverBehavior;
import org.eclipse.gef.mvc.fx.behaviors.SelectionBehavior;
import org.eclipse.gef.mvc.fx.parts.DefaultHoverFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultSelectionFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultSelectionHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.mvc.fx.parts.SquareSegmentHandlePart;
import org.eclipse.gef.mvc.fx.policies.FocusAndSelectOnClickPolicy;
import org.eclipse.gef.mvc.fx.policies.HoverOnHoverPolicy;
import org.eclipse.gef.mvc.fx.policies.ResizeTranslateFirstAnchorageOnHandleDragPolicy;
import org.eclipse.gef.mvc.fx.policies.TransformPolicy;
import org.eclipse.gef.mvc.fx.policies.TranslateSelectedOnDragPolicy;
import org.eclipse.gef.mvc.fx.providers.ShapeBoundsProvider;
import org.eclipse.gef.mvc.fx.providers.ShapeOutlineProvider;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.itemis.gef.tutorial.mindmap.behaviors.CreateFeedbackBehavior;
import com.itemis.gef.tutorial.mindmap.models.InlineEditModel;
import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel;
import com.itemis.gef.tutorial.mindmap.parts.MindMapNodePart;
import com.itemis.gef.tutorial.mindmap.parts.MindMapPartsFactory;
import com.itemis.gef.tutorial.mindmap.parts.SimpleMindMapAnchorProvider;
import com.itemis.gef.tutorial.mindmap.parts.feedback.CreateFeedbackPartFactory;
import com.itemis.gef.tutorial.mindmap.parts.handles.DeleteMindMapNodeHandlePart;
import com.itemis.gef.tutorial.mindmap.parts.handles.MindMapNodeHoverHandlesFactory;
import com.itemis.gef.tutorial.mindmap.parts.handles.MindMapNodeSelectionHandlesFactory;
import com.itemis.gef.tutorial.mindmap.policies.CreateNewConnectiononClickPolicy;
import com.itemis.gef.tutorial.mindmap.policies.CreateNewNodeOnClickPolicy;
import com.itemis.gef.tutorial.mindmap.policies.InlineEditOnClickPolicy;
import com.itemis.gef.tutorial.mindmap.policies.ShowMindMapNodeContextMenuOnClickPolicy;
import com.itemis.gef.tutorial.mindmap.policies.SimpleMindMapResizePolicy;
import com.itemis.gef.tutorial.mindmap.policies.handles.DeleteNodeHandleOnClickPolicy;

/**
 * 
 * The Guice Module to configure our parts and behaviours.
 * 
 * @author hniederhausen
 *
 */
public class SimpleMindMapModul extends MvcFxModule {

	@Override
	protected void configure() {
		// start the default configuration
		super.configure();

		// scoping the creation model
		bindItemCreationModel();
		
		// scoping the inline edit model
		bindInlineEditModel();

		bindMindMapNodePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), MindMapNodePart.class));

		// with this binding we create the handles
		bindSquareSegmentHandlePartPartAdapter(
				AdapterMaps.getAdapterMapBinder(binder(), SquareSegmentHandlePart.class));

		bindDeleteMindMapNodeHandlePartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), DeleteMindMapNodeHandlePart.class));
	}

	/**
	 * 
	 * @param adapterMapBinder
	 */
	protected void bindMindMapNodePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// bind anchor provider used to create the connection anchors
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(SimpleMindMapAnchorProvider.class);

		// bind a geometry provider, which is used in our anchor provider
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ShapeOutlineProvider.class);

		// provides a hover feedback to the shape, , used by the FXHoverBehavior
		AdapterKey<?> role = AdapterKey.role(DefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER);
		adapterMapBinder.addBinding(role).to(ShapeOutlineProvider.class);

		// provides a selection feedback to the shape
		role = AdapterKey.role(DefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER);
		adapterMapBinder.addBinding(role).to(ShapeBoundsProvider.class);

		// adding a translation policy to move the node around
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(TransformPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(TranslateSelectedOnDragPolicy.class);

		// specify the factory to create the geometry object for the selection
		// handles
		role = AdapterKey.role(DefaultSelectionHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER);
		adapterMapBinder.addBinding(role).to(ShapeBoundsProvider.class);

		// bind the resize policy to the MindMapNodePart
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(SimpleMindMapResizePolicy.class);

		// bind create connection policy
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CreateNewConnectiononClickPolicy.class);

		// bind the context menu policy to the part
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ShowMindMapNodeContextMenuOnClickPolicy.class);

		// adding the inline edit policy to the part to listen to double clicks  on "fields"
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(InlineEditOnClickPolicy.class);
	}

	@Override
	protected void bindIContentPartFactoryAsContentViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindIContentPartFactoryAsContentViewerAdapter(adapterMapBinder);

		// binding one instance of our factory to the IContentPartFactory type,
		// to be used to create our parts
		binder().bind(new TypeLiteral<IContentPartFactory>() {
		}).toInstance(new MindMapPartsFactory());
	}

	@Override
	protected void bindIRootPartAdaptersForContentViewer(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindIRootPartAdaptersForContentViewer(adapterMapBinder);
	
		// binding a hover behavior to the root part. it will react to
		// HoverModel changes and render the hover part
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HoverBehavior.class);

		// Adding the create Node policy
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CreateNewNodeOnClickPolicy.class);

		// adding the creation feedback behavior
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CreateFeedbackBehavior.class);
	}

	@Override
	protected void bindAbstractContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractContentPartAdapters(adapterMapBinder);

		// binding the HoverOnHoverPolicy to every part
		// if a mouse is moving above a part it is set i the HoverModel
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HoverOnHoverPolicy.class);

		// add the focus and select policy to every part, listening to clicks
		// and changing the focus and selection model
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FocusAndSelectOnClickPolicy.class);
	}

	@Override
	protected void bindIViewerAdaptersForContentViewer(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindIViewerAdaptersForContentViewer(adapterMapBinder);
		// bind the model to the content viewer
		bindItemCreationModelAsContentViewerAdapter(adapterMapBinder);

		// bind the model to the content viewer
		bindIInlineEditModelAsContentViewerAdapter(adapterMapBinder);
		
		// binding the creation feedback part factory using the role, we are
		// using in the behavior
		AdapterKey<?> role = AdapterKey.role(CreateFeedbackBehavior.CREATE_FEEDBACK_PART_FACTORY);
		adapterMapBinder.addBinding(role).to(CreateFeedbackPartFactory.class);
	}

	@Override
	protected void bindSelectionHandlePartFactoryAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// overriding default factory with our own
		AdapterKey<?> role = AdapterKey.role(SelectionBehavior.SELECTION_HANDLE_PART_FACTORY);
		adapterMapBinder.addBinding(role).to(MindMapNodeSelectionHandlesFactory.class);
	}

	@Override
	protected void bindHoverHandlePartFactoryAsContentViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// overriding default factory with our own
		AdapterKey<?> role = AdapterKey.role(HoverBehavior.HOVER_HANDLE_PART_FACTORY);
		adapterMapBinder.addBinding(role).to(MindMapNodeHoverHandlesFactory.class);
	}

	protected void bindDeleteMindMapNodeHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(DeleteNodeHandleOnClickPolicy.class);
	}

	/**
	 * Binds the parts of the selection handles (the squares in the corner) to
	 * policies
	 * 
	 * @param adapterMapBinder
	 */
	protected void bindSquareSegmentHandlePartPartAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		//
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(ResizeTranslateFirstAnchorageOnHandleDragPolicy.class);
	}

	protected void bindItemCreationModelAsContentViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		AdapterKey<ItemCreationModel> key = AdapterKey.get(ItemCreationModel.class);
		adapterMapBinder.addBinding(key).to(ItemCreationModel.class);
	}

	/**
	 * Scoping the ItemCreationModel in the Viewer class
	 */
	protected void bindItemCreationModel() {
		binder().bind(ItemCreationModel.class).in(AdaptableScopes.typed(IViewer.class));
	}
	
	protected void bindIInlineEditModelAsContentViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		AdapterKey<InlineEditModel> key = AdapterKey.get(InlineEditModel.class);
		adapterMapBinder.addBinding(key).to(InlineEditModel.class);
	}

	/**
	 * Scoping the InlineEditModel in the Viewer class
	 */
	protected void bindInlineEditModel() {
		binder().bind(InlineEditModel.class).in(AdaptableScopes.typed(IViewer.class));
	}
}
