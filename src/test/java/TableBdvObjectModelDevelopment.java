import de.embl.cba.bdv.utils.sources.SelectableARGBConvertedRealSource;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.datamodels.LabelImageSourceModel;
import de.embl.cba.tables.modelview.datamodels.DefaultSegmentWithFeaturesModel;
import de.embl.cba.tables.modelview.objects.DefaultSegmentWithFeatures;
import de.embl.cba.tables.modelview.objects.SegmentWithFeatures;
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

		final SelectableARGBConvertedRealSource source = new SelectableARGBConvertedRealSource( Examples.load2D16BitLabelMask() );

		final LabelImageSourceModel labelImageSourceModel = new LabelImageSourceModel( source, true );

		final File tableFile = new File( Examples.class.getResource( "2d-16bit-labelMask-Morphometry.csv" ).getFile() );


		final ArrayList< DefaultSegmentWithFeatures > segments = new ArrayList<>();

		final ArrayList< DefaultSegmentWithFeatures > segmentsWithFeatures = TableUtils.segmentsFromTableFile(
				tableFile,
				",",
				null,
				"Label",
				null,
				"X",
				"Y",
				null );

		final DefaultSegmentWithFeaturesModel model =
				new DefaultSegmentWithFeaturesModel(
				"MyModel",
				segmentsWithFeatures,
						"Label",
				null,
				labelImageSourceModel );


		final SelectionModel< SegmentWithFeatures > selectionModel = new DefaultSelectionModel<>();

		final SegmentsBdvView segmentsBdvView = new SegmentsBdvView( model, selectionModel );
		final SegmentsTableView tableView = new SegmentsTableView( model, selectionModel );

	}
}
