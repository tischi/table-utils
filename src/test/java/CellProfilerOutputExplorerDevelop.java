import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.combined.DataModelUtils;
import de.embl.cba.tables.modelview.images.CellProfilerImageSourcesModel;
import de.embl.cba.tables.modelview.images.CellProfilerImageSourcesModelCreator;
import de.embl.cba.tables.modelview.segments.DefaultAnnotatedImageSegment;
import de.embl.cba.tables.modelview.segments.DefaultImageSegmentBuilder;
import de.embl.cba.tables.modelview.segments.ImageSegmentCoordinate;
import de.embl.cba.tables.modelview.segments.SegmentUtils;
import net.imglib2.util.ValuePair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CellProfilerOutputExplorerDevelop
{

	public static final String DATASET_INDEX = "ImageNumber";
	public static final String FOLDER = "PathName_";
	public static final String FILE = "FileName_";
	private static ArrayList< String > columns;

	public static void main( String[] args ) throws IOException
	{
		//final File file = new File( TestTableLoading.class.getResource( "cellprofiler-table.txt" ).getFileColumn() );

		final File tableFile = new File("/Users/tischer/Documents/daja-schichler-nucleoli-segmentation--data/2019-01-31/concatenated_tables/merged_images_nuclei.txt");

//		final File file = new File("/Volumes/cba/exchange/Daja-Christian/20190116_for_classification_interphase_versus_mitotic/concatenated_tables/merged_images_nuclei.txt" );
		//final JTable table = TableUtils.loadTable( file, "\t" );

		final CellProfilerImageSourcesModelCreator modelCreator = new CellProfilerImageSourcesModelCreator(
				tableFile,
				"/Volumes/cba/exchange/Daja-Christian/20190116_for_classification_interphase_versus_mitotic",
				"/Users/tischer/Documents/daja-schichler-nucleoli-segmentation--data/2019-01-31",
				"\t"
		);

		final CellProfilerImageSourcesModel imageSourcesModel = modelCreator.getModel();

		final ArrayList< DefaultAnnotatedImageSegment > annotatedImageSegments = createCellProfilerImageSegments( tableFile );

		final ArrayList< String > categoricalColumns = new ArrayList<>();
		categoricalColumns.add( "Label" );


		ArrayList< String > initialSources = new ArrayList< String >();
		initialSources.add( imageSourcesModel.sources().keySet().iterator().next() );

		DataModelUtils.buildModelsAndViews(
				imageSourcesModel,
				annotatedImageSegments,
				categoricalColumns,
				false,
				initialSources );

	}

	public static ArrayList< DefaultAnnotatedImageSegment > createCellProfilerImageSegments( File tableFile )
	{

		final HashMap< ImageSegmentCoordinate, String > coordinateToColumnNameAndIndexMap = new HashMap<>();
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.ImageId,
				"ImageNumber" + SegmentUtils.MULTIPLE_COLUMN_SEPARATOR + "FileName_Objects_Nuclei_Labels" );
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.Label,"Number_Object_Number" );
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.X, "Location_Center_X" );
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.Y, "Location_Center_Y" );

		final ArrayList< DefaultAnnotatedImageSegment > annotatedImageSegments
				= TableUtils.segmentsFromTableFile(
					tableFile,
					null,
					coordinateToColumnNameAndIndexMap, new DefaultImageSegmentBuilder() );

		return annotatedImageSegments;
	}

}
