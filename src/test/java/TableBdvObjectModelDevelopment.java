import bdv.util.RandomAccessibleIntervalSource;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.datamodels.DataModelUtils;
import de.embl.cba.tables.modelview.datamodels.DefaultImageSourcesModel;
import de.embl.cba.tables.modelview.objects.DefaultAnnotatedImageSegment;
import de.embl.cba.tables.modelview.objects.DefaultImageSegmentBuilder;
import de.embl.cba.tables.modelview.objects.ImageSegmentCoordinate;
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

		DataModelUtils.buildModelsAndViews(
				imageSourcesModel,
				annotatedImageSegments,
				categoricalColumns,
				true );
	}

	public static ArrayList< DefaultAnnotatedImageSegment > createImageSegments( File tableFile )
	{

		final ArrayList< DefaultAnnotatedImageSegment > segments = new ArrayList<>();

		final HashMap< ImageSegmentCoordinate, ValuePair< String, Integer > > coordinateToColumnNameMap = new HashMap<>();
		coordinateToColumnNameMap.put( ImageSegmentCoordinate.Label, new ValuePair( "Label",  null ) );
		coordinateToColumnNameMap.put( ImageSegmentCoordinate.X, new ValuePair("X", null ) );
		coordinateToColumnNameMap.put( ImageSegmentCoordinate.Y, new ValuePair("Y", null ) );

		return TableUtils.segmentsFromTableFile(
				tableFile,
				",",
				coordinateToColumnNameMap );
	}

	public static DefaultImageSourcesModel createImageSourcesModel()
	{
		final RandomAccessibleIntervalSource< ? extends RealType< ? > > labelSource = Examples.load2D16BitLabelSource();

		final DefaultImageSourcesModel imageSourcesModel = new DefaultImageSourcesModel( true );

		imageSourcesModel.addLabelSource(
				DefaultImageSegmentBuilder.getDefaultImageSetName(),
				labelSource );

		return imageSourcesModel;
	}
}
