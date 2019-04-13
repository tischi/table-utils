package de.embl.cba.tables.modelview.views;

import bdv.viewer.Source;
import customnode.CustomTriangleMesh;
import de.embl.cba.bdv.utils.BdvUtils;
import de.embl.cba.tables.mesh.MeshExtractor;
import de.embl.cba.tables.mesh.MeshUtils;
import de.embl.cba.tables.modelview.coloring.*;
import de.embl.cba.tables.modelview.images.ImageSourcesModel;
import de.embl.cba.tables.modelview.segments.ImageSegment;
import de.embl.cba.tables.modelview.selection.SelectionListener;
import de.embl.cba.tables.modelview.selection.SelectionModel;
import ij.IJ;
import ij3d.Content;
import ij3d.Image3DUniverse;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;
import org.scijava.vecmath.Color3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.embl.cba.bdv.utils.BdvUtils.getRAI;

public class Segments3dView < T extends ImageSegment >
{
	private final List< T > segments;
	private final SelectionModel< T > selectionModel;
	private final SelectionColoringModel< T > selectionColoringModel;
	private final ImageSourcesModel imageSourcesModel;
	private ArrayList< double[] > segmentsSourceCalibrations;

	private Image3DUniverse universe;
	private T recentFocus;
	private double voxelSpacing3DView;
	private HashMap< T, CustomTriangleMesh > segmentToMesh;
	private HashMap< T, Content > segmentToContent;

	public Segments3dView(
			final List< T > segments,
			final SelectionModel< T > selectionModel,
			final SelectionColoringModel< T > selectionColoringModel,
			ImageSourcesModel imageSourcesModel )
	{

		this( segments,
				selectionModel,
				selectionColoringModel,
				imageSourcesModel,
				null );
	}

	public Segments3dView(
			final List< T > segments,
			final SelectionModel< T > selectionModel,
			final SelectionColoringModel< T > selectionColoringModel,
			ImageSourcesModel imageSourcesModel,
			Image3DUniverse universe )
	{
		this.segments = segments;
		this.selectionModel = selectionModel;
		this.selectionColoringModel = selectionColoringModel;
		this.imageSourcesModel = imageSourcesModel;
		this.universe = universe;

		this.voxelSpacing3DView = 0.1;
		this.segmentToMesh = new HashMap<>();
		this.segmentToContent = new HashMap<>();

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

	private ArrayList< double[] > getCalibrations( Source< ? > labelsSource )
	{
		final ArrayList< double[] > calibrations = new ArrayList<>();
		final int numMipmapLevels = labelsSource.getNumMipmapLevels();
		for ( int level = 0; level < numMipmapLevels; ++level )
			calibrations.add( BdvUtils.getCalibration( labelsSource, level ) );

		return calibrations;
	}


	private void registerAsColoringListener( ColoringModel< T > coloringModel )
	{
		coloringModel.listeners().add( () -> adaptSegmentColors() );
	}

	private void adaptSegmentColors()
	{
		for ( T segment : segmentToContent.keySet() )
		{
			final Color3f color3f = getColor3f( segment );
			final Content content = segmentToContent.get( segment );
			content.setColor( color3f );
		}
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

	private synchronized void showSegment( T segment )
	{
		CustomTriangleMesh triangleMesh = getTriangleMesh( segment );

		addMeshToUniverse( segment, triangleMesh );
	}

	private CustomTriangleMesh getTriangleMesh( T segment )
	{
		if ( segment.getMesh() == null )
			if ( segment.boundingBox() != null )
				addMesh( segment );
			else // TODO: Create mesh with FloodFill
				IJ.showMessage( "ImageSegments without bounding box " +
						"are currently not supported." );

		CustomTriangleMesh triangleMesh = MeshUtils.asCustomTriangleMesh( segment.getMesh() );
		triangleMesh.setColor( getColor3f( segment ) );
		return triangleMesh;
	}

	private void addMesh( ImageSegment segment )
	{
		final FinalInterval interval = segment.boundingBox();
		final RandomAccessibleInterval< ? extends RealType< ? > > rai =
				getLabelsRAI( segment );

		// TODO: get rid of raw cast, somehow....
		final MeshExtractor meshExtractor = new MeshExtractor(
				Views.extendZero( ( RandomAccessibleInterval ) rai ),
				interval,
				new AffineTransform3D(),
				new int[]{ 1, 1, 1 },
				() -> false );

		final float[] meshCoordinates = meshExtractor.generateMesh( segment.labelId() );

		segment.setMesh( meshCoordinates );
	}

	private RandomAccessibleInterval< ? extends RealType< ? > >
	getLabelsRAI( ImageSegment segment )
	{
		final Source< ? > labelsSource
				= imageSourcesModel.sources().get( segment.imageId() ).source();

		final ArrayList< double[] > calibrations = getCalibrations( labelsSource );
		final int level = getLevel( calibrations );

		// TODO: is below rai really nonVolatile???
		final RandomAccessibleInterval< ? extends RealType< ? > > rai
				= getRAI( labelsSource, 0, level );

		return rai;
	}


	private void addMeshToUniverse( T imageSegment, CustomTriangleMesh mesh )
	{
		if ( universe == null )
		{
			universe = new Image3DUniverse();
			universe.show();
		}

		final Content content =
				universe.addCustomMesh( mesh, "" + imageSegment.labelId() );

		segmentToContent.put( imageSegment, content );
	}

	private Color3f getColor3f( T imageSegment )
	{
		final ARGBType argbType = new ARGBType();
		selectionColoringModel.convert( imageSegment, argbType );
		return new Color3f( ColorUtils.getColor( argbType ) );
	}

	private int getLevel( ArrayList< double[] > calibrations )
	{
		int level;

		for ( level = 0; level < calibrations.size(); level++ )
			if ( calibrations.get( level )[ 0 ] > voxelSpacing3DView ) break;

		return level;
	}


}
