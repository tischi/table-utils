package de.embl.cba.tables;

import bdv.BigDataViewer;
import bdv.tools.brightness.ConverterSetup;
import bdv.viewer.Source;
import bdv.viewer.SourceAndConverter;
import de.embl.cba.bdv.utils.BdvUtils;
import ij.ImagePlus;
import mpicbg.spim.data.SpimData;
import net.imglib2.util.Intervals;

import java.util.ArrayList;

public class Utils
{
	public static void removeCalibration( ImagePlus labelImage )
	{
		labelImage.getCalibration().setUnit( "pixel" );
		labelImage.getCalibration().pixelHeight = 1;
		labelImage.getCalibration().pixelWidth = 1;
		labelImage.getCalibration().pixelDepth = 1;
	}

	public static ArrayList< double[] > getVoxelSpacings( Source< ? > labelsSource )
	{
		final ArrayList< double[] > voxelSpacings = new ArrayList<>();
		final int numMipmapLevels = labelsSource.getNumMipmapLevels();
		for ( int level = 0; level < numMipmapLevels; ++level )
			voxelSpacings.add( BdvUtils.getCalibration( labelsSource, level ) );

		return voxelSpacings;
	}

	public static Integer getLevel( Source< ? > source, long maxNumVoxels )
	{
		final ArrayList< double[] > voxelSpacings = getVoxelSpacings( source );

		for ( int level = 0; level < voxelSpacings.size(); level++ )
		{
			final long numElements = Intervals.numElements( source.getSource( 0, level ) );

			if ( numElements <= maxNumVoxels )
				return level;
		}

		return null;

	}

	public static Source< ? > getSource( SpimData spimData, int sourceIndex )
	{
		ArrayList< ConverterSetup > converterSetups = new ArrayList<>();
		ArrayList< SourceAndConverter< ? > > sources = new ArrayList<>();
		BigDataViewer.initSetups( spimData, converterSetups, sources );
		return sources.get( sourceIndex ).getSpimSource();
	}
}
