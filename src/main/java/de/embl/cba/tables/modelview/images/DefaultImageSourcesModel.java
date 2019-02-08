package de.embl.cba.tables.modelview.images;

import bdv.viewer.Source;

import java.util.HashMap;
import java.util.Map;

import static de.embl.cba.tables.modelview.images.Metadata.*;


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


	public void addSource( Source< ? > source,
						   String imageId,
						   Flavour flavor,
						   int numSpatialDimensions )
	{

		final Metadata metadata = new Metadata();
		metadata.put( DISPLAY_NAME, imageId );
		metadata.put( FLAVOUR, flavor );
		metadata.put( NUM_SPATIAL_DIMENSIONS, numSpatialDimensions );

		nameToSourceAndMetadata.put( imageId, new SourceAndMetadata( source, metadata ) );
	}


}
