import bdv.VolatileSpimSource;
import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import de.embl.cba.bdv.utils.BdvUtils;
import de.embl.cba.bdv.utils.converters.LinearMappingARGBConverter;
import de.embl.cba.bdv.utils.converters.SelectableVolatileARGBConverter;
import de.embl.cba.bdv.utils.lut.Luts;
import de.embl.cba.bdv.utils.selection.BdvSelectionEventHandler;
import de.embl.cba.bdv.utils.sources.SelectableVolatileARGBConvertedRealSource;
import de.embl.cba.tables.TableBdvConnector;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.objects.ObjectCoordinate;
import de.embl.cba.tables.objects.ObjectTablePanel;
import mpicbg.spim.data.SpimData;
import mpicbg.spim.data.SpimDataException;
import mpicbg.spim.data.XmlIoSpimData;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.volatiles.VolatileARGBType;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

public class ExampleLargeTableLargeImage
{
	public static void main( String[] args ) throws SpimDataException, IOException
	{
		SpimData labels = new XmlIoSpimData().load( "/Volumes/arendt/EM_6dpf_segmentation/EM-Prospr/em-segmented-cells-labels.xml" );

		final LinearMappingARGBConverter linearMappingARGBConverter =
				new LinearMappingARGBConverter( d -> d + 1, 0, 50, Luts.BLUE_WHITE_RED  );

		final SelectableVolatileARGBConverter selectableVolatileARGBConverter =
				new SelectableVolatileARGBConverter( linearMappingARGBConverter );

		final VolatileSpimSource volatileSpimSource = new VolatileSpimSource( labels, 0, "name" );

		final SelectableVolatileARGBConvertedRealSource labelSource =
				new SelectableVolatileARGBConvertedRealSource(
						volatileSpimSource,
						selectableVolatileARGBConverter );


		final BdvHandle bdvHandle = BdvFunctions.show( labelSource ).getBdvHandle();

		SpimData emRaw = new XmlIoSpimData().load( "/Volumes/arendt/EM_6dpf_segmentation/EM-Prospr/em-raw-full-res.xml" );

		BdvFunctions.show( emRaw, BdvOptions.options().addTo( bdvHandle ) ).get( 0 ).setDisplayRange( 0, 500 );

		final JTable jTable = TableUtils.loadTable(
				new File( "/Volumes/arendt/EM_6dpf_segmentation/EM-Prospr/label_attributes/em-segmented-cells-labels-morphology-v2.csv" ),
				"\t" );

		final ObjectTablePanel objectTablePanel = new ObjectTablePanel( jTable );
		objectTablePanel.setCoordinateColumn( ObjectCoordinate.Label, "label_id" );
		objectTablePanel.setCoordinateColumn( ObjectCoordinate.X, "com_x_microns" );
		objectTablePanel.setCoordinateColumn( ObjectCoordinate.Y, "com_y_microns" );
		objectTablePanel.setCoordinateColumn( ObjectCoordinate.Z, "com_z_microns" );

		// objectTablePanel.showPanel();

		final BdvSelectionEventHandler bdvSelectionEventHandler =
				new BdvSelectionEventHandler(
					bdvHandle,
					labelSource );

		// set up mutual interaction between table and bdv-source
		//
		final TableBdvConnector tableBdvConnector = new TableBdvConnector( objectTablePanel, bdvSelectionEventHandler );

		final LinearMappingARGBConverter tableMapping = tableBdvConnector.createLinearMappingARGBConverter( "com_x_microns" );

//		final JTable table = objectTablePanel.getTable();

//		final LinearMappingARGBConverter otherMapping = new LinearMappingARGBConverter( new Function< Double, Double >()
//		{
//			@Override
//			public Double apply( Double aDouble )
//			{
//				final int rowIndex = objectTablePanel.getRowIndex( aDouble );
//				final Double valueAt = ( Double ) table.getValueAt( rowIndex, 3 );
//
//				return null;
//			}
//		}, 0, 1000, Luts.BLUE_WHITE_RED );


		labelSource.getSelectableConverter().setWrappedConverter( tableMapping );

		//bdvSelectionEventHandler.getSelectableConverter().setWrappedConverter( mappingARGBConverter );

		BdvUtils.repaint( bdvHandle );

	}
}
