package de.embl.cba.tables.ij3d;

import bdv.viewer.Source;
import de.embl.cba.tables.Logger;
import de.embl.cba.tables.Utils;
import de.embl.cba.tables.color.ColorUtils;
import ij.ImagePlus;
import ij3d.Content;
import ij3d.Image3DUniverse;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.RealUnsignedByteConverter;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;
import org.scijava.vecmath.Color3f;

public class UniverseUtils
{
	public static < R extends RealType< R > > void addSourceToUniverse(
			Image3DUniverse universe,
			Source< ? > source,
			long maxNumVoxels,
			int displayType,
			ARGBType argbType,
			float transparency,
			int min,
			int max )
	{
		final Integer level = Utils.getLevel( source, maxNumVoxels );

		if ( level == null )
		{
			Logger.warn( "Image is too large to be displayed in 3D." );
			return;
		}

		if ( universe == null )
		{
			Logger.warn( "No Universe exists => Cannot show volume." );
			return;
		}


		RandomAccessibleInterval< ?  > rai = source.getSource( 0, level );
		rai = Views.permute( Views.addDimension( rai, 0, 0 ), 2, 3 );
		final ImagePlus wrap = ImageJFunctions.wrapUnsignedByte(
				( RandomAccessibleInterval ) rai,
				new RealUnsignedByteConverter< R >( min, max ),
				source.getName() );

		final Content content = universe.addContent( wrap, displayType );

		content.setTransparency( transparency );
		content.setLocked( true );
		content.setColor( new Color3f( ColorUtils.getColor( argbType ) ) );

		//segmentToContent.put( segment, content );
		//contentToSegment.put( content, segment );

		universe.setAutoAdjustView( false );
	}

	public static void addImagePlusToUniverse(
			Image3DUniverse universe,
			ImagePlus imagePlus,
			int displayType,
			double transparency )
	{

		final Content content =
				universe.addContent( imagePlus, displayType );

		content.setTransparency( ( float ) transparency );
		content.setLocked( true );

		//segmentToContent.put( segment, content );
		//contentToSegment.put( content, segment );
	}
}
