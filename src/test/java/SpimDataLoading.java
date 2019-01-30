import bdv.BigDataViewer;
import bdv.SpimSource;
import bdv.ViewerImgLoader;
import bdv.VolatileSpimSource;
import bdv.img.cache.VolatileGlobalCellCache;
import bdv.tools.brightness.ConverterSetup;
import bdv.tools.transformation.TransformedSource;
import bdv.util.BdvFunctions;
import bdv.util.BdvOptions;
import bdv.util.BdvStackSource;
import bdv.viewer.Source;
import bdv.viewer.SourceAndConverter;
import mpicbg.spim.data.SpimData;
import mpicbg.spim.data.SpimDataException;
import mpicbg.spim.data.XmlIoSpimData;
import mpicbg.spim.data.generic.sequence.AbstractSequenceDescription;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Volatile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SpimDataLoading
{
	public static void main( String[] args )
	{
		final String file1 = SpimDataLoading.class.getResource( "bdv_mipmap-raw.xml" ).getFile();
		final String file2 = "/Volumes/arendt/EM_6dpf_segmentation/EM-Prospr/em-raw-full-res.xml";

		final File file = new File( file2 );


		final SpimData spimData = openSpimData( file );

		/**
		 * Version 01: show a Volatile Source extracted from SpimData
		 */
		final ArrayList< ConverterSetup > converterSetups = new ArrayList<>();
		final ArrayList< SourceAndConverter< ? > > sources = new ArrayList<>();
		BigDataViewer.initSetups( spimData, converterSetups, sources );
		final Source< ? extends Volatile< ? > > volatileSpimSource = sources.get( 0 ).asVolatile().getSpimSource();
		final BdvStackSource< ? extends Volatile< ? > > show = BdvFunctions.show( volatileSpimSource );
		final Source< ? extends Volatile< ? > > spimSource = show.getSources().get( 0 ).getSpimSource();

		final SpimSource spimSource1 = ( ( VolatileSpimSource )( (TransformedSource )volatileSpimSource ).getWrappedSource()).nonVolatile();

		/**
		 * Version 01: show SpimData
		 */
		final BdvStackSource< ? > bdvStackSource = BdvFunctions.show( spimData ).get( 0 );

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
}
