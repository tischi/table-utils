package de.embl.cba.tables.modelview.views;

import bdv.tools.brightness.ConverterSetup;
import bdv.util.*;
import bdv.viewer.Source;
import bdv.viewer.state.SourceState;
import bdv.viewer.state.ViewerState;
import customnode.CustomTriangleMesh;
import de.embl.cba.bdv.utils.BdvUtils;
import de.embl.cba.bdv.utils.objects3d.ConnectedComponentExtractorAnd3DViewer;
import de.embl.cba.bdv.utils.sources.ARGBConvertedRealSource;
import de.embl.cba.tables.mesh.MeshExtractor;
import de.embl.cba.tables.mesh.MeshUtils;
import de.embl.cba.tables.modelview.coloring.*;
import de.embl.cba.tables.modelview.combined.ImageSegmentsModel;
import de.embl.cba.tables.modelview.images.SourceAndMetadata;
import de.embl.cba.tables.modelview.images.SourceMetadata;
import de.embl.cba.tables.modelview.segments.ImageSegment;
import de.embl.cba.tables.modelview.segments.ImageSegmentId;
import de.embl.cba.tables.modelview.selection.SelectionListener;
import de.embl.cba.tables.modelview.selection.SelectionModel;
import ij.IJ;
import ij3d.Content;
import ij3d.Image3DUniverse;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;
import org.scijava.ui.behaviour.ClickBehaviour;
import org.scijava.ui.behaviour.io.InputTriggerConfig;
import org.scijava.ui.behaviour.util.Behaviours;
import org.scijava.vecmath.Color3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static de.embl.cba.bdv.utils.BdvUtils.getRAI;
import static de.embl.cba.bdv.utils.converters.SelectableVolatileARGBConverter.BACKGROUND;
import static de.embl.cba.tables.modelview.images.SourceMetadata.Flavour;

public class ImageSegments3dView< T extends ImageSegment, R extends RealType< R > & NativeType< R > >
{
	private final ImageSegmentsModel< T > imageSegmentsModel;
	private final SelectionModel< T > selectionModel;
	private final SelectionColoringModel< T > selectionColoringModel;
	private final Source< R > segmentsSource;
	private ArrayList< double[] > segmentsSourceCalibrations;
	private Behaviours behaviours;

	private Image3DUniverse universe;
	private String labelSourceName;
	private BdvOptions bdvOptions;
	private SourceAndMetadata currentLabelSource;
	private T recentFocus;
	private ViewerState recentViewerState;
	private List< ConverterSetup > recentConverterSetups;
	private double voxelSpacing3DView;
	private Set< SourceAndMetadata< ? extends RealType< ? > > > currentSources;
	private Set< LabelsARGBConverter > labelsARGBConverters;
	private boolean grayValueOverlayWasFirstSource;
	private HashMap< T, CustomTriangleMesh > segmentToMesh;
	private HashMap< T, Content > segmentToContent;

	public ImageSegments3dView(
			final Source< R > segmentsSource,
			final ImageSegmentsModel< T > imageSegmentsModel,
			final SelectionModel< T > selectionModel,
			final SelectionColoringModel< T > selectionColoringModel)
	{

		this( 	segmentsSource,
				imageSegmentsModel,
				selectionModel,
				selectionColoringModel,
				null );
	}


	public ImageSegments3dView(
			final Source< R > segmentsSource,
			final ImageSegmentsModel< T > imageSegmentsModel,
			final SelectionModel< T > selectionModel,
			final SelectionColoringModel< T > selectionColoringModel,
			Image3DUniverse universe )
	{
		this.segmentsSource = segmentsSource;
		this.imageSegmentsModel = imageSegmentsModel;
		this.selectionModel = selectionModel;
		this.selectionColoringModel = selectionColoringModel;
		this.universe = universe;

		this.voxelSpacing3DView = 0.1;
		this.segmentToMesh = new HashMap<>();
		this.segmentToContent = new HashMap<>();

		setCalibrations();

		registerAsSelectionListener( this.selectionModel );
		registerAsColoringListener( this.selectionColoringModel );
	}

	public void setVoxelSpacing3DView( double voxelSpacing3DView )
	{
		this.voxelSpacing3DView = voxelSpacing3DView;
	}

	public Image3DUniverse getUniverse()
	{
		return universe;
	}

	private void setCalibrations()
	{
		segmentsSourceCalibrations = new ArrayList<>(  );
		final int numMipmapLevels = this.segmentsSource.getNumMipmapLevels();
		for ( int level = 0; level < numMipmapLevels; ++level )
			segmentsSourceCalibrations.add( BdvUtils.getCalibration( this.segmentsSource, level ) );
	}


	private void registerAsColoringListener( ColoringModel< T > coloringModel )
	{
		coloringModel.listeners().add( () -> adaptSegmentColors() );
	}

	private void adaptSegmentColors()
	{
	}

	public void registerAsSelectionListener( SelectionModel< T > selectionModel )
	{
		selectionModel.listeners().add( new SelectionListener< T >()
		{
			@Override
			public void selectionChanged()
			{
			}

			@Override
			public void focusEvent( T selection )
			{
				if ( recentFocus != null && selection == recentFocus ) return;

				recentFocus = selection;
				showSegment( selection );
			}
		} );
	}

	private synchronized void showSegment( T imageSegment )
	{
		CustomTriangleMesh mesh;

		if ( segmentToMesh.containsKey( imageSegment ) )
		{
			mesh = segmentToMesh.get( imageSegment );
		}
		else
		{
			if ( imageSegment.boundingBox() != null )
			{
				final RandomAccessibleInterval< R > rai
						= getRAI( segmentsSource, 0, getLevel() );

				final MeshExtractor meshExtractor = new MeshExtractor(
						Views.extendZero( rai ),
						imageSegment.boundingBox(),
						new AffineTransform3D(),
						new int[]{ 1, 1, 1 },
						() -> false );

				mesh = MeshUtils.asCustomTriangleMesh(
						meshExtractor.generateMesh( imageSegment.labelId() ) );
			}
			else
			{
				IJ.showMessage( "ImageSegments without bounding box " +
						"are currently not supported.");
				return;
			}
		}

		setMeshColor( imageSegment, mesh );
		addMeshToUniverse( imageSegment, mesh );
	}

	private void addMeshToUniverse( T imageSegment, CustomTriangleMesh mesh )
	{
		if ( universe == null ) universe = new Image3DUniverse( );
		final Content content = universe.addCustomMesh( mesh, "" + imageSegment.labelId() );
		segmentToContent.put( imageSegment, content );
	}

	private void setMeshColor( T imageSegment, CustomTriangleMesh mesh )
	{
		final ARGBType argbType = new ARGBType();
		selectionColoringModel.convert( imageSegment, argbType );
		final Color3f color3f = new Color3f( ColorUtils.getColor( argbType ) );
		mesh.setColor( color3f );
	}

	private int getLevel()
	{
		int level;
		for ( level = 0; level < segmentsSourceCalibrations.size(); level++ )
			if ( segmentsSourceCalibrations.get( level )[ 0 ] > voxelSpacing3DView ) break;

		return level;
	}


}
