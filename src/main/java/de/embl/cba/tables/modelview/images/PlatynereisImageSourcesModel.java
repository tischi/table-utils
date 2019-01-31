package de.embl.cba.tables.modelview.images;

import bdv.BigDataViewer;
import bdv.tools.brightness.ConverterSetup;
import bdv.util.RandomAccessibleIntervalSource;
import bdv.viewer.Interpolation;
import bdv.viewer.Source;
import bdv.viewer.SourceAndConverter;
import ij.ImagePlus;
import mpicbg.spim.data.SpimData;
import mpicbg.spim.data.SpimDataException;
import mpicbg.spim.data.XmlIoSpimData;
import mpicbg.spim.data.sequence.VoxelDimensions;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static de.embl.cba.tables.modelview.images.Metadata.*;

public class PlatynereisImageSourcesModel implements ImageSourcesModel
{

	public static final String DEFAULT_EM_RAW_FILE_ID = "em-raw-full-res"; //"em-raw-100nm"; //"em-raw-10nm-10nm-25nm"; //"em-raw-100nm"; //
	public static final String DEFAULT_LABELS_FILE_ID = "em-segmented-cells-labels" ;
	public static final String LABELS_FILE_ID = "-labels" ;

	public static final String BDV_XML_SUFFIX = ".xml";
	public static final String IMARIS_SUFFIX = ".ims";
	public static final double PROSPR_SCALING_IN_MICROMETER = 0.5;
	public static final String EM_RAW_FILE_ID = "em-raw-"; //"em-raw-100nm"; //"em-raw-10nm-10nm-25nm"; //"em-raw-100nm"; //
	public static final String EM_SEGMENTED_FILE_ID = "em-segmented";
	public static final String EM_FILE_ID = "em-";
	public static final String SELECTION_UI = "Data sources";
	public static final String POSITION_UI = "Move to position";
	public static final Color DEFAULT_GENE_COLOR = new Color( 255, 0, 255, 255 );
	public static final Color DEFAULT_EM_RAW_COLOR = new Color( 255, 255, 255, 255 );
	public static final Color DEFAULT_EM_SEGMENTATION_COLOR = new Color( 255, 0, 0, 255 );
	public static final double ZOOM_REGION_SIZE = 50.0;
	public static final String NEW_PROSPR = "-new";
	public static final String AVG_PROSPR = "-avg";

	public static final String CELLULAR_MODELS = "cellular-models";
	public static final CharSequence MEDS = "-MEDs" ;
	public static final CharSequence SPMS = "-SPMs";
	public static final String OLD = "-OLD";


	private final Map< String, SourceAndMetadata > nameToSourceAndMetadata;


	public PlatynereisImageSourcesModel( )
	{
		nameToSourceAndMetadata = new HashMap<>();
	}

	@Override
	public Map< String, SourceAndMetadata > sources()
	{
		return nameToSourceAndMetadata;
	}


	public static Metadata metadataFromSpimData( File file )
	{
		final Metadata metadata = new Metadata();

		if ( file.toString().contains( LABELS_FILE_ID ) )
		{
			metadata.get().put( FLAVOUR, LABEL_SOURCE_FLAVOUR );
		}
		else
		{
			metadata.get().put( FLAVOUR, INTENSITY_SOURCE_FLAVOUR );
		}

		metadata.get().put( DIMENSIONS, 3 );
		metadata.get().put( NAME, sourceName( file ) );

		return metadata;
	}



	private static String sourceName( File file )
	{
		String dataSourceName = file.getName().replaceAll( BDV_XML_SUFFIX, "" );

		dataSourceName = getProSPrName( dataSourceName );

		return dataSourceName;
	}

	private static String getProSPrName( String dataSourceName )
	{
		if ( dataSourceName.contains( NEW_PROSPR ) )
		{
			dataSourceName= dataSourceName.replace( NEW_PROSPR, MEDS );
		}
		else if ( dataSourceName.contains( AVG_PROSPR ) )
		{
			dataSourceName = dataSourceName.replace( AVG_PROSPR, SPMS );
		}
		else if ( ! dataSourceName.contains( EM_FILE_ID ) )
		{
			dataSourceName = dataSourceName + OLD;
		}
		return dataSourceName;
	}

	public static SpimData openSpimData( File file )
	{
		try
		{
			SpimData spimData = new XmlIoSpimData().load( file.toString() );
			return spimData;
		}
		catch ( SpimDataException e )
		{
			System.out.println( file.toString() );
			e.printStackTrace();
			return null;
		}
	}

	class LazySpimSource< T extends NumericType< T > > implements Source< T >
	{
		private final String name;
		private final File file;
		private Source< T > source;

		public LazySpimSource( String name, File file )
		{
			this.name = name;
			this.file = file;
		}

		private Source< T > wrappedSource()
		{
			if ( source == null )
			{
				final SpimData spimData = openSpimData( file );
				final ArrayList< ConverterSetup > converterSetups = new ArrayList<>();
				final ArrayList< SourceAndConverter< ? > > sources = new ArrayList<>();
				BigDataViewer.initSetups( spimData, converterSetups, sources );

				source = ( Source< T > ) sources.get( 0 ).asVolatile().getSpimSource();
			}

			return source;
		}

		@Override
		public boolean isPresent( int t )
		{
			return true;
		}

		@Override
		public RandomAccessibleInterval< T > getSource( int t, int level )
		{
			return wrappedSource().getSource( t, level );
		}

		@Override
		public RealRandomAccessible< T > getInterpolatedSource( int t, int level, Interpolation method )
		{
			return wrappedSource().getInterpolatedSource( t, level, method );
		}

		@Override
		public void getSourceTransform( int t, int level, AffineTransform3D transform )
		{
			wrappedSource().getSourceTransform( t, level, transform  );
		}

		@Override
		public T getType()
		{
			return wrappedSource().getType();
		}

		@Override
		public String getName()
		{
			return wrappedSource().getName();
		}

		@Override
		public VoxelDimensions getVoxelDimensions()
		{
			return wrappedSource().getVoxelDimensions();
		}

		@Override
		public int getNumMipmapLevels()
		{
			return wrappedSource().getNumMipmapLevels();
		}
	}

	public void addSource( File file )
	{
		final String imageId = sourceName( file );

		final LazySpimSource lazySpimSource = new LazySpimSource( imageId, file );

		final Metadata metadata = metadataFromSpimData( file );

		nameToSourceAndMetadata.put( imageId, new SourceAndMetadata( lazySpimSource, metadata ) );
	}


	public static < T extends NumericType< T > >
	RandomAccessibleIntervalSource< T > imagePlus2DAsSource3D( String name, ImagePlus imagePlus )
	{
		RandomAccessibleInterval< RealType > wrap = ImageJFunctions.wrapReal( imagePlus );

		// needs to be at least 3D
		wrap = Views.addDimension( wrap, 0, 0);

		return new RandomAccessibleIntervalSource( wrap, Util.getTypeFromInterval( wrap ), name );
	}
}
