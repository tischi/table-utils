import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.coloring.DynamicCategoryColoringModel;
import de.embl.cba.tables.modelview.coloring.SelectionColoringModel;
import de.embl.cba.tables.modelview.datamodels.LabelImageSourceModel;
import de.embl.cba.tables.modelview.datamodels.AnnotatedImageSegmentsModel;
import de.embl.cba.tables.modelview.objects.DefaultAnnotatedImageSegment;
import de.embl.cba.tables.modelview.objects.AnnotatedImageSegment;
import de.embl.cba.tables.modelview.selection.DefaultSelectionModel;
import de.embl.cba.tables.modelview.selection.SelectionModel;
import de.embl.cba.tables.modelview.views.bdv.ImageSegmentsBdvView;
import de.embl.cba.tables.modelview.views.table.TableRowsTableView;
import de.embl.cba.tables.objects.SegmentCoordinate;
import net.imglib2.util.ValuePair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TableBdvObjectModelDevelopment
{
	public static void main( String[] args ) throws IOException
	{


		final LabelImageSourceModel labelImageSourceModel = new LabelImageSourceModel(
				Examples.load2D16BitLabelMask(), true );

		final File tableFile = new File( Examples.class.getResource( "2d-16bit-labelMask-Morphometry.csv" ).getFile() );


		final ArrayList< DefaultAnnotatedImageSegment > segments = new ArrayList<>();

		final HashMap< SegmentCoordinate, ValuePair< String, Integer > > coordinateToColumnNameMap = new HashMap<>();
		coordinateToColumnNameMap.put( SegmentCoordinate.Label, new ValuePair( "Label",  null ) );
		coordinateToColumnNameMap.put( SegmentCoordinate.X, new ValuePair("X", null ) );
		coordinateToColumnNameMap.put( SegmentCoordinate.Y, new ValuePair("Y", null ) );

		final ArrayList< DefaultAnnotatedImageSegment > segmentsWithFeatures =
				TableUtils.segmentsFromTableFile(
						tableFile,
						",",
						coordinateToColumnNameMap );

		final AnnotatedImageSegmentsModel dataModel =
				new AnnotatedImageSegmentsModel(
				"MyModel",
				segmentsWithFeatures,
						"Label",
				null,
				labelImageSourceModel );

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

		final TableRowsTableView tableView = new TableRowsTableView(
				dataModel,
				selectionModel,
				selectionColoringModel );

	}
}
