package de.embl.cba.tables.modelview.views;

import bdv.tools.brightness.ConverterSetup;
import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import bdv.util.BdvStackSource;
import bdv.viewer.Source;
import bdv.viewer.state.SourceState;
import bdv.viewer.state.ViewerState;
import de.embl.cba.bdv.utils.BdvUtils;
import de.embl.cba.bdv.utils.objects3d.ConnectedComponentExtractorAnd3DViewer;
import de.embl.cba.bdv.utils.sources.ARGBConvertedRealSource;
import de.embl.cba.tables.modelview.coloring.ColoringModel;
import de.embl.cba.tables.modelview.coloring.DynamicCategoryColoringModel;
import de.embl.cba.tables.modelview.coloring.SelectionColoringModel;
import de.embl.cba.tables.modelview.combined.ImageSegmentsModel;
import de.embl.cba.tables.modelview.images.ImageSourcesModel;
import de.embl.cba.tables.modelview.images.SourceMetadata;
import de.embl.cba.tables.modelview.images.SourceAndMetadata;
import de.embl.cba.tables.modelview.segments.ImageSegment;
import de.embl.cba.tables.modelview.segments.ImageSegmentId;
import de.embl.cba.tables.modelview.selection.SelectionListener;
import de.embl.cba.tables.modelview.selection.SelectionModel;
import de.embl.cba.tables.modelview.coloring.ImageSegmentLabelsARGBConverter;
import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.ui.behaviour.ClickBehaviour;
import org.scijava.ui.behaviour.io.InputTriggerConfig;
import org.scijava.ui.behaviour.util.Behaviours;

import java.util.*;

import static de.embl.cba.bdv.utils.converters.SelectableVolatileARGBConverter.BACKGROUND;
import static de.embl.cba.tables.modelview.images.SourceMetadata.*;

public class ImageSegmentsBdvView < T extends ImageSegment >
{
	private String selectTrigger = "ctrl button1";
	private String selectNoneTrigger = "ctrl N";
	private String incrementCategoricalLutRandomSeedTrigger = "ctrl L";
	private String iterateSelectionModeTrigger = "ctrl S";
	private String viewIn3DTrigger = "ctrl shift button1";

	private final ImageSegmentsModel< T > imageSegmentsModel;
	private final ImageSourcesModel imageSourcesModel;
	private final SelectionModel< T > selectionModel;
	private final SelectionColoringModel< T > selectionColoringModel;
	private Behaviours behaviours;

	private BdvHandle bdv;
	private String name = "TODO";
	private BdvOptions bdvOptions;
	private SourceAndMetadata currentLabelSource;
	private T recentFocus;
	private ViewerState recentViewerState;
	private List< ConverterSetup > recentConverterSetups;
	private double voxelSpacing3DView;
	private Set< SourceAndMetadata > currentSources;

	public ImageSegmentsBdvView(
			final ImageSourcesModel imageSourcesModel,
			final ImageSegmentsModel< T > imageSegmentsModel,
			final SelectionModel< T > selectionModel,
			final SelectionColoringModel< T > selectionColoringModel)
	{
		this.imageSourcesModel = imageSourcesModel;
		this.imageSegmentsModel = imageSegmentsModel;
		this.selectionModel = selectionModel;
		this.selectionColoringModel = selectionColoringModel;

		this.voxelSpacing3DView = 0.2;
		this.currentSources = new HashSet<>( );

		initBdvOptions();

		showInitialSources();

		//new BdvGrayValuesOverlay( bdv, 20 ); // TODO: makes problems when removing sources

		registerAsSelectionListener( selectionModel );

		registerAsColoringListener( selectionColoringModel );

		installBdvBehaviours();
	}

	public ImageSourcesModel getImageSourcesModel()
	{
		return imageSourcesModel;
	}

	private void showInitialSources()
	{
		boolean isShownNone = true;

		for ( SourceAndMetadata sourceAndMetadata : imageSourcesModel.sources().values() )
		{
			if ( sourceAndMetadata.metadata().showInitially )
			{
				showSourceSet( sourceAndMetadata );
				isShownNone = false;
			}
		}

		if ( isShownNone )
		{
			showSourceSet( imageSourcesModel.sources().values().iterator().next() );
		}

	}

