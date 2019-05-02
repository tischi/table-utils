package de.embl.cba.tables.annotate;

import de.embl.cba.tables.tablerow.TableRow;
import org.fife.rsta.ac.js.Logger;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.util.Set;

public class Annotator
{

	public static < T extends TableRow >
	void assignAttributes(
			final String column,
			final Set< T > rows,
			final String attribute,
			JTable table )
	{
		for ( T row : rows )
			assignAttribute( column, row, attribute, table );
	}

	/**
	 * Write the values both in the TableRows and JTable
	 *
	 * @param column
	 * @param row
	 * @param attribute
	 * @param tableModel
	 * @param table
	 */
	public static  < T extends TableRow >
	void assignAttribute( String column,
						  T row,
						  String attribute,
						  JTable table )
	{

		final TableModel model = table.getModel();
		final int columnIndex = table.getColumnModel().getColumnIndex( column );

		final Object valueToBeReplaced = model.getValueAt(
				row.rowIndex(),
				columnIndex
		);

		if ( valueToBeReplaced.getClass().equals( Double.class ) )
		{
			try
			{
				final double number = Double.parseDouble( attribute );

				model.setValueAt(
						number,
						row.rowIndex(),
						columnIndex );

				row.setCell( column, Double.toString( number ) );
			}
			catch ( Exception e )
			{
				Logger.logError( "Entered value must be numeric for column: "
						+ column );
			}
		}
		else
		{
			model.setValueAt(
					attribute,
					row.rowIndex(),
					columnIndex );

			row.setCell( column, attribute );
		}
	}
}
