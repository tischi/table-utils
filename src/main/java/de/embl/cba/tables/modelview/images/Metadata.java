package de.embl.cba.tables.modelview.images;

import java.util.HashMap;
import java.util.Map;

public class Metadata
{
	public static final String NAME = "Name";
	public static final String FLAVOUR = "Flavour";
	public static final String LABEL_SOURCE_FLAVOUR = "LabelSource";
	public static final String INTENSITY_SOURCE_FLAVOUR = "IntensitySource";
	public static final String DIMENSIONS = "Dimensions";
	public static final String EXCLUSIVE_IMAGE_SET = "ExclusivelyShowWith";

	private final Map< String, Object > metadata;

	public Metadata( )
	{
		this.metadata = new HashMap<>( );
	}

	public Map< String, Object > get()
	{
		return metadata;
	};
}
