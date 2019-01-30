import bdv.util.RandomAccessibleIntervalSource;
import de.embl.cba.bdv.utils.sources.SelectableARGBConvertedRealSource;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.datamodels.CellProfilerImageSourcesModel;
import de.embl.cba.tables.modelview.objects.ImageSegmentCoordinate;
import de.embl.cba.tables.objects.ObjectTablePanel;
import ij.IJ;
import ij.ImagePlus;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public abstract class Examples
{

	public static final String OBJECT_GROUPING_TRIGGER = "ctrl A";

	public static JTable loadObjectTableFor2D16BitLabelMask() throws IOException
	{
		final File tableFile = new File( Examples.class.getResource( "2d-16bit-labelMask-Morphometry.csv" ).getFile() );

		return TableUtils.loadTable( tableFile, "," );
	}

	public static RandomAccessibleIntervalSource load2D16BitLabelSource()
	{
		final ImagePlus imagePlus = IJ.openImage( Examples.class.getResource( "2d-16bit-labelMask.tif" ).getFile() );

		final RandomAccessibleIntervalSource source = CellProfilerImageSourcesModel.imagePlus2DAsSource3D( imagePlus );

		return source;
	}


	public static SelectableARGBConvertedRealSource loadSelectableSource()
	{
		return ( new SelectableARGBConvertedRealSource( Examples.load2D16BitLabelSource() ) );
	}


	public static void createInteractiveTablePanel( JTable jTable )
	{
		final ObjectTablePanel objectTablePanel = new ObjectTablePanel( jTable );

		objectTablePanel.showTable();

		objectTablePanel.setCoordinateColumn( ImageSegmentCoordinate.Label, jTable.getColumnName( 0 ) );
	}
}
