import de.embl.cba.bdv.utils.sources.SelectableARGBConvertedRealSource;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.coloring.AnnotatedSegmentsColoringModel;
import de.embl.cba.tables.modelview.coloring.DefaultAnnotatedSegmentsColoringModel;
import de.embl.cba.tables.modelview.coloring.FeatureColoringModel;
import de.embl.cba.tables.modelview.datamodels.LabelImageSourceModel;
import de.embl.cba.tables.modelview.datamodels.DefaultAnnotatedSegmentsModel;
import de.embl.cba.tables.modelview.objects.DefaultAnnotatedSegment;
import de.embl.cba.tables.modelview.objects.AnnotatedSegment;
import de.embl.cba.tables.modelview.selection.DefaultSelectionModel;
import de.embl.cba.tables.modelview.selection.SelectionModel;
import de.embl.cba.tables.modelview.views.SegmentsBdvView;
import de.embl.cba.tables.modelview.views.SegmentsTableView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TableBdvObjectModelDevelopment
{
	public static void main( String[] args ) throws IOException
	{


		final LabelImageSourceModel labelImageSourceModel = new LabelImageSourceModel(
				Examples.load2D16BitLabelMask(), true );

		final File tableFile = new File( Examples.class.getResource( "2d-16bit-labelMask-Morphometry.csv" ).getFile() );


		final ArrayList< DefaultAnnotatedSegment > segments = new ArrayList<>();

		final ArrayList< DefaultAnnotatedSegment > segmentsWithFeatures = TableUtils.segmentsFromTableFile(
				tableFile,
				",",
				null,
				"Label",
				null,
				"X",
				"Y",
				null );

		final DefaultAnnotatedSegmentsModel dataModel =
				new DefaultAnnotatedSegmentsModel(
				"MyModel",
				segmentsWithFeatures,
						"Label",
				null,
				labelImageSourceModel );


		final FeatureColoringModel< AnnotatedSegment > coloringModel =
				new DefaultAnnotatedSegmentsColoringModel( "Label" );
		final SelectionModel< AnnotatedSegment > selectionModel = new DefaultSelectionModel<>();

		final SegmentsBdvView segmentsBdvView = new SegmentsBdvView( dataModel, selectionModel, coloringModel );
		//final SegmentsTableView tableView = new SegmentsTableView( dataModel, selectionModel, coloringModel );

	}
}
