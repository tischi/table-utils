package de.embl.cba.tables.modelview.views.bdv;

import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import bdv.viewer.DisplayMode;
import bdv.viewer.Source;
import bdv.viewer.VisibilityAndGrouping;
import bdv.viewer.state.SourceGroup;
import de.embl.cba.bdv.utils.BdvUtils;
import de.embl.cba.bdv.utils.sources.ARGBConvertedRealSource;
import de.embl.cba.tables.modelview.coloring.ColoringListener;
import de.embl.cba.tables.modelview.coloring.ColoringModel;
import de.embl.cba.tables.modelview.coloring.SelectionColoringModel;
import de.embl.cba.tables.modelview.datamodels.ImageSourcesModel;
import de.embl.cba.tables.modelview.datamodels.ImagesAndSegmentsModel;
import de.embl.cba.tables.modelview.objects.ImageSegment;
import de.embl.cba.tables.modelview.selection.SelectionListener;
import de.embl.cba.tables.modelview.selection.SelectionModel;
import de.embl.cba.tables.modelview.views.ImageSegmentLabelsARGBConverter;
import net.imglib2.type.numeric.RealType;
import org.scijava.ui.behaviour.ClickBehaviour;
import org.scijava.ui.behaviour.io.InputTriggerConfig;
import org.scijava.ui.behaviour.util.Behaviours;

import java.util.HashMap;
import java.util.List;

import static de.embl.cba.bdv.utils.converters.SelectableVolatileARGBConverter.BACKGROUND;

public class ImageSegmentsBdvView < T extends ImageSegment >
{
	private String selectTrigger = "ctrl button1";
	private String selectNoneTrigger = "ctrl N";
	private String alterCategoricalLutRandomSeedTrigger = "ctrl L";
	private String iterateSelectionModeTrigger = "ctrl S";
	private String viewIn3DTrigger = "ctrl shift button1";

	private final ImagesAndSegmentsModel< T > imagesAndSegmentsModel;
	private final SelectionModel< T > selectionModel;
	private final SelectionColoringModel< T > selectionColoringModel;
	private Behaviours behaviours;

	private BdvHandle bdv;
	private String name = "TODO";
	private int bdvGroupId;
	private HashMap< String, Integer > imageSetIdToBdvGroupIdxMap;
	private BdvOptions bdvOptions;

	public ImageSegmentsBdvView(
			final ImagesAndSegmentsModel< T > imagesAndSegmentsModel,
			final SelectionModel< T > selectionModel,
			final SelectionColoringModel< T > selectionColoringModel )
	{
		this.imagesAndSegmentsModel = imagesAndSegmentsModel;
		this.selectionModel = selectionModel;
		this.selectionColoringModel = selectionColoringModel;

		showFirstImageSetAndConfigureBdv(
				imagesAndSegmentsModel.getImageSourcesModel(),
				selectionColoringModel );

		bdvOptions = bdvOptions.addTo( bdv );

		registerAsSelectionListener( selectionModel );

		registerAsColoringListener( selectionColoringModel );

		installBdvBehaviours();
	}

	public void registerAsColoringListener( ColoringModel< T > coloringModel )
	{
		coloringModel.listeners().add( new ColoringListener()
		{
			@Override
			public void coloringChanged()
			{
				BdvUtils.repaint( bdv );
			}
		} );
	}

	public void registerAsSelectionListener( SelectionModel< ? extends ImageSegment > selectionModel )
	{
		selectionModel.listeners().add( new SelectionListener< ImageSegment >()
		{
			@Override
			public void selectionChanged()
			{
				BdvUtils.repaint( bdv );
			}

			@Override
			public void selectionEvent( ImageSegment selection, boolean selected )
			{
				if ( selected )
				{
					centerBdvViewOnSegment( selection );
				}
			}
		} );
	}

	public void centerBdvViewOnSegment( ImageSegment selection )
	{
		final double[] position = new double[ 3 ];
		selection.localize( position );

		adaptGroupVisibility( selection );

		BdvUtils.moveToPosition(
				bdv,
				position,
				selection.timePoint(),
				500 );
	}

	private void adaptGroupVisibility( ImageSegment selection )
	{
		final int currentGroup = getCurrentGroup();
		final String currentImageSetName = imagesAndSegmentsModel.getImageSourcesModel().getImageSetIds().get( currentGroup );

		if ( ! currentImageSetName.equals( selection.imageSetName() ) )
		{
			final int selectedGroup = imagesAndSegmentsModel.getImageSourcesModel().getImageSetIds().indexOf( selection.imageSetName() );
			//bdv.getViewerPanel().getState().setCurrentGroup( selectedGroup );
			bdv.getViewerPanel().getVisibilityAndGrouping().setGroupActive( selectedGroup, true );
		}
		;
	}

