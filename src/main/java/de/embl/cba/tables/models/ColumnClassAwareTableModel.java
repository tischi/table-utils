package de.embl.cba.tables.models;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class ColumnClassAwareTableModel extends DefaultTableModel
{
	ArrayList< Class > columnClasses;

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
		return false;
	}

	/**
	 * Determines getFeature classes from entries in 1st row.
	 */
	public void refreshColumnClassesFromStringColumns()
	{
		columnClasses = new ArrayList<>(  );

		for ( int column = 0; column < getColumnCount(); column++ )
		{
			final String string = (String) this.getValueAt( 1, column );

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
	 * Determines getFeature classes from entries in 1st row.
	 */
	public void refreshColumnClassesFromObjectColumns()
	{
		columnClasses = new ArrayList<>(  );

		for ( int column = 0; column < getColumnCount(); column++ )
		{
			final Object value = this.getValueAt( 1, column );
			columnClasses.add( value.getClass() );
		}
	}

	public void addColumnClass( Object aValue )
	{
		columnClasses.add( aValue.getClass() );
	}

}
