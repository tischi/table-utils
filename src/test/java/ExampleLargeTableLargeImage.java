import bdv.VolatileSpimSource;
import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import de.embl.cba.bdv.utils.BdvUtils;
import de.embl.cba.bdv.utils.converters.MappingLinearARGBConverter;
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

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class ExampleLargeTableLargeImage
{
	public static void main( String[] args ) throws SpimDataException, IOException
	{
		SpimData labels = new XmlIoSpimData().load( "/Volumes/arendt/EM_6dpf_segmentation/EM-Prospr/em-segmented-cells-labels.xml" );

		final MappingLinearARGBConverter linearMappingARGBConverter =
				new MappingLinearARGBConverter( 0, 50, Luts.BLUE_WHITE_RED, d -> d + 1  );

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

		final MappingLinearARGBConverter tableMapping = tableBdvConnector.createMappingLinearARGBConverter( "com_x_microns" );

		labelSource.getSelectableConverter().setWrappedConverter( tableMapping );

		BdvUtils.repaint( bdvHandle );

	}
}
