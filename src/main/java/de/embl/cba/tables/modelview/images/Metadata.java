package de.embl.cba.tables.modelview.images;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Metadata extends HashMap< String, Object >
{
	// TODO: make enum
	public static final String DISPLAY_NAME = "Display Name";
	public static final String IMAGE_ID = "ImageId";
	public static final String FLAVOUR = "Flavour";
	public static final String NUM_SPATIAL_DIMENSIONS = "Number of spatial dimensions";
	public static final String EXCLUSIVE_IMAGE_SET = "Exclusive image set";
	public static final String SHOW_INITIALLY = "Show initially";
	public static final String DISPLAY_RANGE_MIN = "Display Range Min";
	public static final String DISPLAY_RANGE_MAX = "Display Range Max";
	public static final String COLOR = "Color";

	public enum Flavour
	{
		LabelSource,
		IntensitySource
	}

	public Metadata()
	{
		super();
		put( FLAVOUR, Flavour.IntensitySource );
		put( DISPLAY_NAME, "Image" );
		put( IMAGE_ID, "Image001" );
		put( NUM_SPATIAL_DIMENSIONS, 3 );
		put( SHOW_INITIALLY, false );
		put( COLOR, Color.white );
	}
}
