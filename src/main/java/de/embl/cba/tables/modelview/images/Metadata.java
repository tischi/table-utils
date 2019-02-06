package de.embl.cba.tables.modelview.images;

import java.util.HashMap;
import java.util.Map;

public class Metadata
{
	public static final String DISPLAY_NAME = "Display Name";
	public static final String IMAGE_ID = "ImageId";
	public static final String FLAVOUR = "Flavour";
	public static final String NUM_SPATIAL_DIMENSIONS = "Number of spatial dimensions";
	public static final String EXCLUSIVE_IMAGE_SET = "Exclusive image set";

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
		metadata.put( DISPLAY_NAME, "Image" );
		metadata.put( IMAGE_ID, "Image001" );
		metadata.put( NUM_SPATIAL_DIMENSIONS, 3 );
	}

	public Map< String, Object > getMap()
	{
		return metadata;
	};
}
