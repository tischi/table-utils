package de.embl.cba.tables.modelview.views.bdv;

import bdv.tools.brightness.ConverterSetup;
import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import bdv.viewer.Source;
import bdv.viewer.state.SourceState;
import bdv.viewer.state.ViewerState;
import de.embl.cba.bdv.utils.BdvUtils;
import de.embl.cba.bdv.utils.sources.ARGBConvertedRealSource;
import de.embl.cba.tables.modelview.coloring.ColoringListener;
import de.embl.cba.tables.modelview.coloring.ColoringModel;
import de.embl.cba.tables.modelview.coloring.DynamicCategoryColoringModel;
import de.embl.cba.tables.modelview.coloring.SelectionColoringModel;
import de.embl.cba.tables.modelview.images.ImageSourcesModel;
import de.embl.cba.tables.modelview.combined.ImagesAndSegmentsModel;
import de.embl.cba.tables.modelview.images.Metadata;
import de.embl.cba.tables.modelview.images.SourceAndMetadata;
import de.embl.cba.tables.modelview.segments.ImageSegment;
import de.embl.cba.tables.modelview.selection.SelectionListener;
import de.embl.cba.tables.modelview.selection.SelectionModel;
import de.embl.cba.tables.modelview.views.ImageSegmentLabelsARGBConverter;
import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.ui.behaviour.ClickBehaviour;
import org.scijava.ui.behaviour.io.InputTriggerConfig;
import org.scijava.ui.behaviour.util.Behaviours;

import java.util.*;

import static de.embl.cba.bdv.utils.converters.SelectableVolatileARGBConverter.BACKGROUND;
import static de.embl.cba.tables.modelview.images.Metadata.*;

public class ImageSegmentsBdvView < T extends ImageSegment >
{
	private String selectTrigger = "ctrl button1";
	private String selectNoneTrigger = "ctrl N";
	private String incrementCategoricalLutRandomSeedTrigger = "ctrl L";
	private String iterateSelectionModeTrigger = "ctrl S";
	private String viewIn3DTrigger = "ctrl shift button1";

	private final ImagesAndSegmentsModel< T > imagesAndSegmentsModel;
	private final SelectionModel< T > selectionModel;
	private final SelectionColoringModel< T > selectionColoringModel;
	private Behaviours behaviours;

	private BdvHandle bdv;
	private String name = "TODO";
//	private int bdvGroupId;
//	private HashMap< String, Integer > imageSetIdToBdvGroupIdxMap;
	private BdvOptions bdvOptions;
	private HashMap< Integer, String > groupIdxToName;
	private HashMap< String, String > imageIdToGroupName;
	private ImageSourcesModel imageSourcesModel;
//	private boolean isBdvGroupingConfigured;
	private HashMap< Integer, String > sourceIndexToFlavour;
	private HashMap< String, Integer > groupNameToIndex;
	private boolean centerOnSegment;
	private SourceAndMetadata currentLabelSource;
	private T recentFocus;

	public ImageSegmentsBdvView(
			final ImagesAndSegmentsModel imagesAndSegmentsModel,
			final SelectionModel selectionModel,
			final SelectionColoringModel selectionColoringModel,
			final boolean centerOnSegment,
			final ArrayList< String > initialSources)
	{
		this.imagesAndSegmentsModel = imagesAndSegmentsModel;
		this.selectionModel = selectionModel;
		this.selectionColoringModel = selectionColoringModel;

		this.imageSourcesModel = imagesAndSegmentsModel.getImageSourcesModel();
		this.centerOnSegment = centerOnSegment;

//		this.nextSourceIndex = 0;

		groupIdxToName = new HashMap<>(  );
		groupNameToIndex = new HashMap<>(  );
		imageIdToGroupName = new HashMap<>(  );
		sourceIndexToFlavour = new HashMap<>();

//		isBdvGroupingConfigured = false;

		initBdvOptions( imageSourcesModel );

//		configureGroupingNames();

		for ( String sourceName : initialSources )
		{
			showSource( sourceName, imageSourcesModel.sources().get( sourceName ) );
		}

		registerAsSelectionListener( selectionModel );

		registerAsColoringListener( selectionColoringModel );

		installBdvBehaviours();
	}

//	public void configureGroupingNames()
//	{
//		final Set< String > imageSetNames = imageSourcesModel.sources().keySet();
//
//		int i = 0;
//		for ( String imageSetName : imageSetNames )
//		{
//			groupIdxToName.put( i, imageSetName );
//			groupNameToIndex.put( imageSetName, i );
//			imageIdToGroupName.put( imageSetName, imageSetName ); // TODO!
//			i++;
//		}
//	}

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

