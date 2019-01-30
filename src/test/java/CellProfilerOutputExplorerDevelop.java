import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.images.CellProfilerImageSourcesCreator;
import de.embl.cba.tables.modelview.combined.DataModelUtils;
import de.embl.cba.tables.modelview.images.CellProfilerImageSourcesModel;
import de.embl.cba.tables.modelview.combined.SegmentUtils;
import de.embl.cba.tables.modelview.segments.DefaultAnnotatedImageSegment;
import de.embl.cba.tables.modelview.segments.ImageSegmentCoordinate;
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

		final File tableFile = new File("/Users/tischer/Documents/daja-schichler-nucleoli-segmentation--data/20190116_for_classification_interphase_versus_mitotic/concatenated_tables/merged_images_nuclei.txt");

//		final File file = new File("/Volumes/cba/exchange/Daja-Christian/20190116_for_classification_interphase_versus_mitotic/concatenated_tables/merged_images_nuclei.txt" );
		//final JTable table = TableUtils.loadTable( file, "\t" );

		final CellProfilerImageSourcesCreator parser = new CellProfilerImageSourcesCreator(
				tableFile,
				"/Volumes/cba/exchange/Daja-Christian",
				"/Users/tischer/Documents/daja-schichler-nucleoli-segmentation--data",
				"\t"
		);

		final CellProfilerImageSourcesModel imageSourcesModel = parser.getImageSourcesModel();

		final ArrayList< DefaultAnnotatedImageSegment > annotatedImageSegments = createCellProfilerImageSegments( tableFile );

		final ArrayList< String > categoricalColumns = new ArrayList<>();
		categoricalColumns.add( "Label" );

		DataModelUtils.buildModelsAndViews(
				imageSourcesModel,
				annotatedImageSegments,
				categoricalColumns,
				false );

	}

	public static ArrayList< DefaultAnnotatedImageSegment > createCellProfilerImageSegments( File tableFile )
	{

		final HashMap< ImageSegmentCoordinate, ValuePair< String, Integer > > coordinateToColumnNameAndIndexMap = new HashMap<>();
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.ImageId,
				new ValuePair( "ImageNumber" + SegmentUtils.SEVERAL_COLUMN_SEPARATOR + "FileName_Objects_Nuclei_Grayscale",  null ) );
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.Label, new ValuePair( "Number_Object_Number",  null ) );
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.X, new ValuePair("Location_Center_X", null ) );
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.Y, new ValuePair("Location_Center_Y", null ) );

		final ArrayList< DefaultAnnotatedImageSegment > annotatedImageSegments
				= TableUtils.segmentsFromTableFile(
					tableFile,
					null,
					coordinateToColumnNameAndIndexMap );

		return annotatedImageSegments;
	}

}
