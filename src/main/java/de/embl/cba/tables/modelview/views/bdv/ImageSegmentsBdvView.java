package de.embl.cba.tables.modelview.views.bdv;

import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import bdv.viewer.DisplayMode;
import bdv.viewer.Source;
import bdv.viewer.VisibilityAndGrouping;
import bdv.viewer.state.SourceGroup;
import bdv.viewer.state.SourceState;
import de.embl.cba.bdv.utils.BdvUtils;
import de.embl.cba.bdv.utils.sources.ARGBConvertedRealSource;
import de.embl.cba.tables.modelview.coloring.ColoringListener;
import de.embl.cba.tables.modelview.coloring.ColoringModel;
import de.embl.cba.tables.modelview.coloring.SelectionColoringModel;
import de.embl.cba.tables.modelview.datamodels.ImageSourcesModel;
import de.embl.cba.tables.modelview.datamodels.ImagesAndSegmentsModel;
import de.embl.cba.tables.modelview.datamodels.LabelSource;
import de.embl.cba.tables.modelview.objects.ImageSegment;
import de.embl.cba.tables.modelview.selection.SelectionListener;
import de.embl.cba.tables.modelview.selection.SelectionModel;
import de.embl.cba.tables.modelview.views.ImageSegmentLabelsARGBConverter;
import org.scijava.ui.behaviour.ClickBehaviour;
import org.scijava.ui.behaviour.io.InputTriggerConfig;
import org.scijava.ui.behaviour.util.Behaviours;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
	private HashMap< Integer, String > groupIdxToName;
	private HashMap< String, String > imageIdToGroupName;
	private ImageSourcesModel imageSourcesModel;
	private boolean isGroupingConfigured;

	public ImageSegmentsBdvView(
			final ImagesAndSegmentsModel< T > imagesAndSegmentsModel,
			final SelectionModel< T > selectionModel,
			final SelectionColoringModel< T > selectionColoringModel )
	{
		this.imagesAndSegmentsModel = imagesAndSegmentsModel;
		this.selectionModel = selectionModel;
		this.selectionColoringModel = selectionColoringModel;

		this.imageSourcesModel = imagesAndSegmentsModel.getImageSourcesModel();

		groupIdxToName = new HashMap<>(  );
		imageIdToGroupName = new HashMap<>(  );
		isGroupingConfigured = false;

		initBdvOptions( imageSourcesModel );
		showGroup( 0 );
		
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

		showSegmentGroup( selection );

		BdvUtils.moveToPosition(
				bdv,
				position,
				selection.timePoint(),
				500 );
	}

	private void showSegmentGroup( ImageSegment imageSegment )
	{
		if ( ! getCurrentGroupName().equals( getImageSegmentGroupName( imageSegment ) ) )
		{
			final int selectedGroup = getImageSegmentGroupIdx( imageSegment );

			showGroup( selectedGroup );
		}
	}

	private void showGroup( int selectedGroup )
	{
		if ( isEmpty( selectedGroup ) )
		{
			populateGroup( selectedGroup );
		}

		bdv.getViewerPanel().getVisibilityAndGrouping().setGroupActive( selectedGroup, true );
		bdv.getViewerPanel().getVisibilityAndGrouping().setCurrentGroup( selectedGroup );
	}

	private void populateGroup( int groupIdx )
	{
		final String groupName = groupIdxToName.get( groupIdx );

		final ArrayList< Source< ? > > sources = imageSourcesModel.getImageSources().get( groupName );

		for ( Source source : sources )
		{
			if ( source instanceof LabelSource )
			{
				Source labelSource = asLabelSource( groupName, source );

				bdv = BdvFunctions.show( labelSource, bdvOptions ).getBdvHandle();
			}
			else
			{
				bdv = BdvFunctions.show( source, bdvOptions ).getBdvHandle();
			}

			final VisibilityAndGrouping visibilityAndGrouping = bdv.getViewerPanel().getVisibilityAndGrouping();

			if ( ! isGroupingConfigured ) configureGrouping();

			visibilityAndGrouping.addSourceToGroup( bdv.getViewerPanel().getState().numSources() - 1, groupIdx );
		}
	}

	private Source asLabelSource( String groupName, Source source )
	{
		ImageSegmentLabelsARGBConverter labelSourcesARGBConverter =
				new ImageSegmentLabelsARGBConverter(
						imagesAndSegmentsModel,
						groupName, // TODO???
						selectionColoringModel );

		// TODO: this could be a SourceAndConverter
		return new ARGBConvertedRealSource(
				source,
				labelSourcesARGBConverter );
	}

	private boolean isEmpty( int selectedGroup )
	{
		if ( bdv == null ) return true;
		return bdv.getViewerPanel().getState().getSourceGroups().get( selectedGroup ).getSourceIds().size() == 0;
	}

	private String getImageSegmentGroupName( ImageSegment selection )
	{
		return imageIdToGroupName.get( selection.imageId() );
	}

	private Integer getImageSegmentGroupIdx( ImageSegment selection )
	{
		final HashMap< String, Integer > groupNameToIdx = new HashMap<>();
		return groupNameToIdx.get( imageIdToGroupName.get( selection.imageId() ) );
	}

	private String getCurrentGroupName()
	{
		return groupIdxToName.get( getCurrentGroupIdx() );
	}

	private int getCurrentGroupIdx()
	{
		return bdv.getViewerPanel().getVisibilityAndGrouping().getCurrentGroup();
	}


	private void configureGrouping()
	{
		final Set< String > imageSetNames = imageSourcesModel.getImageSources().keySet();

		final VisibilityAndGrouping visibilityAndGrouping = bdv.getViewerPanel().getVisibilityAndGrouping();

		int i = 0;
		for ( String imageSetName : imageSetNames )
		{
			if ( i == 0 )
			{
				// first group exists by default => only change name
				visibilityAndGrouping.setGroupName( 0, imageSetName );
			}
			else
			{
				bdv.getViewerPanel().addGroup( new SourceGroup( imageSetName ) );
				visibilityAndGrouping.setGroupName( i, imageSetName );
				groupIdxToName.put( i, imageSetName );
				imageIdToGroupName.put( imageSetName, imageSetName ); // TODO!
			}
		}

		visibilityAndGrouping.setGroupingEnabled( true );
		visibilityAndGrouping.setDisplayMode( DisplayMode.GROUP );

		isGroupingConfigured = true;
	}

	private void addMostRecentSourceToGroup( int groupIdx )
	{
		bdv.getViewerPanel().getVisibilityAndGrouping().addSourceToGroup( bdv.getViewerPanel().getState().numSources() - 1, groupIdx );
	}

	private void initBdvOptions( ImageSourcesModel imageSourcesModel )
	{
		// TODO: is it correct to already here specify the number of groups?

		bdvOptions = BdvOptions.options();

		if ( imageSourcesModel.is2D() ) bdvOptions = bdvOptions.is2D();
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

		final List< Integer > indices = bdv.getViewerPanel().getState().getVisibleSourceIndices();
		final List< SourceState< ? > > sources = bdv.getViewerPanel().getState().getSources();

		for ( int idx : indices )
		{
			if ( sources.get( idx ).getSpimSource() instanceof LabelSource )
			{
				final int timePoint = getCurrentTimePoint();

				final double label = BdvUtils.getValueAtGlobalCoordinates(
						sources.get( idx ).getSpimSource(),
						BdvUtils.getGlobalMouseCoordinates( bdv ),
						timePoint );

				if ( label == BACKGROUND ) return;

				// TODO: getCurrentGroupName() does not seem to make sense. Maybe sources must have good names?
				selectionModel.toggle( imagesAndSegmentsModel.getSegment( getCurrentGroupName(), label, timePoint ) );

				break; // TODO: at the minute there can be only one LabelSource per group
			}
		}

	}

	private int getCurrentTimePoint()
	{
		return bdv.getBdvHandle().getViewerPanel().getState().getCurrentTimepoint();
	}




}