	public void registerAsSelectionListener( SelectionModel< T > selectionModel )
	{
		selectionModel.listeners().add( new SelectionListener< T >()
		{
			@Override
			public void selectionChanged()
			{
				BdvUtils.repaint( bdv );
			}

			@Override
			public void focusEvent( T selection )
			{
				if ( recentFocus != null
						&& selection == recentFocus )
				{
					return;
				}
				else
				{
					recentFocus = selection;
					centerBdvOnSegment( selection );
				}
			}
		} );
	}

	public synchronized void centerBdvOnSegment( ImageSegment selection )
	{
		showSegmentImage( selection );

		bdv.getBdvHandle().getViewerPanel().setTimepoint( selection.timePoint() );

		if ( centerOnSegment )
		{
			final double[] position = new double[ 3 ];
			selection.localize( position );
			BdvUtils.moveToPosition(
					bdv,
					position,
					selection.timePoint(),
					500 );
		}
	}

	private void showSegmentImage( ImageSegment imageSegment )
	{
		final String imageId = imageSegment.imageId();

		if ( currentLabelSource.getMetadata().get().get( NAME ).equals( imageId ) ) return;

		final SourceAndMetadata sourceAndMetadata = imageSourcesModel.sources().get( imageId );

		showSource( imageId, sourceAndMetadata );

		// sources source from imageSources by this name imageSegment.imageId();
		// and showSource( source );
		// if source.metadata.contains("ShowExclusivelyWith") => remove all other sources
		// else simply add the source

		// an issue is that the Bdv alters the sources when they are being added
		// => how to remove one?

//		final String imageSegmentGroupName = getImageSegmentGroupName( imageSegment );
//		final String currentGroupName = getCurrentGroupName();
//
//		if ( ! currentGroupName.equals( imageSegmentGroupName ) )
//		{
//			final int selectedGroup = getImageSegmentGroupIdx( imageSegment );
//
//			showGroup( selectedGroup );
//		}
	}

//	private void showGroup( int selectedGroup )
//	{
//		if ( isEmpty( selectedGroup ) )
//		{
//			populateAndShowGroup( selectedGroup );
//		}
//
//		bdv.getViewerPanel().getVisibilityAndGrouping().setGroupActive( selectedGroup, true );
//		bdv.getViewerPanel().getVisibilityAndGrouping().setCurrentGroup( selectedGroup );
//	}


	/**
	 * ...will show more sources if required by metadata...
	 *
	 * @param imageId
	 * @param sourceAndMetadata
	 */
	public void showSource( String imageId, SourceAndMetadata sourceAndMetadata )
	{
		final Map< String, Object > metadata = sourceAndMetadata.getMetadata().get();

		if( metadata.containsKey( Metadata.EXCLUSIVE_IMAGE_SET ) )
		{
			showExclusiveImageSet( metadata );
		}
		else
		{
			showSingleSource( imageId, sourceAndMetadata );
		}
	}

	public void applyViewerState( ViewerState viewerState )
	{
		if ( viewerState != null )
		{
			final AffineTransform3D transform3D = new AffineTransform3D();
			viewerState.getViewerTransform( transform3D );
			bdv.getViewerPanel().setCurrentViewerTransform( transform3D );

			// TODO: also try to copy the brightness and other settings
		}
	}

	public void showExclusiveImageSet( Map< String, Object > metadata )
	{
		ViewerState viewerState = null;

		if ( bdv != null  )
		{
			viewerState = removeAllSources();
		}

		final ArrayList< String > imageIDs = ( ArrayList< String > ) metadata.get( Metadata.EXCLUSIVE_IMAGE_SET );

		for ( String associatedImageId : imageIDs )
		{
			final SourceAndMetadata associatedSourceAndMetadata = imageSourcesModel.sources().get( associatedImageId );
			showSingleSource( associatedImageId, associatedSourceAndMetadata );
		}

		applyViewerState( viewerState );
	}

