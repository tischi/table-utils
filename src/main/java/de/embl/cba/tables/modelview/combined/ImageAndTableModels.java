package de.embl.cba.tables.modelview.combined;

import de.embl.cba.tables.modelview.images.ImageSourcesModel;
import de.embl.cba.tables.modelview.segments.DefaultTableRowImageSegment;

import java.util.ArrayList;

public class ImageAndTableModels
{
	/**
	 * Builds derived tablemodels and views from the inputs.
	 *  @param imageSourcesModel
	 * @param annotatedImageSegments
	 * @param categoricalColumns
	 * @param initialSources
	 */
	public static void buildModelsAndViews(
			ImageSourcesModel imageSourcesModel,
			ArrayList< DefaultTableRowImageSegment > annotatedImageSegments,
			ArrayList< String > categoricalColumns )
	{
//		final AnnotatedImageSegmentsModel imageSegmentsModel =
//				new AnnotatedImageSegmentsModel(
//						"Data",
//						annotatedImageSegments,
//						imageSourcesModel );
//
//		new DefaultImageSegmentsModel< AnnotatedImageSegment >( annotatedImageSegments )
//
//		final SelectionModel< AnnotatedImageSegment > selectionModel
//				= new DefaultSelectionModel<>();
//
//		final DynamicCategoryColoringModel< AnnotatedImageSegment > coloringModel
//				= new DynamicCategoryColoringModel<>( new GlasbeyARGBLut(), 50 );
//
//		final SelectionColoringModel< AnnotatedImageSegment > selectionColoringModel
//				= new SelectionColoringModel<>(
//				coloringModel,
//				selectionModel );
//
//		final ImageSegmentsBdvView imageSegmentsBdvView =
//				new ImageSegmentsBdvView(
//						imageSourcesModel,
//						imageSegmentsModel,
//						selectionModel,
//						selectionColoringModel );
//
//
//		final TableRowsTableView tableView = new TableRowsTableView(
//				imageSegmentsModel,
//				selectionModel,
//				selectionColoringModel,
//				categoricalColumns );
	}


}
