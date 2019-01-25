package de.embl.cba.tables.modelview.views;

import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import bdv.viewer.Source;
import de.embl.cba.bdv.utils.BdvUtils;
import de.embl.cba.bdv.utils.selection.Segment;
import de.embl.cba.bdv.utils.sources.ARGBConvertedRealSource;
import de.embl.cba.tables.modelview.coloring.FeatureColoringModel;
import de.embl.cba.tables.modelview.datamodels.LabelImageSourceModel;
import de.embl.cba.tables.modelview.datamodels.DefaultAnnotatedSegmentsModel;
import de.embl.cba.tables.modelview.objects.AnnotatedSegment;
import de.embl.cba.tables.modelview.selection.SelectionListener;
import de.embl.cba.tables.modelview.selection.SelectionModel;
import org.scijava.ui.behaviour.ClickBehaviour;
import org.scijava.ui.behaviour.io.InputTriggerConfig;
import org.scijava.ui.behaviour.util.Behaviours;

import static de.embl.cba.bdv.utils.converters.SelectableVolatileARGBConverter.BACKGROUND;

public class SegmentsBdvView < T extends Segment >
{
	private String selectTrigger = "ctrl button1";
	private String selectNoneTrigger = "ctrl Q";
	private String iterateSelectionModeTrigger = "ctrl S";
	private String viewIn3DTrigger = "ctrl shift button1";

	private final DefaultAnnotatedSegmentsModel segmentsModel;
	private final SelectionModel< AnnotatedSegment > selectionModel;
	private final FeatureColoringModel< AnnotatedSegment > coloringModel;
	private Behaviours behaviours;

	private final BdvHandle bdv;
	private final Source source;


	// TODO: extract interface from DefaultAnnotatedSegmentsModel
	public SegmentsBdvView( final DefaultAnnotatedSegmentsModel segmentsModel,
							final SelectionModel< AnnotatedSegment > selectionModel,
							final FeatureColoringModel< AnnotatedSegment > coloringModel )
	{
		this.segmentsModel = segmentsModel;
		this.selectionModel = selectionModel;
		this.coloringModel = coloringModel;

		this.source = segmentsModel.getLabelImageSourceModel().getSource();

		bdv = showLabelSourceInBdv( segmentsModel.getLabelImageSourceModel() );

		addSelectionListener( selectionModel );

		installBdvBehaviours();
	}

	public void addSelectionListener( SelectionModel< ? extends Segment > selectionModel )
	{

		selectionModel.listeners().add( new SelectionListener< Segment >()
		{
			@Override
			public void selectionChanged()
			{
				//selectableConverter.setSelected( selectionModel.getSelected() );
				BdvUtils.repaint( bdv );
			}

			@Override
			public void selectionEvent( Segment selection, boolean selected )
			{
				BdvUtils.moveToPosition(
							bdv,
							selection.getPosition(),
							selection.getTimePoint(),
							500 );
			}
		} );
	}

	public BdvHandle showLabelSourceInBdv( LabelImageSourceModel labelImageSourceModel )
	{
		final AnnotatedSegmentsColoringARGBConverter coloringConverter =
				new AnnotatedSegmentsColoringARGBConverter(
						segmentsModel,
						coloringModel );

		final ARGBConvertedRealSource argbConvertedLabelSource = new ARGBConvertedRealSource(
				segmentsModel.getLabelImageSourceModel().getSource(),
				coloringConverter );

		BdvOptions options = BdvOptions.options();

		if ( labelImageSourceModel.is2D() )
		{
			options = options.is2D();
		}

		final BdvHandle bdvHandle = BdvFunctions.show( argbConvertedLabelSource, options ).getBdvHandle();

		bdvHandle.getViewerPanel().addTimePointListener( coloringConverter );

		return bdvHandle;
	}

	private void installBdvBehaviours()
	{
		behaviours = new Behaviours( new InputTriggerConfig() );
		behaviours.install(
				bdv.getBdvHandle().getTriggerbindings(),
				segmentsModel.getLabelImageSourceModel().getSource().getName() + "-bdv-selection-handler" );

		installSelectionBehaviour( );
		installSelectNoneBehaviour( );
		//installSelectionModeIterationBehaviour( );
		//installRandomColorShufflingBehaviour();

		//if( is3D() ) install3DViewBehaviour();
	}


	private void installSelectNoneBehaviour( )
	{
		behaviours.behaviour( ( ClickBehaviour ) ( x, y ) ->
		{
			if ( BdvUtils.isActive( bdv, source ) )
			{
				selectNone();
			}
		}, source.getName() + "-select-none", selectNoneTrigger );
	}

	public void selectNone()
	{
		selectionModel.clearSelection( );

		//selectableConverter.setSelected( selectionModel.getSelected() );

		BdvUtils.repaint( bdv );
	}

	private void installSelectionBehaviour()
	{
		behaviours.behaviour( ( ClickBehaviour ) ( x, y ) ->
		{
			if ( BdvUtils.isActive( bdv, source ) )
			{
				toggleSelectionAtMousePosition();
			}
		}, source.getName()+"-toggle-selection", selectTrigger ) ;
	}

	private void toggleSelectionAtMousePosition()
	{
		final double label = BdvUtils.getValueAtGlobalCoordinates(
				source,
				BdvUtils.getGlobalMouseCoordinates( bdv ),
				getCurrentTimePoint() );

		if ( label == BACKGROUND ) return;

		final int timePoint = getCurrentTimePoint();

		selectionModel.toggle( segmentsModel.getSegment( label, timePoint ) );

		//selectableConverter.setSelected( selectionModel.getSelected() );

		BdvUtils.repaint( bdv );
	}

	private int getCurrentTimePoint()
	{
		return bdv.getBdvHandle().getViewerPanel().getState().getCurrentTimepoint();
	}




}
