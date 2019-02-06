import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.images.PlatynereisImageSourcesModel;
import de.embl.cba.tables.modelview.images.PlatynereisImageSourcesModelFactory;
import de.embl.cba.tables.modelview.segments.DefaultTableRowImageSegment;
import de.embl.cba.tables.modelview.segments.DefaultImageSegmentBuilder;
import de.embl.cba.tables.modelview.segments.ImageSegmentCoordinate;

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
		//final File file = new File( TestTableLoading.class.getResource( "cellprofiler-table.txt" ).fileColumn() );

//		final File tableFile = new File("/Users/tischer/Documents/daja-schichler-nucleoli-segmentation--data/20190116_for_classification_interphase_versus_mitotic/concatenated_tables/merged_images_nuclei.txt");

//		final File file = new File("/Volumes/cba/exchange/Daja-Christian/20190116_for_classification_interphase_versus_mitotic/concatenated_tables/merged_images_nuclei.txt" );
		//final JTable table = TableUtils.loadTable( file, "\t" );


		/**
		 * Create AnnotatedSegments
		 */

		final File cellTable =
				new File( "/Volumes/arendt/EM_6dpf_segmentation/EM-Prospr/label_attributes/em-segmented-cells-labels-morphology-v2.csv" );

		final ArrayList< DefaultTableRowImageSegment > annotatedImageSegments
				= createCellSegments( cellTable );

		/**
		 * Create ImageSourcesModel
		 */
		final File directory = new File( "/Volumes/arendt/EM_6dpf_segmentation/EM-Prospr" );


		final PlatynereisImageSourcesModel imageSourcesModel
				= new PlatynereisImageSourcesModelFactory( directory ).getModel();

		final ArrayList< String > categoricalColumns = new ArrayList<>();
		categoricalColumns.add( "label_id" );

		ArrayList< String > initialSources = new ArrayList< String >();
		initialSources.add( "em-raw-full-res" );
		initialSources.add( "em-segmented-cells-labels" );

		// TODO
	}

	public static ArrayList< DefaultTableRowImageSegment > createCellSegments( File tableFile )
	{
		final HashMap< ImageSegmentCoordinate, String > coordinateToColumnMap = new HashMap<>();
		coordinateToColumnMap.put( ImageSegmentCoordinate.LabelId, "label_id" );
		coordinateToColumnMap.put( ImageSegmentCoordinate.X, "com_x_microns" );
		coordinateToColumnMap.put( ImageSegmentCoordinate.Y, "com_y_microns" );
		coordinateToColumnMap.put( ImageSegmentCoordinate.Z, "com_z_microns" );

		final DefaultImageSegmentBuilder segmentBuilder = new DefaultImageSegmentBuilder();

		segmentBuilder.setImageId( "em-segmented-cells-labels" );

		final ArrayList< DefaultTableRowImageSegment > annotatedImageSegments
				= TableUtils.segmentsFromTableFileColumnWise(
					tableFile,
					null,
					coordinateToColumnMap,
					segmentBuilder );

		return annotatedImageSegments;
	}

}
