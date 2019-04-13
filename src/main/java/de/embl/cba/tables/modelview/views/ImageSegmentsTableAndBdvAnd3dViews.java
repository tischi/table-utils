package de.embl.cba.tables.modelview.views;

import bdv.util.BdvHandle;
import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.tables.modelview.coloring.LazyCategoryColoringModel;
import de.embl.cba.tables.modelview.coloring.SelectionColoringModel;
import de.embl.cba.tables.modelview.combined.DefaultImageSegmentsModel;
import de.embl.cba.tables.modelview.combined.DefaultTableRowsModel;
import de.embl.cba.tables.modelview.images.ImageSourcesModel;
import de.embl.cba.tables.modelview.segments.TableRowImageSegment;
import de.embl.cba.tables.modelview.selection.DefaultSelectionModel;
import de.embl.cba.tables.modelview.selection.SelectionModel;

import java.util.List;

public class ImageSegmentsTableAndBdvAnd3dViews
{
	private final List< TableRowImageSegment > tableRowImageSegments;
	private final ImageSourcesModel imageSourcesModel;
	private final String viewName;
	private ImageSegmentsBdvView< TableRowImageSegment > imageSegmentsBdvView;
	private TableRowsTableView< TableRowImageSegment > tableRowsTableView;

	public ImageSegmentsTableAndBdvAnd3dViews(
			List< TableRowImageSegment > tableRowImageSegments,
			ImageSourcesModel imageSourcesModel,
			String viewName )
	{
		this( tableRowImageSegments, imageSourcesModel, viewName, null );
	}

	public ImageSegmentsTableAndBdvAnd3dViews(
			List< TableRowImageSegment > tableRowImageSegments,
			ImageSourcesModel imageSourcesModel,
			String viewName,
			BdvHandle bdv )
	{
		this.tableRowImageSegments = tableRowImageSegments;
		this.imageSourcesModel = imageSourcesModel;
		this.viewName = viewName;
		show( bdv );
	}

	private void show( BdvHandle bdv )
	{
		final SelectionModel< TableRowImageSegment > selectionModel
				= new DefaultSelectionModel<>();

		final LazyCategoryColoringModel< TableRowImageSegment > coloringModel
				= new LazyCategoryColoringModel<>( new GlasbeyARGBLut( 255 ) );

		final SelectionColoringModel< TableRowImageSegment > selectionColoringModel
				= new SelectionColoringModel<>(
					coloringModel,
					selectionModel );

		final DefaultImageSegmentsModel< TableRowImageSegment > imageSegmentsModel
				= new DefaultImageSegmentsModel<>( tableRowImageSegments, viewName );

		final DefaultTableRowsModel< TableRowImageSegment > tableRowsModel
				= new DefaultTableRowsModel<>( tableRowImageSegments, viewName );

		imageSegmentsBdvView = new ImageSegmentsBdvView< TableRowImageSegment >(
				imageSourcesModel,
				imageSegmentsModel,
				selectionModel,
				selectionColoringModel,
				bdv );

		tableRowsTableView = new TableRowsTableView< TableRowImageSegment >(
				tableRowsModel,
				selectionModel,
				selectionColoringModel );
	}

	public ImageSegmentsBdvView< TableRowImageSegment > getImageSegmentsBdvView()
	{
		return imageSegmentsBdvView;
	}

	public TableRowsTableView< TableRowImageSegment > getTableRowsTableView()
	{
		return tableRowsTableView;
	}
}
