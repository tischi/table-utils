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
import de.embl.cba.tables.modelview.datamodels.ImageSourcesMetaData;
import de.embl.cba.tables.modelview.datamodels.ImageSourcesModel;
import de.embl.cba.tables.modelview.datamodels.ImagesAndSegmentsModel;
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
	private boolean isBdvGroupingConfigured;
	private HashMap< Integer, String > sourceIndexToMetaData;
	private HashMap< String, Integer > groupNameToIndex;

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
		groupNameToIndex = new HashMap<>(  );
		imageIdToGroupName = new HashMap<>(  );
		sourceIndexToMetaData = new HashMap<>();
		isBdvGroupingConfigured = false;

		initBdvOptions( imageSourcesModel );

		configureGroupingNames();

		showGroup( 0 );

		registerAsSelectionListener( selectionModel );

		registerAsColoringListener( selectionColoringModel );

		installBdvBehaviours();
	}

	public void configureGroupingNames()
	{
		final Set< String > imageSetNames = imageSourcesModel.getImageSources().keySet();

		int i = 0;
		for ( String imageSetName : imageSetNames )
		{
			groupIdxToName.put( i, imageSetName );
			groupNameToIndex.put( imageSetName, i );
			imageIdToGroupName.put( imageSetName, imageSetName ); // TODO!
			i++;
		}
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
		final String imageSegmentGroupName = getImageSegmentGroupName( imageSegment );
		final String currentGroupName = getCurrentGroupName();

		if ( ! currentGroupName.equals( imageSegmentGroupName ) )
		{
			final int selectedGroup = getImageSegmentGroupIdx( imageSegment );

			showGroup( selectedGroup );
		}
	}

	private void showGroup( int selectedGroup )
	{
		if ( isEmpty( selectedGroup ) )
		{
			populateAndShowGroup( selectedGroup );
		}

		bdv.getViewerPanel().getVisibilityAndGrouping().setGroupActive( selectedGroup, true );
		bdv.getViewerPanel().getVisibilityAndGrouping().setCurrentGroup( selectedGroup );
	}

	private void populateAndShowGroup( int groupIndex )
	{
		final String groupName = groupIdxToName.get( groupIndex );

		final ArrayList< Source< ? > > sources = imageSourcesModel.getImageSources().get( groupName );

		for ( Source source : sources )
		{
			final String imageSourceMetaData = imageSourcesModel.getImageSourceMetaData( source );

			if ( imageSourceMetaData.contains( ImageSourcesMetaData.LABEL_SOURCE ) )
			{
				Source labelSource = asLabelSource( groupName, source );

				bdv = BdvFunctions.show( labelSource, bdvOptions ).getBdvHandle();
			}
			else
			{
				bdv = BdvFunctions.show( source, bdvOptions ).getBdvHandle();
			}

			bdvOptions = bdvOptions.addTo( bdv );

			if ( ! isBdvGroupingConfigured ) configureBdvGrouping();

			final VisibilityAndGrouping visibilityAndGrouping = bdv.getViewerPanel().getVisibilityAndGrouping();

			final int sourceIndex = bdv.getViewerPanel().getState().numSources() - 1;

			sourceIndexToMetaData.put( sourceIndex, imageSourceMetaData );

			visibilityAndGrouping.addSourceToGroup( sourceIndex, groupIndex );
			visibilityAndGrouping.setSourceActive( sourceIndex, true );

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
		if ( bdv == null )
		{
			return true;
		}

		final int size = bdv.getViewerPanel().getState().getSourceGroups().get( selectedGroup ).getSourceIds().size();

		return size == 0;
	}

	private String getImageSegmentGroupName( ImageSegment selection )
	{
		return imageIdToGroupName.get( selection.imageId() );
	}

	private Integer getImageSegmentGroupIdx( ImageSegment selection )
	{
		final String groupName = imageIdToGroupName.get( selection.imageId() );
		final Integer groupIndex = groupNameToIndex.get( groupName );
		return groupIndex;
	}

	private String getCurrentGroupName()
	{
		return groupIdxToName.get( getCurrentGroupIdx() );
	}

	private int getCurrentGroupIdx()
	{
		return bdv.getViewerPanel().getVisibilityAndGrouping().getCurrentGroup();
	}


	private void configureBdvGrouping()
	{
		final Set< String > imageSetNames = imageSourcesModel.getImageSources().keySet();

		final VisibilityAndGrouping visibilityAndGrouping = bdv.getViewerPanel().getVisibilityAndGrouping();

		while ( visibilityAndGrouping.getSourceGroups().size() > 1 ) {
			final SourceGroup g = visibilityAndGrouping.getSourceGroups().get(0);
			this.bdv.getViewerPanel().removeGroup( g );
		}


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
			}

			i++;
		}

		visibilityAndGrouping.setGroupingEnabled( true );
		visibilityAndGrouping.setDisplayMode( DisplayMode.GROUP );

		isBdvGroupingConfigured = true;
	}

	private void addMostRecentSourceToGroup( int groupIdx )
	{
		bdv.getViewerPanel().getVisibilityAndGrouping().addSourceToGroup( bdv.getViewerPanel().getState().numSources() - 1, groupIdx );
	}

	private void initBdvOptions( ImageSourcesModel imageSourcesModel )
	{
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
		final List< Integer > visibleSourceIndices = bdv.getViewerPanel().getState().getVisibleSourceIndices();

		for ( int index : visibleSourceIndices )
		{
			if ( sourceIndexToMetaData.get( index ).contains( ImageSourcesMetaData.LABEL_SOURCE ) )
			{
				final int timePoint = getCurrentTimePoint();

				final double label = BdvUtils.getValueAtGlobalCoordinates(
						bdv.getViewerPanel().getState().getSources().get( index ).getSpimSource(),
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
