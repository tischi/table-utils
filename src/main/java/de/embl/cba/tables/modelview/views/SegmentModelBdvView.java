package de.embl.cba.tables.modelview.views;

import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import de.embl.cba.bdv.utils.selection.BdvLabelSourceSelectionListener;
import de.embl.cba.bdv.utils.selection.BdvSelectionEventHandler;
import de.embl.cba.bdv.utils.sources.SelectableARGBConvertedRealSource;
import de.embl.cba.tables.modelview.datamodels.LabelImageSource;
import de.embl.cba.tables.modelview.datamodels.SegmentModel;
import de.embl.cba.tables.modelview.objects.Segment;
import de.embl.cba.tables.modelview.selection.SelectionListener;
import de.embl.cba.tables.modelview.selection.SelectionModel;

public class SegmentModelBdvView< T extends Segment >
{
	private final SegmentModel< T > segmentModel;
	private final SelectionModel< T > selectionModel;
	private final BdvSelectionEventHandler bdvSelectionEventHandler;

	public SegmentModelBdvView( final SegmentModel< T > segmentModel,
								final SelectionModel< T > selectionModel )
	{
		this.segmentModel = segmentModel;
		this.selectionModel = selectionModel;

		final BdvHandle bdv = show( segmentModel.getLabelImageSource() );

		addSelectionListener( selectionModel );

		bdvSelectionEventHandler = new BdvSelectionEventHandler(
				bdv,
				( SelectableARGBConvertedRealSource ) segmentModel.getLabelImageSource().getSource() );

		bdvSelectionEventHandler.addSelectionEventListener(

				new BdvLabelSourceSelectionListener()
				{
					@Override
					public void selectionChanged( double label, int timePoint, boolean selected )
					{
						final Segment segment = segmentModel.getSegment( label, timePoint );
						selectionModel.setSelected( ( T ) segment, selected );
					}
				}

		);

	}

	public void addSelectionListener( SelectionModel< T > selectionModel )
	{
		selectionModel.listeners().add( new SelectionListener()
		{
			@Override
			public void selectionChanged()
			{

			}

			@Override
			public void selectionChanged( Object selection, boolean selected )
			{

			}
		} );
	}

	public BdvHandle show( LabelImageSource labelImageSource )
	{
		BdvOptions options = BdvOptions.options();

		if ( labelImageSource.is2D() )
		{
			options = options.is2D();
		}

		return BdvFunctions.show( labelImageSource.getSource(), options ).getBdvHandle();
	}


}
