import bdv.util.RandomAccessibleIntervalSource;
import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.coloring.DynamicCategoryColoringModel;
import de.embl.cba.tables.modelview.coloring.SelectionColoringModel;
import de.embl.cba.tables.modelview.datamodels.DefaultImageSourcesModelOld;
import de.embl.cba.tables.modelview.datamodels.AnnotatedImageSegmentsAndImagesModel;
import de.embl.cba.tables.modelview.datamodels.ImageSourcesModel;
import de.embl.cba.tables.modelview.objects.DefaultAnnotatedImageSegment;
import de.embl.cba.tables.modelview.objects.AnnotatedImageSegment;
import de.embl.cba.tables.modelview.objects.DefaultImageSegmentBuilder;
import de.embl.cba.tables.modelview.selection.DefaultSelectionModel;
import de.embl.cba.tables.modelview.selection.SelectionModel;
import de.embl.cba.tables.modelview.views.bdv.ImageSegmentsBdvView;
import de.embl.cba.tables.modelview.views.table.TableRowsTableView;
import de.embl.cba.tables.modelview.objects.ImageSegmentCoordinate;
import net.imglib2.util.ValuePair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TableBdvObjectModelDevelopment
{
	public static void main( String[] args ) throws IOException
	{

		final DefaultImageSourcesModelOld defaultImageSourcesModelOld = new DefaultImageSourcesModelOld(
				Examples.load2D16BitLabelSource(), true );

		final RandomAccessibleIntervalSource labelSource = Examples.load2D16BitLabelSource();

		final ImageSourcesModel imageSourcesModel = new ImageSourcesModel( true );

		imageSourcesModel.addLabelImageSource( DefaultImageSegmentBuilder.getDefaultImageId(), labelSource );

		final File tableFile = new File( Examples.class.getResource( "2d-16bit-labelMask-Morphometry.csv" ).getFile() );

		final ArrayList< DefaultAnnotatedImageSegment > segments = new ArrayList<>();

		final HashMap< ImageSegmentCoordinate, ValuePair< String, Integer > > coordinateToColumnNameMap = new HashMap<>();
		coordinateToColumnNameMap.put( ImageSegmentCoordinate.Label, new ValuePair( "Label",  null ) );
		coordinateToColumnNameMap.put( ImageSegmentCoordinate.X, new ValuePair("X", null ) );
		coordinateToColumnNameMap.put( ImageSegmentCoordinate.Y, new ValuePair("Y", null ) );

		final ArrayList< DefaultAnnotatedImageSegment > annotatedImageSegments =
				TableUtils.segmentsFromTableFile(
						tableFile,
						",",
						coordinateToColumnNameMap );

		final AnnotatedImageSegmentsAndImagesModel dataModel =
				new AnnotatedImageSegmentsAndImagesModel(
						"MyModel",
						annotatedImageSegments,
						"Label",
						null,
						imageSourcesModel );

		final SelectionModel< AnnotatedImageSegment > selectionModel
				= new DefaultSelectionModel<>();

		final DynamicCategoryColoringModel< AnnotatedImageSegment > coloringModel
				= new DynamicCategoryColoringModel<>( new GlasbeyARGBLut(), 50 );

		final SelectionColoringModel< AnnotatedImageSegment > selectionColoringModel
				= new SelectionColoringModel<>(
					coloringModel,
					selectionModel );

		final ImageSegmentsBdvView imageSegmentsBdvView = new ImageSegmentsBdvView(
				dataModel,
				selectionModel,
				selectionColoringModel );

		final ArrayList< String > categoricalColumns = new ArrayList<>();
		categoricalColumns.add( "Label" );
		final TableRowsTableView tableView = new TableRowsTableView(
				dataModel,
				selectionModel,
				selectionColoringModel,
				categoricalColumns );

	}
}
