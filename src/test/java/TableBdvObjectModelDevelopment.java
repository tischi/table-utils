import de.embl.cba.bdv.utils.sources.SelectableARGBConvertedRealSource;
import de.embl.cba.tables.tablebdvobject.*;

import javax.swing.*;
import java.io.IOException;

public class TableBdvObjectModelDevelopment
{
	public static void main( String[] args ) throws IOException
	{

		final JTable table = Examples.loadObjectTableFor2D16BitLabelMask();
		final SelectableARGBConvertedRealSource source = Examples.loadSelectableSource();

		final SegmentationInstancesModel model = new SegmentationInstancesModel(
				source,
				table,
				"Label",
				null,
				true );

		final SelectionModel< SegmentationInstance > selectionModel = new DefaultSelectionModel<>();

		final BdvView< SegmentationInstance > bdvView = new BdvView( model, selectionModel );
		final TableView< SegmentationInstance > tableView = new TableView<>( model, selectionModel );

		selectionModel.listeners().add( bdvView );
		selectionModel.listeners().add( tableView );

	}
}
