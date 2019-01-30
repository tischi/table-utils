import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.combined.DataModelUtils;
import de.embl.cba.tables.modelview.images.PlatynereisImageSourcesModel;
import de.embl.cba.tables.modelview.images.PlatynereisImageSourcesModelCreator;
import de.embl.cba.tables.modelview.segments.DefaultAnnotatedImageSegment;
import de.embl.cba.tables.modelview.segments.DefaultImageSegmentBuilder;
import de.embl.cba.tables.modelview.segments.ImageSegmentCoordinate;
import net.imglib2.util.ValuePair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class PlatyenereisExplorerDevelop
{

	public static final String DATASET_INDEX = "ImageNumber";
	public static final String FOLDER = "PathName_";
	public static final String FILE = "FileName_";
	private static ArrayList< String > columns;

	public static void main( String[] args ) throws IOException
	{
		//final File file = new File( TestTableLoading.class.getResource( "cellprofiler-table.txt" ).getFileColumn() );

//		final File tableFile = new File("/Users/tischer/Documents/daja-schichler-nucleoli-segmentation--data/20190116_for_classification_interphase_versus_mitotic/concatenated_tables/merged_images_nuclei.txt");

//		final File file = new File("/Volumes/cba/exchange/Daja-Christian/20190116_for_classification_interphase_versus_mitotic/concatenated_tables/merged_images_nuclei.txt" );
		//final JTable table = TableUtils.loadTable( file, "\t" );


		/**
		 * Create AnnotatedSegments
		 */

		final File cellTable =
				new File( "/Volumes/arendt/EM_6dpf_segmentation/EM-Prospr/label_attributes/em-segmented-cells-labels-morphology-v2.csv" );

		final ArrayList< DefaultAnnotatedImageSegment > annotatedImageSegments = createCellSegments( cellTable );

		/**
		 * Create ImageSourcesModel
		 */
		final File directory = new File( "/Volumes/arendt/EM_6dpf_segmentation/EM-Prospr" );

		final PlatynereisImageSourcesModelCreator sourcesModelCreator
				= new PlatynereisImageSourcesModelCreator( directory );

		final PlatynereisImageSourcesModel imageSourcesModel = sourcesModelCreator.getModel();

		final ArrayList< String > categoricalColumns = new ArrayList<>();
		categoricalColumns.add( "label_id" );


		ArrayList< String > initialSources = new ArrayList< String >();
		initialSources.add( "em-raw-full-res" );
		initialSources.add( "em-segmented-cells-labels" );

		DataModelUtils.buildModelsAndViews(
				imageSourcesModel,
				annotatedImageSegments,
				categoricalColumns,
				false,
				initialSources );

	}

	public static ArrayList< DefaultAnnotatedImageSegment > createCellSegments( File tableFile )
	{
		final HashMap< ImageSegmentCoordinate, ValuePair< String, Integer > > coordinateToColumnNameAndIndexMap = new HashMap<>();
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.Label, new ValuePair( "label_id",  null ) );
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.X, new ValuePair("com_x_microns", null ) );
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.Y, new ValuePair("com_y_microns", null ) );
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.Z, new ValuePair("com_z_microns", null ) );

		final DefaultImageSegmentBuilder segmentBuilder = new DefaultImageSegmentBuilder();

		segmentBuilder.setImageId( "em-segmented-cells-labels" );

		final ArrayList< DefaultAnnotatedImageSegment > annotatedImageSegments
				= TableUtils.segmentsFromTableFile(
					tableFile,
					null,
					coordinateToColumnNameAndIndexMap,
					segmentBuilder );

		return annotatedImageSegments;
	}

}
