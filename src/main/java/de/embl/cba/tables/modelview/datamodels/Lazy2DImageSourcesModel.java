package de.embl.cba.tables.modelview.datamodels;

import bdv.util.RandomAccessibleIntervalSource;
import bdv.viewer.Source;
import ij.IJ;
import ij.ImagePlus;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lazy2DImageSourcesModel implements ImageSourcesModel
{
	private Map< String, IntensityAndLabelImageSourcePaths > imageSourcesMap;

	private final boolean is2D;

	/**
	 * Lazily constructs Sources from 2D image files upon request.
	 * There is no caching.
	 *
	 * @param is2D
	 */
	public Lazy2DImageSourcesModel( )
	{
		this.imageSourcesMap = new HashMap<>(  );
		this.is2D = true;
	}

	@Override
	public ArrayList< Source< ? > > getIntensityImageSources( String imageSetId )
	{
		final ArrayList< File > files = imageSourcesMap.get( imageSetId ).intensityImageSources;

		final ArrayList< Source< ? > > sources = new ArrayList<>();

		for ( File file : files )
		{
			final ImagePlus imagePlus = IJ.openImage( file.toString() );
			final RandomAccessibleIntervalSource source = imagePlus2DAsSource3D( imagePlus );
			sources.add( source );
		}

		return sources;
	}

	@Override
	public Source< ? > getLabelImageSource( String imageSetId )
	{
		final ImagePlus imagePlus = IJ.openImage( imageSourcesMap.get( imageSetId ).labelImageSource.toString() );

		final RandomAccessibleIntervalSource source = imagePlus2DAsSource3D( imagePlus );

		return source;
	}

	@Override
	public boolean is2D()
	{
		return is2D;
	}

	@Override
	public ArrayList< String > getImageSetIds()
	{
		return new ArrayList( imageSourcesMap.keySet() );
	}

	public void addLabelImageSource( String imageId, File labelImageSource )
	{
		addIfMissing( imageId );

		imageSourcesMap.get( imageId ).labelImageSource = labelImageSource;
	}

	public void addIntensityImageSource( String imageId, File intensityImageSource )
	{
		addIfMissing( imageId );

		imageSourcesMap.get( imageId ).intensityImageSources.add( intensityImageSource );
	}

	public void addIfMissing( String imageId )
	{
		if ( ! imageSourcesMap.containsKey( imageId ) )
		{
			final IntensityAndLabelImageSourcePaths sources = new IntensityAndLabelImageSourcePaths();
			imageSourcesMap.put( imageId, sources );
		}
	}

	private class IntensityAndLabelImageSourcePaths
	{
		ArrayList< File > intensityImageSources = new ArrayList<>(  );
		File labelImageSource;
	}

	public static RandomAccessibleIntervalSource imagePlus2DAsSource3D( ImagePlus imagePlus )
	{
		RandomAccessibleInterval< RealType > wrap = ImageJFunctions.wrapReal( imagePlus );

		// needs to be at least 3D
		wrap = Views.addDimension( wrap, 0, 0);

		return new RandomAccessibleIntervalSource( wrap, Util.getTypeFromInterval( wrap ), imagePlus.getTitle() );
	}
}
