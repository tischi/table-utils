package example;

import de.embl.cba.table.color.ColoringModel;
import de.embl.cba.table.color.SelectionColoringModel;
import de.embl.cba.table.select.DefaultSelectionModel;
import de.embl.cba.table.tablerow.ColumnBasedTableRow;
import de.embl.cba.table.util.TableUtils;
import de.embl.cba.table.view.TableRowsTableView;

import javax.swing.colorchooser.DefaultColorSelectionModel;
import java.util.List;
import java.util.Map;

public class OpenAndShowTable
{
	public static void main( String[] args )
	{
		final String tablePath = OpenAndShowTable.class.getResource( "tables/3d-image-lbl-morpho.csv" ).getFile();

		final Map< String, List< String > > columnNamesToColumns = TableUtils.loadColumns( tablePath );

		final List< ColumnBasedTableRow > columnBasedTableRows = TableUtils.columnBasedTableRowsFromColumns( columnNamesToColumns );

		final DefaultSelectionModel< ColumnBasedTableRow > selectionModel = new DefaultSelectionModel<>();

		final ColoringModel< ColumnBasedTableRow > columnBasedTableRowDefaultSelectionModel = new DefaultColorSelectionModel<>();

		final TableRowsTableView< ColumnBasedTableRow > tableView =
				new TableRowsTableView<>( columnBasedTableRows, selectionModel,

				);

		tableView.showTableAndMenu();


	}

}

