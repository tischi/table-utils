package de.embl.cba.tables.modelview.views.combined;

import bdv.util.BdvHandle;
import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.tables.modelview.coloring.LazyCategoryColoringModel;
import de.embl.cba.tables.modelview.coloring.SelectionColoringModel;
import de.embl.cba.tables.modelview.images.ImageSourcesModel;
import de.embl.cba.tables.modelview.segments.TableRowImageSegment;
import de.embl.cba.tables.modelview.selection.DefaultSelectionModel;
import de.embl.cba.tables.modelview.selection.SelectionModel;
import de.embl.cba.tables.modelview.views.Segments3dView;
import de.embl.cba.tables.modelview.views.SegmentsBdvView;
import de.embl.cba.tables.modelview.views.TableRowsTableView;
import ij3d.Image3DUniverse;

import java.util.List;

public class SegmentsTableBdvAnd3dViews
{
	private final List< TableRowImageSegment > tableRowImageSegments;
	private final ImageSourcesModel imageSourcesModel;
	private final String viewName;
	private SegmentsBdvView< TableRowImageSegment > segmentsBdvView;
	private TableRowsTableView< TableRowImageSegment > tableRowsTableView;
	private Segments3dView< TableRowImageSegment > segments3dView;

	public SegmentsTableBdvAnd3dViews(
			List< TableRowImageSegment > tableRowImageSegments,
			ImageSourcesModel imageSourcesModel,
			String viewName )
	{
		this( tableRowImageSegments, imageSourcesModel, viewName, null, null );
	}

	public SegmentsTableBdvAnd3dViews(
			List< TableRowImageSegment > tableRowImageSegments,
			ImageSourcesModel imageSourcesModel,
			String viewName,
			BdvHandle bdv,
			Image3DUniverse universe )
	{
		this.tableRowImageSegments = tableRowImageSegments;
		this.imageSourcesModel = imageSourcesModel;
		this.viewName = viewName;
		show( bdv, universe );
	}

	private void show( BdvHandle bdv, Image3DUniverse universe )
	{
		final SelectionModel< TableRowImageSegment > selectionModel
				= new DefaultSelectionModel<>();

		final LazyCategoryColoringModel< TableRowImageSegment > coloringModel
				= new LazyCategoryColoringModel<>( new GlasbeyARGBLut( 255 ) );

		final SelectionColoringModel< TableRowImageSegment > selectionColoringModel
				= new SelectionColoringModel<>(
					coloringModel,
					selectionModel );

		segmentsBdvView = new SegmentsBdvView<>(
				tableRowImageSegments,
				selectionModel,
				selectionColoringModel,
				imageSourcesModel,
				bdv );

		tableRowsTableView = new TableRowsTableView<>(
				tableRowImageSegments,
				selectionModel,
				selectionColoringModel,
				viewName );

		segments3dView = new Segments3dView<>(
				tableRowImageSegments,
				selectionModel,
				selectionColoringModel,
				imageSourcesModel,
				universe
		);


	}

	public SegmentsBdvView< TableRowImageSegment > getSegmentsBdvView()
	{
		return segmentsBdvView;
	}

	public TableRowsTableView< TableRowImageSegment > getTableRowsTableView()
	{
		return tableRowsTableView;
	}

	public Segments3dView< TableRowImageSegment > getSegments3dView()
	{
		return segments3dView;
	}

	/**
	 * TODO
	 * - I am not sure this is useful or necessary.
	 * - All the views implement listeners
	 */
	public void close()
	{
		segmentsBdvView.close();
		tableRowsTableView.close();
		segments3dView.close();

		segmentsBdvView = null;
		tableRowsTableView = null;
		segments3dView = null;

		System.gc();
	}
}