	/**
	 * Shows a single source
	 *
	 *
	 * @param imageId
	 * @param sourceAndMetadata
	 */
	public void showSingleSource( String imageId, SourceAndMetadata sourceAndMetadata )
	{
		final Map< String, Object > metadata = sourceAndMetadata.getMetadata().get();
		final Source< ? > source = sourceAndMetadata.getSource();

		if ( metadata.containsKey( DIMENSIONS ) && metadata.get( DIMENSIONS ).equals( 2 ) )
		{
			bdvOptions = bdvOptions.is2D();
		}

		if ( metadata.containsKey( FLAVOUR ) && metadata.get( FLAVOUR ).equals( LABEL_SOURCE_FLAVOUR ) )
		{
			bdv = BdvFunctions.show( asLabelSource( imageId, source ), bdvOptions ).getBdvHandle();
			currentLabelSource = sourceAndMetadata;
		}
		else
		{
			bdv = BdvFunctions.show( source, bdvOptions ).getBdvHandle();
		}

		bdvOptions = bdvOptions.addTo( bdv );
	}

	private void initBdv()
	{
//		final RandomAccessibleInterval< BitType > bits = ArrayImgs.bits( new long[]{ 10, 10 } );

//		bdv = BdvFunctions.show( bits, "dummy", bdvOptions ).getBdvHandle();

//		bdvOptions = bdvOptions.addTo( bdv );
	}

	private ViewerState removeAllSources()
	{

		final ViewerState state = bdv.getViewerPanel().getState();

		final List< SourceState< ? > > sources = state.getSources();
		final List< ConverterSetup > converterSetups = bdv.getSetupAssignments().getConverterSetups();
		final int numSources = sources.size();

		for ( int i = numSources - 1; i >= 0; --i )
		{
			final Source< ? > source = sources.get( i ).getSpimSource();
			bdv.getViewerPanel().removeSource( source );

			final ConverterSetup converterSetup = converterSetups.get( i );
			bdv.getSetupAssignments().removeSetup( converterSetup );
		}

		return state;
	}

//	private synchronized void populateAndShowGroup( int groupIndex )
//	{
//		final String groupName = groupIdxToName.sources( groupIndex );
//
//		final Set< String > sourceNames = imageSourcesModel.sources().sources( groupName ).keySet();
//
//		for ( String sourceName : sourceNames )
//		{
//			//imageSourcesModel.sources().sources( groupName );
////			if ( imageSourcesModel.getMetaData( groupName, sourceName ).sources( show ) )
//
//
//			final Map< String, Object > imageSourceMetaData = imageSourcesModel.getMetaData( sourceName );
//
//			if ( imageSourceMetaData.sources( FLAVOUR ).equals( LABEL_SOURCE_FLAVOUR ) )
//			{
//				Source labelSource = asLabelSource( groupName, source );
//
//				bdv = BdvFunctions.show( labelSource, bdvOptions ).getBdvHandle();
//			}
//			else
//			{
//				bdv = BdvFunctions.show( source, bdvOptions ).getBdvHandle();
//			}
//
//			// bdv.getViewerPanel().removeSource(  );
//
//			// 1. remove all sources, but the dummy source
//			// 2. add requested source and put into a currentSources List
//			// 3. check whether the requested source contains a "ShowWith" metadata and add those sources as well;
//			// ...also putting them into the currentSources List
//			// put into currentSourcesMap( sourceName, ValuePair< BdvStackSource, Source  )
//			// final BdvStackSource< Object > bdvStackSource = BdvFunctions.show( labelSource, bdvOptions );
//			// bdvStackSource.removeFromBdv();
//
//			bdvOptions = bdvOptions.addTo( bdv );
//
//			if ( ! isBdvGroupingConfigured ) configureBdvGrouping();
//
//			final VisibilityAndGrouping visibilityAndGrouping = bdv.getViewerPanel().getVisibilityAndGrouping();
//
////			sourceIndexToName.put( nextSourceIndex, sourceName );
//
//			//sourceIndexToFlavour.put( nextSourceIndex, ( String ) imageSourceMetaData.sources( FLAVOUR ) );
//			visibilityAndGrouping.addSourceToGroup( nextSourceIndex, groupIndex );
//			visibilityAndGrouping.setSourceActive( nextSourceIndex, true );
//			nextSourceIndex++;
//
//		}
//	}
//

