package de.embl.cba.tables.commands;

import bdv.util.Bdv;
import bdv.util.BdvFunctions;
import bdv.util.BdvOptions;
import bdv.util.RandomAccessibleIntervalSource4D;
import bdv.viewer.Source;
import de.embl.cba.bdv.utils.sources.SelectableARGBConvertedRealSource;
import de.embl.cba.tables.Logger;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.objects.ObjectTablePanel;
import ij.IJ;
import ij.ImagePlus;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import javax.swing.*;
import java.io.File;
import java.io.IOException;


@Plugin(type = Command.class, menuPath = "Plugins>Screening>Explore CellProfiler Output" )
public class ExploreCellProfilerOutputCommand< R extends RealType< R > & NativeType< R > >
		implements Command
{
	@Parameter ( label = "CellProfiler Table" )
	public File inputTableFile;

	@Override
	public void run()
	{
		final JTable table = loadTable( inputTableFile );


//		final SelectableARGBConvertedRealSource labelsSource = loadLabels();
//
//		final Bdv bdv = showImagesWithBdv( labelsSource );
//
//		ObjectTablePanel objectTablePanel = createAndShowTablePanel( table );
//
//		TableBdvConnector tableBdvConnector = new TableBdvConnector(
//				objectTablePanel,
//				new BdvSelectionEventHandler(
//						bdv,
//						labelsSource )
//		);
//
//		tableBdvConnector.setSelectionByAttribute( true );
//
//		new ObjectCoordinateColumnsSelectionUI( objectTablePanel );
	}

	public ObjectTablePanel createAndShowTablePanel( JTable table )
	{
		ObjectTablePanel objectTablePanel = new ObjectTablePanel( table, inputTableFile.getName() );
		objectTablePanel.showTable();
		return objectTablePanel;
	}

	public Bdv showImagesWithBdv( SelectableARGBConvertedRealSource labelsSource )
	{
		int nT = getNumTimePoints( labelsSource );

		final long nZ = labelsSource.getSource( 0, 0 ).dimension( 2 );

		BdvOptions bdvOptions = BdvOptions.options();

		if ( nZ == 1 ) bdvOptions = bdvOptions.is2D();

		return BdvFunctions.show(
					labelsSource,
					nT,
					bdvOptions ).getBdvHandle();
	}

	public static int getNumTimePoints( Source labelsSource )
	{
		int nT = 0;
		while ( labelsSource.isPresent( nT  ) ) nT++;
		return nT;
	}

//
//	public SelectableARGBConvertedRealSource loadLabels()
//	{
//		final ArrayList< RandomAccessibleIntervalSource4D< R > > sources =
//				Wraps.imagePlusAsSource4DChannelList( IJ.openImage( inputLabelMasksFile.toString() ) );
//
//		if ( sources.size() > 1 )
//		{
//			Logger.error( "Label input image must be single channel!" );
//			return null;
//		}
//
//		return new SelectableARGBConvertedRealSource( sources.get( 0 ) );
//	}

	public JTable loadTable( File file )
	{
		try
		{
			return TableUtils.loadTable( file, null );
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}

		return null;
	}

	public static RandomAccessibleIntervalSource4D loadImage( File file )
	{
		final ImagePlus imagePlus = IJ.openImage( file.toString() );

		if ( imagePlus.getNChannels() > 1 )
		{
			Logger.error( "Only single channel images are supported.");
			return null;
		}

		RandomAccessibleInterval< RealType > wrap = ImageJFunctions.wrapReal( imagePlus );

		if ( imagePlus.getNFrames() == 1 )
		{
			// needs to be a movie
			wrap = Views.addDimension( wrap, 0, 0 );
		}

		if ( imagePlus.getNSlices() == 1 )
		{
			// needs to be 3D
			wrap = Views.addDimension( wrap, 0, 0 );
			wrap = Views.permute( wrap, 2, 3 );
		}

		return new RandomAccessibleIntervalSource4D( wrap, Util.getTypeFromInterval( wrap ), imagePlus.getTitle() );
	}

}
