package de.embl.cba.tables.table;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class ColumnClassAwareTableModel extends DefaultTableModel
{
	List< Class > columnClasses;

	public ColumnClassAwareTableModel( )
	{
		super();
	}

	public ColumnClassAwareTableModel( int rowCount, int columnCount )
	{
		super( rowCount, columnCount );
	}

	@Override
	public Class getColumnClass( int column )
	{
		return columnClasses.get( column );
	}

	@Override
	public boolean isCellEditable( int row, int column )
	{
		return true;
	}

	/**
	 * Determines getColumnName classes from entries in 1st row.
	 */
	public void refreshColumnClassesFromStringColumns()
	{
		columnClasses = new ArrayList<>(  );

		for ( int column = 0; column < getColumnCount(); column++ )
		{
			final String string = (String) this.getValueAt( 0, column );

			try
			{
				Double.parseDouble( string );
				columnClasses.add( Double.class );
			}
			catch ( Exception e )
			{
				columnClasses.add( String.class );
			}
		}
	}

	/**
	 * Determines getColumnName classes from entries in 1st row.
	 */
	public void refreshColumnClassesFromObjectColumns()
	{
		columnClasses = new ArrayList<>(  );

		for ( int column = 0; column < getColumnCount(); column++ )
		{
			final Object value = this.getValueAt( 0, column );
			columnClasses.add( value.getClass() );
		}
	}

	public void addColumnClass( Object aValue )
	{
		columnClasses.add( aValue.getClass() );
	}

}
