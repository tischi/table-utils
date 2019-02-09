package de.embl.cba.tables.modelview.images;

import bdv.viewer.Source;
import net.imglib2.realtransform.AffineTransform3D;

import java.util.HashMap;
import java.util.Map;

import static de.embl.cba.tables.modelview.images.SourceMetadata.*;


public class DefaultImageSourcesModel implements ImageSourcesModel
{
	private final Map< String, SourceAndMetadata > nameToSourceAndMetadata;

	public DefaultImageSourcesModel( )
	{
		nameToSourceAndMetadata = new HashMap<>();
	}

	@Override
	public Map< String, SourceAndMetadata > sources()
	{
		return nameToSourceAndMetadata;
	}

	@Override
	public boolean is2D()
	{
		return false;
	}

	public void addSource( Source< ? > source,
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
