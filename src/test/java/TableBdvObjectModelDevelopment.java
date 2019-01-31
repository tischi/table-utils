import bdv.util.RandomAccessibleIntervalSource;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.combined.DataModelUtils;
import de.embl.cba.tables.modelview.images.DefaultImageSourcesModel;
import de.embl.cba.tables.modelview.segments.DefaultAnnotatedImageSegment;
import de.embl.cba.tables.modelview.segments.DefaultImageSegmentBuilder;
import de.embl.cba.tables.modelview.segments.ImageSegmentCoordinate;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.ValuePair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TableBdvObjectModelDevelopment
{
	public static void main( String[] args ) throws IOException
	{

		final DefaultImageSourcesModel imageSourcesModel = createImageSourcesModel();

		final ArrayList< DefaultAnnotatedImageSegment > annotatedImageSegments = createImageSegments(
				new File( Examples.class.getResource( "2d-16bit-labelMask-Morphometry.csv" ).getFile() ) );

		final ArrayList< String > categoricalColumns = new ArrayList<>();
		categoricalColumns.add( "Label" );

		ArrayList< String > initialSources = new ArrayList< String >();
		initialSources.add( imageSourcesModel.sources().keySet().iterator().next() );

		DataModelUtils.buildModelsAndViews(
				imageSourcesModel,
				annotatedImageSegments,
				categoricalColumns,
				true,
				initialSources );
	}

	public static ArrayList< DefaultAnnotatedImageSegment > createImageSegments( File tableFile )
	{

		final ArrayList< DefaultAnnotatedImageSegment > segments = new ArrayList<>();

		final HashMap< ImageSegmentCoordinate, String > coordinateToColumnNameMap = new HashMap<>();
		coordinateToColumnNameMap.put( ImageSegmentCoordinate.Label, "Label" );
		coordinateToColumnNameMap.put( ImageSegmentCoordinate.X, "X" );
		coordinateToColumnNameMap.put( ImageSegmentCoordinate.Y, "Y" );

		return TableUtils.segmentsFromTableFile(
				tableFile,
				",",
				coordinateToColumnNameMap, new DefaultImageSegmentBuilder() );
	}

	public static DefaultImageSourcesModel createImageSourcesModel()
	{
		final RandomAccessibleIntervalSource< ? extends RealType< ? > > labelSource = Examples.load2D16BitLabelSource();

		final DefaultImageSourcesModel imageSourcesModel = new DefaultImageSourcesModel( true );

		imageSourcesModel.addLabelSource(
				DefaultImageSegmentBuilder.getDefaultImageIdName(),
				labelSource );

		return imageSourcesModel;
	}
}
