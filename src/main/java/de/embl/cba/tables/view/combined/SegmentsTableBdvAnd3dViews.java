package de.embl.cba.tables.view.combined;

import bdv.util.BdvHandle;
import bdv.viewer.ViewerFrame;
import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.tables.color.LazyCategoryColoringModel;
import de.embl.cba.tables.color.SelectionColoringModel;
import de.embl.cba.tables.image.ImageSourcesModel;
import de.embl.cba.tables.tablerow.TableRowImageSegment;
import de.embl.cba.tables.select.DefaultSelectionModel;
import de.embl.cba.tables.select.SelectionModel;
import de.embl.cba.tables.view.Segments3dView;
import de.embl.cba.tables.view.SegmentsBdvView;
import de.embl.cba.tables.view.TableRowsTableView;
import ij3d.Image3DUniverse;

import java.awt.*;
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


		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		// TODO: put to BdvUtils
		final ViewerFrame viewerFrame =
				( ViewerFrame ) segmentsBdvView.getBdv().getViewerPanel().getParent().getParent().getParent().getParent();

		viewerFrame.setLocation(
				screenSize.width / 2 - viewerFrame.getWidth() / 2,
				0 + 50 );

		tableRowsTableView = new TableRowsTableView<>(
				tableRowImageSegments,
				selectionModel,
				selectionColoringModel,
				viewName );


		tableRowsTableView.setParentComponent( viewerFrame );

		tableRowsTableView.showTableAndMenu();

		segments3dView = new Segments3dView<>(
				tableRowImageSegments,
				selectionModel,
				selectionColoringModel,
				imageSourcesModel,
				universe
		);

		segments3dView.setParentComponent( segmentsBdvView.getBdv().getViewerPanel() );



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
	 * - All the view implement listeners
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
