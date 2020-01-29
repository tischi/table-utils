package de.embl.cba.tables.view.dialogs;

import bdv.cache.CacheControl;
import bdv.util.Prefs;
import bdv.viewer.ViewerPanel;
import bdv.viewer.overlay.ScaleBarOverlayRenderer;
import bdv.viewer.render.MultiResolutionRenderer;
import bdv.viewer.state.ViewerState;
import de.embl.cba.tables.view.SegmentsBdvView;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.ui.PainterThread;
import net.imglib2.ui.RenderTarget;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import static de.embl.cba.tables.SwingUtils.horizontalLayoutPanel;


public class BdvViewSourcesBrowsingAndActionsDialog extends JPanel
{
	private final SegmentsBdvView bdvView;

	public BdvViewSourcesBrowsingAndActionsDialog( SegmentsBdvView bdvView )
	{
		this.bdvView = bdvView;
		configPanel();
		addSourceSetSelectionPanel();
		addBatchSourceSavingButton();
		showFrame();
	}


	private void configPanel()
	{
		setLayout( new BoxLayout(this, BoxLayout.Y_AXIS ) );
		setAlignmentX( Component.LEFT_ALIGNMENT );
	}

	private void showFrame()
	{
		final JFrame frame = new JFrame();
		frame.setContentPane( this );
		frame.setLocation( MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y );
		frame.pack();
		frame.setVisible( true );
	}

	private void addSourceSetSelectionPanel( )
	{
		final JPanel horizontalLayoutPanel = horizontalLayoutPanel();

		final JComboBox< String > comboBox = new JComboBox();
		final ArrayList< String > sourceSetIds = bdvView.getSourceSetIds();

		for ( String sourceSet : sourceSetIds )
			comboBox.addItem( new File( sourceSet ).getName() );

		final JButton button = new JButton( "Show image set" );

		button.addActionListener( e -> {
			bdvView.updateImageSet(
					sourceSetIds.get(
						comboBox.getSelectedIndex() ) );
		} );

		horizontalLayoutPanel.add( button );
		horizontalLayoutPanel.add( comboBox );
		this.add( horizontalLayoutPanel );
	}

	private void addBatchSourceSavingButton( )
	{
		final JPanel horizontalLayoutPanel = horizontalLayoutPanel();

		final ArrayList< String > sourceSetIds = bdvView.getSourceSetIds();

		final JButton button = new JButton( "Save current view for all image sets" );

		button.addActionListener( e -> {
			sourceSetIds.forEach( sourceSetId -> {
				bdvView.updateImageSet( sourceSetId );
				saveScreenShot( new File( "/Users/tischer/Desktop/daja-out/" + sourceSetId + ".png" ), bdvView.getBdv().getViewerPanel() );
			} );
		} );

		horizontalLayoutPanel.add( button );
		this.add( horizontalLayoutPanel );
	}

	public void saveScreenShot( final File outputFile, ViewerPanel viewer )
	{
		final ViewerState renderState = viewer.getState();
		final int canvasW = viewer.getDisplay().getWidth();
		final int canvasH = viewer.getDisplay().getHeight();

		final int width = canvasW;
		final int height = canvasH;


		final AffineTransform3D affine = new AffineTransform3D();
		renderState.getViewerTransform( affine );
		affine.set( affine.get( 0, 3 ) - canvasW / 2, 0, 3 );
		affine.set( affine.get( 1, 3 ) - canvasH / 2, 1, 3 );
		affine.scale( ( double ) width / canvasW );
		affine.set( affine.get( 0, 3 ) + width / 2, 0, 3 );
		affine.set( affine.get( 1, 3 ) + height / 2, 1, 3 );
		renderState.setViewerTransform( affine );

		final ScaleBarOverlayRenderer scalebar = Prefs.showScaleBarInMovie() ? new ScaleBarOverlayRenderer() : null;

		class MyTarget implements RenderTarget
		{
			BufferedImage bi;

			@Override
			public BufferedImage setBufferedImage( final BufferedImage bufferedImage )
			{
				bi = bufferedImage;
				return null;
			}

			@Override
			public int getWidth()
			{
				return width;
			}

			@Override
			public int getHeight()
			{
				return height;
			}
		}

		final MyTarget target = new MyTarget();
		final MultiResolutionRenderer renderer = new MultiResolutionRenderer(
				target, new PainterThread( null ), new double[] { 1 }, 0, false, 1, null, false,
				viewer.getOptionValues().getAccumulateProjectorFactory(), new CacheControl.Dummy() );

		int minTimepointIndex = 0;
		int maxTimepointIndex = 0;

		for ( int timepoint = minTimepointIndex; timepoint <= maxTimepointIndex; ++timepoint )
		{
			renderState.setCurrentTimepoint( timepoint );
			renderer.requestRepaint();
			renderer.paint( renderState );

			if ( Prefs.showScaleBarInMovie() )
			{
				final Graphics2D g2 = target.bi.createGraphics();
				g2.setClip( 0, 0, width, height );
				scalebar.setViewerState( renderState );
				scalebar.paint( g2 );
			}

			try
			{
				ImageIO.write( target.bi, "png", outputFile );
			} catch ( IOException e )
			{
				e.printStackTrace();
			}
		}
	}


}
