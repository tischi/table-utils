package de.embl.cba.tables.modelview.views;

import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.tables.modelview.coloring.DynamicCategoryColoringModel;
import de.embl.cba.tables.modelview.coloring.SelectionColoringModel;
import de.embl.cba.tables.modelview.combined.DefaultImageSegmentsModel;
import de.embl.cba.tables.modelview.combined.DefaultTableRowsModel;
import de.embl.cba.tables.modelview.images.ImageSourcesModel;
import de.embl.cba.tables.modelview.segments.ColumnBasedTableRowImageSegment;
import de.embl.cba.tables.modelview.selection.DefaultSelectionModel;
import de.embl.cba.tables.modelview.selection.SelectionModel;

import java.util.List;

public class DefaultViews
{
	private final List< ColumnBasedTableRowImageSegment > tableRowImageSegments;
	private final ImageSourcesModel imageSourcesModel;
	private ImageSegmentsBdvView imageSegmentsBdvView;
	private TableRowsTableView tableRowsTableView;

	public DefaultViews(
			List< ColumnBasedTableRowImageSegment > tableRowImageSegments,
			ImageSourcesModel imageSourcesModel )
	{
		this.tableRowImageSegments = tableRowImageSegments;
		this.imageSourcesModel = imageSourcesModel;
		show();
	}

	private void show( )
	{
		final SelectionModel< ColumnBasedTableRowImageSegment > selectionModel
				= new DefaultSelectionModel<>();

		final DynamicCategoryColoringModel< ColumnBasedTableRowImageSegment > coloringModel
				= new DynamicCategoryColoringModel<>( new GlasbeyARGBLut(), 50 );

		final SelectionColoringModel< ColumnBasedTableRowImageSegment > selectionColoringModel
				= new SelectionColoringModel<>(
					coloringModel,
					selectionModel );

		final DefaultImageSegmentsModel< ColumnBasedTableRowImageSegment > imageSegmentsModel
				= new DefaultImageSegmentsModel<>( tableRowImageSegments );

		final DefaultTableRowsModel< ColumnBasedTableRowImageSegment > tableRowsModel
				= new DefaultTableRowsModel<>( tableRowImageSegments );

		imageSegmentsBdvView = new ImageSegmentsBdvView(
				imageSourcesModel,
				imageSegmentsModel,
				selectionModel,
				selectionColoringModel );

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