	private Source asLabelSource( String imageId, Source source )
	{
		ImageSegmentLabelsARGBConverter labelSourcesARGBConverter =
				new ImageSegmentLabelsARGBConverter(
						imagesAndSegmentsModel,
						imageId,
						selectionColoringModel );

		return new ARGBConvertedRealSource(
				source,
				labelSourcesARGBConverter );
	}

//	private boolean isEmpty( int selectedGroup )
//	{
//		if ( bdv == null )
//		{
//			return true;
//		}
//
//		final int size = bdv.getViewerPanel().getState().getSourceGroups().sources( selectedGroup ).getSourceIds().size();
//
//		return size == 0;
//	}
//
//	private String getImageSegmentGroupName( ImageSegment selection )
//	{
//		return imageIdToGroupName.sources( selection.imageId() );
//	}
//
//	private Integer getImageSegmentGroupIdx( ImageSegment selection )
//	{
//		final String groupName = imageIdToGroupName.sources( selection.imageId() );
//		final Integer groupIndex = groupNameToIndex.sources( groupName );
//		return groupIndex;
//	}
//
//	private String getCurrentGroupName()
//	{
//		return groupIdxToName.sources( getCurrentGroupIdx() );
//	}
//
//	private int getCurrentGroupIdx()
//	{
//		return bdv.getViewerPanel().getVisibilityAndGrouping().getCurrentGroup();
//	}

//
//	private void configureBdvGrouping()
//	{
//		final Set< String > imageSetNames = imageSourcesModel.sources().keySet();
//
//		final VisibilityAndGrouping visibilityAndGrouping = bdv.getViewerPanel().getVisibilityAndGrouping();
//
//		while ( visibilityAndGrouping.getSourceGroups().size() > 1 ) {
//			final SourceGroup g = visibilityAndGrouping.getSourceGroups().sources(0);
//			this.bdv.getViewerPanel().removeGroup( g );
//		}
//
//
//		int i = 0;
//		for ( String imageSetName : imageSetNames )
//		{
//			if ( i == 0 )
//			{
//				// first group exists by default => only change name
//				visibilityAndGrouping.setGroupName( 0, imageSetName );
//			}
//			else
//			{
//				bdv.getViewerPanel().addGroup( new SourceGroup( imageSetName ) );
//				visibilityAndGrouping.setGroupName( i, imageSetName );
//			}
//
//			i++;
//		}
//
//		visibilityAndGrouping.setGroupingEnabled( true );
//		visibilityAndGrouping.setDisplayMode( DisplayMode.GROUP );
//
////		isBdvGroupingConfigured = true;
//	}
//
//	private void addMostRecentSourceToGroup( int groupIdx )
//	{
//		bdv.getViewerPanel().getVisibilityAndGrouping().addSourceToGroup( bdv.getViewerPanel().getState().numSources() - 1, groupIdx );
//	}

	private void initBdvOptions( ImageSourcesModel imageSourcesModel )
	{
		bdvOptions = BdvOptions.options();
	}

	private void installBdvBehaviours()
	{
		behaviours = new Behaviours( new InputTriggerConfig() );
		behaviours.install(
				bdv.getBdvHandle().getTriggerbindings(),
				name + "-bdv-selection-handler" );

		installSelectionBehaviour( );
		installSelectNoneBehaviour( );
		installSelectionColoringModeBehaviour( );
		installRandomColorShufflingBehaviour();
		//if( is3D() ) install3DViewBehaviour();
	}

	private void installRandomColorShufflingBehaviour()
	{
		behaviours.behaviour( ( ClickBehaviour ) ( x, y ) ->
		{
			if ( selectionColoringModel.getWrappedColoringModel() instanceof DynamicCategoryColoringModel )
			{
				( ( DynamicCategoryColoringModel ) selectionColoringModel.getWrappedColoringModel() ).incRandomSeed();
				BdvUtils.repaint( bdv );
			}
		}, name + "-change-coloring-random-seed", incrementCategoricalLutRandomSeedTrigger );
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
		if ( currentLabelSource != null )
		{
			final int timePoint = getCurrentTimePoint();

			final double label = BdvUtils.getValueAtGlobalCoordinates(
					currentLabelSource.getSource(),
					BdvUtils.getGlobalMouseCoordinates( bdv ),
					timePoint );

			if ( label == BACKGROUND ) return;

			if ( currentLabelSource.getMetadata().get().containsKey( NAME ) )
			{
				final String imageId = ( String ) currentLabelSource.getMetadata().get().get( NAME );
				final T segment = imagesAndSegmentsModel.getSegment( imageId, label, timePoint );

				selectionModel.toggle( segment );

				if ( selectionModel.isSelected( segment ) )
				{
					recentFocus = segment;
					selectionModel.focus( segment );
				}
			}
		}
	}

	private void installSelectionColoringModeBehaviour( )
	{
		behaviours.behaviour( ( ClickBehaviour ) ( x, y ) ->
		{
			selectionColoringModel.iterateSelectionMode();
			BdvUtils.repaint( bdv );
		}, name + "-iterate-selection", iterateSelectionModeTrigger );
	}

	private int getCurrentTimePoint()
	{
		return bdv.getBdvHandle().getViewerPanel().getState().getCurrentTimepoint();
	}

}
