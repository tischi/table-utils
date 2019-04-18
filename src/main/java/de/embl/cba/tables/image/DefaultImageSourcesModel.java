package de.embl.cba.tables.image;

import bdv.viewer.Source;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.RealType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static de.embl.cba.tables.image.SourceMetadata.*;


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

	public < R extends RealType< R > > void addSourceAndMetadata( Source< R > source,
																  String imageId,
																  Flavour flavor,
																  int numSpatialDimensions,
																  AffineTransform3D transform,
																  File segmentsTable // TODO: what to do here if the table is not from a file?
	)
	{

		final SourceMetadata metadata = new SourceMetadata( imageId );
		metadata.flavour = flavor;
		metadata.numSpatialDimensions = numSpatialDimensions;
		metadata.sourceTransform = transform;
		metadata.segmentsTable = segmentsTable;

		nameToSourceAndMetadata.put( imageId, new SourceAndMetadata( source, metadata ) );
	}

	public < R extends RealType< R > > void addSourceAndMetadata(
			String imageId,
			SourceAndMetadata< R > sourceAndMetadata )
	{
		nameToSourceAndMetadata.put( imageId, sourceAndMetadata );
	}



}