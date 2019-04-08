package de.embl.cba.tables.modelview.views;

import bdv.util.Bdv;
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

public class DefaultTableAndBdvViews
{
	private final List< TableRowImageSegment > tableRowImageSegments;
	private final ImageSourcesModel imageSourcesModel;
	private ImageSegmentsBdvView imageSegmentsBdvView;
	private TableRowsTableView tableRowsTableView;

	public DefaultTableAndBdvViews(
			List< TableRowImageSegment > tableRowImageSegments,
			ImageSourcesModel imageSourcesModel )
	{
		this( tableRowImageSegments, imageSourcesModel, null );
	}

	public DefaultTableAndBdvViews(
			List< TableRowImageSegment > tableRowImageSegments,
			ImageSourcesModel imageSourcesModel,
			BdvHandle bdv )
	{
		this.tableRowImageSegments = tableRowImageSegments;
		this.imageSourcesModel = imageSourcesModel;
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
				= new DefaultImageSegmentsModel<>( tableRowImageSegments );

		final DefaultTableRowsModel< TableRowImageSegment > tableRowsModel
				= new DefaultTableRowsModel<>( tableRowImageSegments );

		imageSegmentsBdvView = new ImageSegmentsBdvView(
				imageSourcesModel,
				imageSegmentsModel,
				selectionModel,
				selectionColoringModel,
				bdv );

		tableRowsTableView = new TableRowsTableView(
				tableRowsModel,
				selectionModel,
				selectionColoringModel );
	}

	public ImageSegmentsBdvView getImageSegmentsBdvView()
	{
		return imageSegmentsBdvView;
	}

	public TableRowsTableView getTableRowsTableView()
	{
		return tableRowsTableView;
	}
}
