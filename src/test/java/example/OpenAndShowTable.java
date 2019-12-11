package example;

import de.embl.cba.table.color.LazyCategoryColoringModel;
import de.embl.cba.table.lut.GlasbeyARGBLut;
import de.embl.cba.table.select.DefaultSelectionModel;
import de.embl.cba.table.tablerow.ColumnBasedTableRow;
import de.embl.cba.table.util.TableUtils;
import de.embl.cba.table.view.TableRowsTableView;

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

		final LazyCategoryColoringModel< ColumnBasedTableRow > coloringModel = new LazyCategoryColoringModel< >( new GlasbeyARGBLut( 255 ) );

		final TableRowsTableView< ColumnBasedTableRow > tableView =
				new TableRowsTableView<>(
						columnBasedTableRows,
						selectionModel,
						coloringModel
				);

		tableView.showTableAndMenu();
	}

}

