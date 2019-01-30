import bdv.util.Bdv;
import bdv.util.BdvFunctions;
import bdv.util.BdvOptions;
import bdv.util.RandomAccessibleIntervalSource;
import de.embl.cba.bdv.utils.selection.BdvSelectionEventHandler;
import de.embl.cba.bdv.utils.sources.SelectableARGBConvertedRealSource;
import de.embl.cba.tables.modelview.segments.ImageSegmentCoordinate;
import de.embl.cba.tables.objects.ObjectTablePanel;
import net.imagej.ImageJ;

import javax.swing.*;
import java.io.IOException;

public class ExampleInteractiveObjectGrouping
{
	public static void main( String[] args ) throws IOException
	{

		/**
		 * Example of interactive object selections and groupings
		 *
		 * Ctrl + Left-Click: Select/Unselect object(s) in image
		 *
		 * Ctrl + Q: Select none
		 *
		 * Ctrl + G: Assign selected object(s) to a group
		 *
		 */

		new ImageJ().ui().showUI();

		/**
		 * Load and showImageSourcesInBdv image
		 */

		final RandomAccessibleIntervalSource raiSource = Examples.load2D16BitLabelSource();

		final SelectableARGBConvertedRealSource selectableSource =
				new SelectableARGBConvertedRealSource( raiSource );

		Bdv bdv = BdvFunctions.show( selectableSource, BdvOptions.options().is2D() ).getBdvHandle();

		/**
		 * Load table and add a group getColumn
		 */

		final JTable jTable = Examples.loadObjectTableFor2D16BitLabelMask();

		final ObjectTablePanel objectTablePanel = new ObjectTablePanel( jTable, "Table" );
		objectTablePanel.showTable();
		objectTablePanel.setCoordinateColumn( ImageSegmentCoordinate.Label, jTable.getColumnName( 0 ) );
		objectTablePanel.addColumn( "MyGrouping", "None" );

		/**
		 * Configure interactive object attributes
		 */

		// Add a behaviour to Bdv, enabling selection of labels by Ctrl + Left-Click
		//
		final BdvSelectionEventHandler bdvSelectionEventHandler =
				new BdvSelectionEventHandler( bdv, selectableSource );


		// Define additional behaviour: assigning attributes by Ctrl + G
		//
		// Broken: needs a fix
//		final Behaviours behaviours = new Behaviours( new InputTriggerConfig() );
//		behaviours.install( bdv.getBdvHandle().getTriggerbindings(), "bdv-object-attributes-" + selectableSource.getName() );
//
//		final AssignObjectAttributesUI assignObjectAttributesUI
//				= new AssignObjectAttributesUI( objectTablePanel );
//
//		behaviours.behaviour( ( ClickBehaviour ) ( labelImageIndex, y ) ->
//		{
//			assignObjectAttributesUI.showUI( bdvSelectionEventHandler.getSelectedValues()  );
//		}
//		, "fetch-curently-selected-segments-" + selectableSource.getName(), Examples.OBJECT_GROUPING_TRIGGER  );

	}
}
