package de.embl.cba.tables.image;
import bdv.util.BdvStackSource;
import net.imglib2.realtransform.AffineTransform3D;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static de.embl.cba.tables.image.Metadata.Flavour.IntensitySource;


/**
 * Questions:
 * - make it rather an interface with methods ?
 * -
 */
public class Metadata
{
	public String displayName = "Image";
	public String imageId;
	public List< String > imageSetIDs = new ArrayList<>();
	public Flavour flavour = IntensitySource;
	public int numSpatialDimensions = 3;
	public boolean showInitially = false;
	public Double displayRangeMin = 0.0;
	public Double displayRangeMax = 500.0;
	public Color displayColor = Color.white;
	public BdvStackSource< ? > bdvStackSource = null;
	public AffineTransform3D sourceTransform = new AffineTransform3D();
	public File segmentsTable = null;

	public enum Flavour
	{
		LabelSource,
		IntensitySource
	}

	public Metadata( String imageId )
	{
		this.imageId = imageId;
		imageSetIDs.add( this.imageId );
	}
}
