package com.itemis.gef.tutorial.mindmap.parts;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.fx.anchors.DynamicAnchor;
import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.anchors.DynamicAnchor.AnchorageReferenceGeometry;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.mvc.parts.IVisualPart;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;
import com.itemis.gef.tutorial.mindmap.visuals.MindMapConnectionVisual;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;

/**
 * The {@link SimpleMindMapAnchorProvider} create an anchor for a {@link MindMapConnectionVisual}.
 * 
 * It is bound to an {@link MindMapNodePart} and will be called in the {@link MindMapConnectionPart}.
 * 
 * @author hniederhausen
 *
 */
public class SimpleMindMapAnchorProvider extends IAdaptable.Bound.Impl<IVisualPart<Node, ? extends Node>> implements Provider<IAnchor> {

	// the part it is bound to
	private IVisualPart<Node, ? extends Node> host;
	
	// the anchor in case we already created one
	private DynamicAnchor anchor;
	
	@Override
	public IAnchor get() {
		if (anchor == null) {
			// get the visual from the host (MindMapNodePart)
			Node anchorage = getAdaptable().getVisual();
			// create a new anchor instance
			anchor = new DynamicAnchor(anchorage);
			
			// what exactly does that mean?
			anchor.getComputationParameter(AnchorageReferenceGeometry.class).bind(new ObjectBinding<IGeometry>() {
				{
					bind(anchorage.layoutBoundsProperty());
				}
				
				@Override
				protected IGeometry computeValue() {
					@SuppressWarnings("serial")
					// get the registered geometry provider from the host
					Provider<IGeometry> geomProvider = host.getAdapter(new TypeToken<Provider<IGeometry>>() {});
					
					return geomProvider.get();
				}
			});
		}
		return anchor;
	}
	
	@Override
	public IVisualPart<Node, ? extends Node> getAdaptable() {
		return host;
	}

	@Override
	public void setAdaptable(IVisualPart<Node, ? extends Node> adaptable) {
		this.host = adaptable;
	}

	@Override
	public ReadOnlyObjectProperty<IVisualPart<Node, ? extends Node>> adaptableProperty() {
		return null;
	}

}