	private int getCurrentGroup()
	{
		return bdv.getViewerPanel().getVisibilityAndGrouping().getCurrentGroup();
	}

	private void showFirstImageSetAndConfigureBdv(
			ImageSourcesModel imageSourcesModel,
			SelectionColoringModel< T > selectionColoringModel )
	{
		initBdvOptions( imageSourcesModel );

		final String imageSetId = imageSourcesModel.getImageSetIds().get( 0 );

		showImageSet( imageSourcesModel, imageSetId, selectionColoringModel );

		configureGrouping();
	}

	private void configureGrouping()
	{
		final List<SourceGroup> groups = this.bdv.getViewerPanel().getState().getSourceGroups();
		for (final SourceGroup g : groups) {
			this.bdv.getViewerPanel().getState().removeGroup(g);
		}

		final VisibilityAndGrouping visibilityAndGrouping = bdv.getViewerPanel().getVisibilityAndGrouping();
		visibilityAndGrouping.setGroupingEnabled( true );
		visibilityAndGrouping.setDisplayMode( DisplayMode.GROUP );
	}

	private void showImageSet( ImageSourcesModel imageSourcesModel, String imageSetId, SelectionColoringModel< T > selectionColoringModel )
	{
		final SourceGroup sourceGroup = new SourceGroup( imageSetId );

		showLabelSource( imageSourcesModel, imageSetId, selectionColoringModel, sourceGroup );

		for ( Source intensitySource : imageSourcesModel.getIntensityImageSources( imageSetId ) )
		{
			sourceGroup.addSource( bdv.getViewerPanel().getState().numSources()  );
			BdvFunctions.show( intensitySource, bdvOptions );
		}

		bdv.getViewerPanel().addGroup( sourceGroup );
	}

	private void initBdvOptions( ImageSourcesModel imageSourcesModel )
	{
		// TODO: is it correct to already here specify the number of groups?

		bdvOptions = BdvOptions.options()
				.numSourceGroups( imageSourcesModel.getImageSetIds().size() );

		bdvOptions = bdvOptions.addTo( bdv );

		if ( imageSourcesModel.is2D() ) bdvOptions = bdvOptions.is2D();
	}

	private void showLabelSource( ImageSourcesModel imageSourcesModel, String imageSetId, SelectionColoringModel< T > selectionColoringModel, SourceGroup sourceGroup )
	{
		sourceGroup.addSource( bdv.getViewerPanel().getState().numSources() );

		ImageSegmentLabelsARGBConverter labelSourcesARGBConverter =
			new ImageSegmentLabelsARGBConverter(
					imagesAndSegmentsModel,
					imageSetId,
					selectionColoringModel );

		final Source labelSource = imageSourcesModel.getLabelImageSource( imageSetId );

		Source argbConvertedLabelSource = new ARGBConvertedRealSource(
				labelSource,
				labelSourcesARGBConverter );

		bdv = BdvFunctions.show(
				argbConvertedLabelSource,
				bdvOptions ).getBdvHandle();

		bdv.getViewerPanel().addTimePointListener( labelSourcesARGBConverter );
	}

	private void installBdvBehaviours()
	{
		behaviours = new Behaviours( new InputTriggerConfig() );
		behaviours.install(
				bdv.getBdvHandle().getTriggerbindings(),
				name + "-bdv-selection-handler" );

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
			selectNone();

		}, name + "-select-none", selectNoneTrigger );
	}

	public void selectNone()
	{
		selectionModel.clearSelection( );

		BdvUtils.repaint( bdv );
	}

	private void installSelectionBehaviour()
	{
		behaviours.behaviour( ( ClickBehaviour ) ( x, y ) ->
		{
			toggleSelectionAtMousePosition();
		}, name + "-toggle-selection", selectTrigger ) ;
	}

	private void toggleSelectionAtMousePosition()
	{
		final int currentGroup = getCurrentGroup();

		final String currentImageSet = imagesAndSegmentsModel.getImageSourcesModel().getImageSetIds().get( currentGroup );

		final Source< ? extends RealType< ? > > labelImageSource = imagesAndSegmentsModel.getImageSourcesModel().getLabelImageSource( currentImageSet );

		final int timePoint = getCurrentTimePoint();

		final double label = BdvUtils.getValueAtGlobalCoordinates(
				labelImageSource,
				BdvUtils.getGlobalMouseCoordinates( bdv ),
				timePoint );

		if ( label == BACKGROUND ) return;

		selectionModel.toggle( imagesAndSegmentsModel.getSegment( currentImageSet, label, timePoint ) );

	}

	private int getCurrentTimePoint()
	{
		return bdv.getBdvHandle().getViewerPanel().getState().getCurrentTimepoint();
	}




}
