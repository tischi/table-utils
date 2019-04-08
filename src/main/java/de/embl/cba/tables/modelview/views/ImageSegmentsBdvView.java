package de.embl.cba.tables.modelview.views;

import bdv.tools.brightness.ConverterSetup;
import bdv.util.*;
import bdv.viewer.Source;
import bdv.viewer.state.SourceState;
import bdv.viewer.state.ViewerState;
import de.embl.cba.bdv.utils.BdvUtils;
import de.embl.cba.bdv.utils.objects3d.ConnectedComponentExtractorAnd3DViewer;
import de.embl.cba.bdv.utils.overlays.BdvGrayValuesOverlay;
import de.embl.cba.bdv.utils.sources.ARGBConvertedRealSource;
import de.embl.cba.tables.modelview.coloring.*;
import de.embl.cba.tables.modelview.combined.DefaultImageSegmentsModel;
import de.embl.cba.tables.modelview.combined.ImageSegmentsModel;
import de.embl.cba.tables.modelview.images.ImageSourcesModel;
import de.embl.cba.tables.modelview.images.SourceMetadata;
import de.embl.cba.tables.modelview.images.SourceAndMetadata;
import de.embl.cba.tables.modelview.segments.ImageSegment;
import de.embl.cba.tables.modelview.segments.ImageSegmentId;
import de.embl.cba.tables.modelview.segments.TableRowImageSegment;
import de.embl.cba.tables.modelview.selection.SelectionListener;
import de.embl.cba.tables.modelview.selection.SelectionModel;
import net.imglib2.RealPoint;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.RealType;
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
	private SourceAndMetadata activeLabelSource;
	private T recentFocus;
	private ViewerState recentViewerState;
	private List< ConverterSetup > recentConverterSetups;
	private double voxelSpacing3DView;
	private Set< SourceAndMetadata< ? extends RealType< ? > > > currentSources;
	private Set< LabelsARGBConverter > labelsARGBConverters;
	private boolean grayValueOverlayWasFirstSource;
	//private boolean hasGrayValueOverlay;

	public ImageSegmentsBdvView(
			final ImageSourcesModel imageSourcesModel,
			final ImageSegmentsModel< T > imageSegmentsModel,
			final SelectionModel< T > selectionModel,
			final SelectionColoringModel< T > selectionColoringModel)
	{

		this(	imageSourcesModel,
				imageSegmentsModel,
				selectionModel,
				selectionColoringModel,
				null );
	}


	public ImageSegmentsBdvView(
			final ImageSourcesModel imageSourcesModel,
			final ImageSegmentsModel< T > imageSegmentsModel,
			final SelectionModel< T > selectionModel,
			final SelectionColoringModel< T > selectionColoringModel,
			BdvHandle bdv )
	{
		this.imageSourcesModel = imageSourcesModel;
		this.imageSegmentsModel = imageSegmentsModel;
		this.selectionModel = selectionModel;
		this.selectionColoringModel = selectionColoringModel;
		this.bdv = bdv;

		this.voxelSpacing3DView = 0.1; // TODO
		this.currentSources = new HashSet<>( );
		this.labelsARGBConverters = new HashSet<>(  );

		initBdvOptions();

		showInitialSources();

		addGrayValueOverlay();

		registerAsSelectionListener( selectionModel );

		registerAsColoringListener( selectionColoringModel );

		installBdvBehaviours();

	}

	public void addGrayValueOverlay()
	{
		new BdvGrayValuesOverlay( bdv, 20 ).getBdvOverlaySource();
		grayValueOverlayWasFirstSource = false;
		//hasGrayValueOverlay = true;
	}

	public ImageSourcesModel getImageSourcesModel()
	{
		return imageSourcesModel;
	}

	private void showInitialSources()
	{
		boolean isShownNone = true;

		for ( SourceAndMetadata< ? > sourceAndMetadata : imageSourcesModel.sources().values() )
		{
			if ( sourceAndMetadata.metadata().showInitially )
			{
				showSourceSet( sourceAndMetadata, false );
				isShownNone = false;
			}
		}


		if ( isShownNone )
		{
			showSourceSet(
					// TODO: how get an entry of map values?
					imageSourcesModel.sources().values().iterator().next(),
					false );
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
		showSegmentImageSet( imageSegment );

		bdv.getBdvHandle().getViewerPanel().setTimepoint( imageSegment.timePoint() );

		final double[] position = new double[ 3 ];
		imageSegment.localize( position );
		BdvUtils.moveToPosition(
				bdv,
				position,
				imageSegment.timePoint(),
				500 );
	}

	private void showSegmentImageSet( ImageSegment imageSegment )
	{
		final String imageId = imageSegment.imageId();

		if ( activeLabelSource.metadata().imageId.equals( imageId ) )
		{
			return;
		}
		else
		{
			// Replace sources, because the selected image segment
			// belongs to another image set of the same data set
			//

			final SourceAndMetadata sourceAndMetadata
					= imageSourcesModel.sources().get( imageId );

			showSourceSet( sourceAndMetadata, true );
		}
	}

	public void setVoxelSpacing3DView( double voxelSpacing3DView )
	{
		this.voxelSpacing3DView = voxelSpacing3DView;
	}

	/**
	 * ...will show more sources if required by metadata...
	 *
	 * @param sourceAndMetadata
	 * @param removeOtherSources
	 */
	public void showSourceSet( SourceAndMetadata sourceAndMetadata, boolean removeOtherSources )
	{
		final List< String > imageSetIDs = sourceAndMetadata.metadata().imageSetIDs;

		if ( bdv != null && removeOtherSources )
		{
			removeSources();
		}

		for ( int associatedSourceIndex = 0;
			  associatedSourceIndex < imageSetIDs.size();
			  associatedSourceIndex++ )
		{
			final SourceAndMetadata associatedSourceAndMetadata =
					imageSourcesModel.sources().get( imageSetIDs.get( associatedSourceIndex ) );

			applyRecentDisplaySettings( associatedSourceIndex, associatedSourceAndMetadata );
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

	public void applyViewerStateVisibility( ViewerState recentViewerState )
	{
		final int numSources = bdv.getViewerPanel().getVisibilityAndGrouping().numSources();
		final List< Integer > visibleSourceIndices = recentViewerState.getVisibleSourceIndices();

		for ( int i = 1; i < numSources; i++ )
		{
			int recentSourceIndex = i;

			if ( ! grayValueOverlayWasFirstSource )
				recentSourceIndex--;

			bdv.getViewerPanel().getVisibilityAndGrouping().
					setSourceActive(
							i,
							visibleSourceIndices.contains( recentSourceIndex ) );
		}
	}


	public void applyViewerStateTransform( ViewerState viewerState )
	{
		final AffineTransform3D transform3D = new AffineTransform3D();
		viewerState.getViewerTransform( transform3D );
		bdv.getViewerPanel().setCurrentViewerTransform( transform3D );
	}

	private void applyRecentDisplaySettings( int associatedSourceIndex,
											 SourceAndMetadata associatedSourceAndMetadata )
	{
		if ( recentConverterSetups != null )
		{
			int recentConverterSetupIndex = associatedSourceIndex;

			if ( grayValueOverlayWasFirstSource )
				recentConverterSetupIndex++;

			associatedSourceAndMetadata.metadata().displayRangeMin =
					recentConverterSetups.get( recentConverterSetupIndex ).getDisplayRangeMin();

			associatedSourceAndMetadata.metadata().displayRangeMax =
					recentConverterSetups.get( recentConverterSetupIndex ).getDisplayRangeMax();
		}
	}

	public BdvStackSource showSource( SourceAndMetadata sourceAndMetadata )
	{
		final SourceMetadata metadata = sourceAndMetadata.metadata();
		Source< ? > source = sourceAndMetadata.source();

		if ( metadata.flavour == Flavour.LabelSource )
			source = asLabelSource( sourceAndMetadata );

		bdvOptions = bdvOptions.sourceTransform( metadata.sourceTransform );

		int numTimePoints = getNumTimePoints( source );

		final BdvStackSource bdvStackSource = BdvFunctions.show(
				source,
				numTimePoints,
				bdvOptions );

		bdvStackSource.setActive( true );

		bdvStackSource.setDisplayRange( metadata.displayRangeMin, metadata.displayRangeMax );

		bdv = bdvStackSource.getBdvHandle();

		updateBdvTimePointListeners();

		bdvOptions = bdvOptions.addTo( bdv );

		metadata.bdvStackSource = bdvStackSource;

		currentSources.add( sourceAndMetadata );

		return bdvStackSource;
	}

	public void updateBdvTimePointListeners()
	{
		for ( LabelsARGBConverter converter : labelsARGBConverters )
		{
			bdv.getViewerPanel().addTimePointListener( converter );
		}
	}

	public int getNumTimePoints( Source< ? > source )
	{
		int numTimePoints = 0;
		while ( source.isPresent( numTimePoints++ ) ){}
		return numTimePoints - 1;
	}

	public void removeSource( BdvStackSource bdvStackSource )
	{
		currentSources.remove( bdvStackSource );
		BdvUtils.removeSource( bdv, bdvStackSource );
	}

	public ArrayList< SourceAndMetadata< ? extends RealType< ? > > > getCurrentSources()
	{
		return new ArrayList<>( currentSources );
	}

	private void removeSources()
	{
		recentViewerState = bdv.getViewerPanel().getState();
		recentConverterSetups = new ArrayList<>( bdv.getSetupAssignments().getConverterSetups() );

		final List< SourceState< ? > > sources = recentViewerState.getSources();
		final int numSources = sources.size();

		for ( int i = 0; i < numSources; ++i )
		{
			final Source< ? > source = sources.get( i ).getSpimSource();
			if ( source instanceof PlaceHolderSource )
			{
				if ( i == 0 )
					grayValueOverlayWasFirstSource = true;
			}
			else
			{
				bdv.getViewerPanel().removeSource( source );
				bdv.getSetupAssignments().removeSetup( recentConverterSetups.get( i ) );
			}
		}
	}

	private Source asLabelSource( SourceAndMetadata< ? extends RealType< ? > > sourceAndMetadata )
	{
		LabelsARGBConverter labelsARGBConverter;

		if ( sourceAndMetadata.metadata().segmentsTable == null )
		{
			labelsARGBConverter = new LazyLabelsARGBConverter();
		}
		else
		{
			// TODO: implement different logic of what is the active label source
			activeLabelSource = sourceAndMetadata;

			labelsARGBConverter =
					new ImageSegmentLabelsARGBConverter(
							imageSegmentsModel,
							sourceAndMetadata.metadata().imageId,
							selectionColoringModel );

		}

		labelsARGBConverters.add( labelsARGBConverter );

		return new ARGBConvertedRealSource( sourceAndMetadata.source(), labelsARGBConverter );
	}

	private void initBdvOptions( )
	{
		bdvOptions = BdvOptions.options();

		if ( imageSourcesModel.is2D() )
			bdvOptions = bdvOptions.is2D();

		if ( bdv != null )
			bdvOptions = bdvOptions.addTo( bdv );
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
						new Thread( () -> shuffleRandomColors() ).start(),
				name + "-change-coloring-random-seed",
				incrementCategoricalLutRandomSeedTrigger );
	}

	private synchronized void shuffleRandomColors()
	{
		final ColoringModel< T > coloringModel =
				selectionColoringModel.getWrappedColoringModel();

		if ( coloringModel instanceof CategoryColoringModel )
		{
			( ( CategoryColoringModel ) coloringModel ).incRandomSeed();
			BdvUtils.repaint( bdv );
		}
	}


	private void installSelectNoneBehaviour( )
	{
		behaviours.behaviour( ( ClickBehaviour ) ( x, y ) ->
				new Thread( () -> selectNone() ).start(),
				name + "-select-none", selectNoneTrigger );
	}

	public synchronized void selectNone()
	{
		selectionModel.clearSelection( );

		BdvUtils.repaint( bdv );
	}

	private void installSelectionBehaviour()
	{
		behaviours.behaviour(
				( ClickBehaviour ) ( x, y ) ->
						new Thread( () -> toggleSelectionAtMousePosition() ).start(),
				name + "-toggle-selection", selectTrigger ) ;
	}

	private synchronized void toggleSelectionAtMousePosition()
	{
		if ( activeLabelSource == null ) return;

		final double labelId = getLabelIdAtCurrentMouseCoordinates( activeLabelSource );

		if ( labelId == BACKGROUND ) return;

		final String imageId = activeLabelSource.metadata().imageId;

		final ImageSegmentId imageSegmentId =
				new ImageSegmentId( imageId, labelId, getCurrentTimePoint() );

		final T segment = imageSegmentsModel.getImageSegment( imageSegmentId );

		// TODO
		// Here one could select several objects based on the clicked object
		// For example, all objects that have the same numerical or categorical value
		// final ColoringModel< T > wrappedColoringModel = selectionColoringModel.getWrappedColoringModel();

		selectionModel.toggle( segment );

		if ( selectionModel.isSelected( segment ) )
		{
			recentFocus = segment;
			selectionModel.focus( segment );
		}
	}

	private double getLabelIdAtCurrentMouseCoordinates( SourceAndMetadata activeLabelSource )
	{
		final RealPoint globalMouseCoordinates =
				BdvUtils.getGlobalMouseCoordinates( bdv );

		System.out.println( "Finding pixel value at " + globalMouseCoordinates );

		final Double value = BdvUtils.getValueAtGlobalCoordinates(
				activeLabelSource.source(),
				globalMouseCoordinates,
				getCurrentTimePoint() );

		if ( value == null )
		{
			System.out.println( "Could not find pixel value at position " + globalMouseCoordinates);
		}
		else
		{
			System.out.println( "Pixel value is " + value );
		}

		return value;
	}

	private void installSelectionColoringModeBehaviour( )
	{
		behaviours.behaviour( ( ClickBehaviour ) ( x, y ) ->
				new Thread( () ->
				{
					selectionColoringModel.iterateSelectionMode();
					BdvUtils.repaint( bdv );
				} ).start(),
				name + "-iterate-selection", iterateSelectionModeTrigger );
	}

	private void install3DViewBehaviour()
	{
		behaviours.behaviour( ( ClickBehaviour ) ( x, y ) ->

				new Thread( () -> {

					if ( this.activeLabelSource == null ) return;

					final SourceAndMetadata labelSource = this.activeLabelSource;

					if ( getLabelIdAtCurrentMouseCoordinates( labelSource ) != BACKGROUND )
					{
						viewObjectAtCurrentMouseCoordinatesIn3D( labelSource );
					}

				}).start(),
				name + "-view-3d", viewIn3DTrigger );
	}

	private synchronized void viewObjectAtCurrentMouseCoordinatesIn3D(
			SourceAndMetadata activeLabelSource )
	{
		new ConnectedComponentExtractorAnd3DViewer( activeLabelSource.source() )
				.extractAndShowIn3D(
						BdvUtils.getGlobalMouseCoordinates( bdv ),
						voxelSpacing3DView );
	}

	private int getCurrentTimePoint()
	{
		return bdv.getBdvHandle().getViewerPanel().getState().getCurrentTimepoint();
	}


}