	public BdvHandle getBdv()
	{
		return bdv;
	}

	public void registerAsColoringListener( ColoringModel< T > coloringModel )
	{
		coloringModel.listeners().add( () -> BdvUtils.repaint( bdv ) );
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
				if ( recentFocus != null && selection == recentFocus )
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

	public synchronized void centerBdvOnSegment( ImageSegment imageSegment )
	{
		showSegmentImage( imageSegment );

		bdv.getBdvHandle().getViewerPanel().setTimepoint( imageSegment.timePoint() );

		final double[] position = new double[ 3 ];
		imageSegment.localize( position );
		BdvUtils.moveToPosition(
				bdv,
				position,
				imageSegment.timePoint(),
				500 );
	}

	private void showSegmentImage( ImageSegment imageSegment )
	{
		final String imageId = imageSegment.imageId();

		if ( currentLabelSource.metadata().imageId.equals( imageId ) ) return;

		final SourceAndMetadata sourceAndMetadata
				= imageSourcesModel.sources().get( imageId );

		showSourceSet( sourceAndMetadata );
	}

	public void setVoxelSpacing3DView( double voxelSpacing3DView )
	{
		this.voxelSpacing3DView = voxelSpacing3DView;
	}

	/**
	 * ...will show more sources if required by metadata...
	 *
	 * @param sourceAndMetadata
	 */
	public void showSourceSet( SourceAndMetadata sourceAndMetadata )
	{
		final List< String > imageSetIDs = sourceAndMetadata.metadata().imageSetIDs;

		if ( bdv != null  ) removeAllSources();

		for ( int sourceIndex = 0; sourceIndex < imageSetIDs.size(); sourceIndex++ )
		{
			final SourceAndMetadata associatedSourceAndMetadata =
					imageSourcesModel.sources().get( imageSetIDs.get( sourceIndex ) );

			applyRecentDisplaySettings( sourceIndex, associatedSourceAndMetadata );

			showSource( associatedSourceAndMetadata );
		}

		applyRecentViewerSettings( );
	}

	public void applyRecentViewerSettings( )
	{
		if ( recentViewerState != null )
		{
			applyViewerStateTransform( recentViewerState );

			applyViewerStateVisibility( recentViewerState );
		}
	}

	public void applyViewerStateVisibility( ViewerState viewerState )
	{
		final int numSources = bdv.getViewerPanel().getVisibilityAndGrouping().numSources();
		for ( int i = 0; i < numSources; i++ )
		{
			if ( viewerState.getVisibleSourceIndices().contains( i ) )
			{
				bdv.getViewerPanel().getVisibilityAndGrouping().setSourceActive( i, true );
			}
			else
			{
				bdv.getViewerPanel().getVisibilityAndGrouping().setSourceActive( i, false );
			}
		}
	}


	public void applyViewerStateTransform( ViewerState viewerState )
	{
		final AffineTransform3D transform3D = new AffineTransform3D();
		viewerState.getViewerTransform( transform3D );
		bdv.getViewerPanel().setCurrentViewerTransform( transform3D );
	}

	private void applyRecentDisplaySettings( int i, SourceAndMetadata associatedSourceAndMetadata )
	{
		if ( recentConverterSetups != null )
		{
			associatedSourceAndMetadata.metadata().displayRangeMin =
					recentConverterSetups.get( i ).getDisplayRangeMin();

			associatedSourceAndMetadata.metadata().displayRangeMax =
					recentConverterSetups.get( i ).getDisplayRangeMax();
		}
	}


	/**
	 * Shows a single source
	 * @param sourceAndMetadata
	 * @param displayRangeMin
	 * @param displayRangeMax
	 */
	public BdvStackSource showSource( SourceAndMetadata sourceAndMetadata )
	{
		final SourceMetadata metadata = sourceAndMetadata.metadata();
		Source< ? > source = sourceAndMetadata.source();

		if ( metadata.flavour == Flavour.LabelSource )
		{
			source = asLabelSource( sourceAndMetadata );
			currentLabelSource = sourceAndMetadata; // Currently, there can be only one.
		}

		if ( metadata.numSpatialDimensions == 2 )
			bdvOptions = bdvOptions.is2D();

		bdvOptions = bdvOptions.sourceTransform( metadata.sourceTransform );

		final BdvStackSource bdvStackSource = BdvFunctions.show( source, bdvOptions );

		bdvStackSource.setDisplayRange( metadata.displayRangeMin, metadata.displayRangeMax );

		bdv = bdvStackSource.getBdvHandle();

		bdvOptions = bdvOptions.addTo( bdv );

		metadata.bdvStackSource = bdvStackSource;

		currentSources.add( sourceAndMetadata );

		return bdvStackSource;
	}

	public void removeSource( BdvStackSource bdvStackSource )
	{
		currentSources.remove( bdvStackSource );
		BdvUtils.removeSource( bdv, bdvStackSource );
	}

	public Set< SourceAndMetadata > getCurrentSources()
	{
		return Collections.unmodifiableSet( currentSources );
	}

	private void removeAllSources()
	{
		recentViewerState = bdv.getViewerPanel().getState();
		recentConverterSetups = new ArrayList<>( bdv.getSetupAssignments().getConverterSetups() );

		final List< SourceState< ? > > sources = recentViewerState.getSources();
		final int numSources = sources.size();

		for ( int i = numSources - 1; i >= 0; --i )
		{
			final Source< ? > source = sources.get( i ).getSpimSource();
			bdv.getViewerPanel().removeSource( source );

			final ConverterSetup converterSetup = recentConverterSetups.get( i );
			bdv.getSetupAssignments().removeSetup( converterSetup );
		}
	}

	private Source asLabelSource( SourceAndMetadata sourceAndMetadata )
	{
		ImageSegmentLabelsARGBConverter labelSourcesARGBConverter =
				new ImageSegmentLabelsARGBConverter(
						imageSegmentsModel,
						sourceAndMetadata.metadata().imageId,
						selectionColoringModel );

		return new ARGBConvertedRealSource(
				sourceAndMetadata.source(),
				labelSourcesARGBConverter );
	}

	private void initBdvOptions( )
	{
		bdvOptions = BdvOptions.options();

		if ( imageSourcesModel.is2D() )
			bdvOptions = bdvOptions.is2D();
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
		install3DViewBehaviour();
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
		if ( currentLabelSource == null ) return;

		final double labelId = getLabelIdAtCurrentMouseCoordinates();

		if ( labelId == BACKGROUND ) return;

		final String imageId = currentLabelSource.metadata().imageId;

		final ImageSegmentId imageSegmentId = new ImageSegmentId( imageId, labelId, getCurrentTimePoint() );

		final T segment = imageSegmentsModel.getImageSegment( imageSegmentId );

		selectionModel.toggle( segment );

		if ( selectionModel.isSelected( segment ) )
		{
			recentFocus = segment;
			selectionModel.focus( segment );
		}
	}

	private double getLabelIdAtCurrentMouseCoordinates()
	{
		return BdvUtils.getValueAtGlobalCoordinates(
					currentLabelSource.source(),
					BdvUtils.getGlobalMouseCoordinates( bdv ),
					getCurrentTimePoint() );
	}

	private void installSelectionColoringModeBehaviour( )
	{
		behaviours.behaviour( ( ClickBehaviour ) ( x, y ) ->
		{
			selectionColoringModel.iterateSelectionMode();
			BdvUtils.repaint( bdv );
		}, name + "-iterate-selection", iterateSelectionModeTrigger );
	}

	private void install3DViewBehaviour( )
	{
		behaviours.behaviour( ( ClickBehaviour ) ( x, y ) ->
		{
			if ( getLabelIdAtCurrentMouseCoordinates() != BACKGROUND )
			{
				viewObjectAtCurrentMouseCoordinatesIn3D();
			}
		}, name + "-view-3d", viewIn3DTrigger );
	}

	private void viewObjectAtCurrentMouseCoordinatesIn3D()
	{
		new Thread( () -> new ConnectedComponentExtractorAnd3DViewer( currentLabelSource.source() )
				.extractAndShowIn3D(
						BdvUtils.getGlobalMouseCoordinates( bdv ),
						voxelSpacing3DView ) ).start();
	}

	private int getCurrentTimePoint()
	{
		return bdv.getBdvHandle().getViewerPanel().getState().getCurrentTimepoint();
	}


}
