package de.embl.cba.tables.modelview.images;

import bdv.util.RandomAccessibleIntervalSource4D;
import bdv.viewer.Interpolation;
import bdv.viewer.Source;
import de.embl.cba.bdv.utils.wrap.Wraps;
import ij.IJ;
import ij.ImagePlus;
import mpicbg.spim.data.sequence.VoxelDimensions;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import java.io.File;
import java.util.*;

import static de.embl.cba.tables.modelview.images.SourceMetadata.*;

public class FileImageSourcesModel implements ImageSourcesModel
{
	private final Map< String, SourceAndMetadata > nameToSourceAndMetadata;

	public FileImageSourcesModel( )
	{
		nameToSourceAndMetadata = new HashMap<>();
	}

	@Override
	public Map< String, SourceAndMetadata > sources()
	{
		return nameToSourceAndMetadata;
	}

	class FileSource< R extends RealType< R > & NativeType< R > > implements Source< R >
	{
		private final SourceMetadata metadata;
		private final File file;
		private RandomAccessibleIntervalSource4D source;

		public FileSource( SourceMetadata metadata, File file )
		{
			this.metadata = metadata;
			this.file = file;
		}

		@Override
		public boolean isPresent( int t )
		{
			return true;
		}

		@Override
		public RandomAccessibleInterval< R > getSource( int t, int level )
		{
			return wrappedSource().getSource( t, level );
		}

		private RandomAccessibleIntervalSource4D< R > wrappedSource()
		{
			if ( source == null )
			{
				final ImagePlus imagePlus = IJ.openImage( file.toString() );
				imagePlus.setTitle( metadata.displayName );

				if( metadata.flavour == Flavour.LabelSource || imagePlus.getBitDepth() == 8 )
				{
					metadata.displayRangeMin = 0.0;
					metadata.displayRangeMax = 255.0;
				}
				else if( imagePlus.getBitDepth() == 16 )
				{
					metadata.displayRangeMin = 0.0;
					metadata.displayRangeMax = 65535.0;
				}

				source = Wraps.imagePlusAsSource4DChannelList( imagePlus ).get( 0 );
			}

			return source;
		}

		@Override
		public RealRandomAccessible< R > getInterpolatedSource( int t, int level, Interpolation method )
		{
			return wrappedSource().getInterpolatedSource( t, level, method );
		}

		@Override
		public void getSourceTransform( int t, int level, AffineTransform3D transform )
		{
			wrappedSource().getSourceTransform( t, level, transform  );
		}

		@Override
		public R getType()
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

	public void addSource(
			String imageId,
			String imageDisplayName,
			File file,
			List< String > imageSetIDs,
			Flavour flavor,
			int numSpatialDimensions )
	{
		if ( nameToSourceAndMetadata.containsKey( imageId ) ) return;

		final SourceMetadata metadata = new SourceMetadata();
		metadata.flavour = flavor;
		metadata.numSpatialDimensions = numSpatialDimensions;
		metadata.imageSetIDs = imageSetIDs;
		metadata.displayName = imageDisplayName;
		metadata.imageId = imageId;

		final FileSource fileSource = new FileSource( metadata, file );

		nameToSourceAndMetadata.put( imageId, new SourceAndMetadata( fileSource, metadata ) );
	}

}
