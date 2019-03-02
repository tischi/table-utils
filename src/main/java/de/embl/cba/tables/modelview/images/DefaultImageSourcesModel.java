package de.embl.cba.tables.modelview.images;

import bdv.viewer.Source;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.RealType;

import java.util.HashMap;
import java.util.Map;

import static de.embl.cba.tables.modelview.images.SourceMetadata.*;


public class DefaultImageSourcesModel implements ImageSourcesModel
{
	private final Map< String, SourceAndMetadata< ? > > nameToSourceAndMetadata;
	private boolean is2D;

	public DefaultImageSourcesModel( boolean is2D )
	{
		this.is2D = is2D;
		nameToSourceAndMetadata = new HashMap<>();
	}

	@Override
	public Map< String, SourceAndMetadata< ? > > sources()
	{
		return nameToSourceAndMetadata;
	}

	@Override
	public boolean is2D()
	{
		return is2D;
	}

	public < R extends RealType< R > > void addSource( Source< R > source,
													   String imageId,
													   Flavour flavor,
													   int numSpatialDimensions,
													   AffineTransform3D transform )
	{

		final SourceMetadata metadata = new SourceMetadata( imageId );
		metadata.flavour = flavor;
		metadata.numSpatialDimensions = numSpatialDimensions;
		metadata.sourceTransform = transform;

		nameToSourceAndMetadata.put( imageId, new SourceAndMetadata( source, metadata ) );
	}


}
