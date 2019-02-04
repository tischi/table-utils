package de.embl.cba.tables.modelview.images;

import java.util.HashMap;
import java.util.Map;

public class Metadata
{
	public static final String NAME = "Name";
	public static final String FLAVOUR = "Flavour";
	public static final String NUM_SPATIAL_DIMENSIONS = "Dimensions";
	public static final String EXCLUSIVE_IMAGE_SET = "ExclusivelyShowWith";

	public enum Flavour
	{
		LabelSource,
		IntensitySource
	}

	private final Map< String, Object > metadata;

	public Metadata()
	{
		metadata = new HashMap<>( );
		metadata.put( FLAVOUR, Flavour.IntensitySource );
		metadata.put( NAME, "Image" );
		metadata.put( NUM_SPATIAL_DIMENSIONS, 3 );
	}

	public Map< String, Object > getMap()
	{
		return metadata;
	};
}
