package de.embl.cba.tables.modelview.images;
import bdv.util.BdvStackSource;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static de.embl.cba.tables.modelview.images.SourceMetadata.Flavour.IntensitySource;


public class SourceMetadata
{
	public String displayName = "Image";
	public String imageId;
	public List< String > imageSetIDs = new ArrayList<>();
	public Flavour flavour = IntensitySource;
	public int numSpatialDimensions = 3;
	public boolean showInitially = false;
	public Double displayRangeMin = 0.0;
	public Double displayRangeMax = 255.0;
	public Color displayColor = Color.white;
	public BdvStackSource bdvStackSource = null;

	private static int id = 0;

	public enum Flavour
	{
		LabelSource,
		IntensitySource
	}

	public SourceMetadata()
	{
		imageId = "ImageId" + id++;
		imageSetIDs.add( imageId );
	}
}
