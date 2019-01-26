package de.embl.cba.tables.cellprofiler;

import bdv.util.*;
import bdv.viewer.Source;
import de.embl.cba.bdv.utils.selection.BdvSelectionEventHandler;
import de.embl.cba.bdv.utils.sources.SelectableARGBConvertedRealSource;
import de.embl.cba.tables.TableBdvConnector;
import de.embl.cba.tables.objects.ObjectTablePanel;
import ij.IJ;
import ij.ImagePlus;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

import javax.swing.*;
import java.util.HashMap;

@Deprecated
public class CellProfilerOutputExplorer < T extends RealType< T > & NativeType< T > >
{
	BdvHandle bdv;
	private final ObjectTablePanel tablePanel;

	public CellProfilerOutputExplorer(
			JTable table,
			HashMap< Object, CellProfilerDataset > datasets )
	{
		tablePanel = new ObjectTablePanel( table );
		tablePanel.showTable();

		new CellProfilerDatasetSelectionUI( this, datasets );
	}

	public void showImages( CellProfilerDataset dataset )
	{
		if( bdv != null ) bdv.close();

		for ( CellProfilerDataset.ImageTypeAndPath image : dataset.getImages() )
		{

			if ( image.type.equals( CellProfilerDataset.OBJECT_LABEL_MASK ) )
			{
				final ImagePlus imagePlus2D = IJ.openImage( image.path );

				RandomAccessibleInterval< T > img = wrapAs3D( imagePlus2D );

				Source source = new RandomAccessibleIntervalSource( img, Util.getTypeFromInterval( img ), imagePlus2D.getTitle() );

				source = new SelectableARGBConvertedRealSource( source );

				bdv = BdvFunctions.show(
						source,
						BdvOptions.options().is2D().addTo( bdv ) ).getBdvHandle();

				new TableBdvConnector(
						tablePanel,
						new BdvSelectionEventHandler( bdv, ( SelectableARGBConvertedRealSource ) source )
				);

//				new ObjectCoordinateColumnsSelectionUI( tablePanel );
			}
		}
	}

	public RandomAccessibleInterval< T > wrapAs3D( ImagePlus imagePlus2D )
	{
		RandomAccessibleInterval< T > img = ImageJFunctions.wrapReal( imagePlus2D );
		img = Views.addDimension( img, 0, 0);
		return img;
	}
}
