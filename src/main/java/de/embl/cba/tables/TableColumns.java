package de.embl.cba.tables;

import ij.measure.ResultsTable;
import java.io.File;
import java.util.*;

public class TableColumns
{
	public static LinkedHashMap< String,List< Object > > columnsFromImageJ1ResultsTable(
			ResultsTable resultsTable )
	{

		List< String > columnNames = Arrays.asList( resultsTable.getHeadings() );
		final int numRows = resultsTable.size();

		final LinkedHashMap< String, List< Object > > columnNamesToValues
				= new LinkedHashMap<>();

		for ( String columnName : columnNames )
		{
			System.out.println( "Parsing column: " + columnName );

			final double[] columnValues = getColumnValues( resultsTable, columnName );

			final List< Object > list = new ArrayList<>( );
			for ( int row = 0; row < numRows; ++row )
			{
				list.add( columnValues[ row ] );
			}

			columnNamesToValues.put( columnName, list );
		}

		return columnNamesToValues;
	}

	private static double[] getColumnValues( ResultsTable table, String heading )
	{
		String[] allHeaders = table.getHeadings();

		// Check if column header corresponds to row label header
		boolean hasRowLabels = hasRowLabelColumn(table);
		if (hasRowLabels && heading.equals(allHeaders[0]))
		{
			// need to parse row label column
			int nr = table.size();
			double[] values = new double[nr];
			for (int r = 0; r < nr; r++)
			{
				String label = table.getLabel(r);
				values[r] = Double.parseDouble(label);
			}
			return values;
		}

		// determine index of column
		int index = table.getColumnIndex(heading);
		if ( index == ResultsTable.COLUMN_NOT_FOUND )
		{
			throw new RuntimeException("Unable to find column index from header: " + heading);
		}
		return table.getColumnAsDoubles(index);
	}

	private static final boolean hasRowLabelColumn( ResultsTable table )
	{
		return table.getLastColumn() == (table.getHeadings().length-2);
	}

	public static LinkedHashMap< String, List< Object > > columnsFromTableFile( final File file )
	{
		return columnsFromTableFile( file, null );
	}

	public static LinkedHashMap< String, List< Object > > columnsFromTableFile(
			final File file,
			String delim )
	{
		final List< String > rowsInTable = TableUtils.readRows( file );

		delim = TableUtils.autoDelim( delim, rowsInTable );

		List< String > columns = TableUtils.getColumnNames( rowsInTable, delim );

		final LinkedHashMap< String, List< Object > > columnToValues
				= new LinkedHashMap<>();

		for ( int columnIndex = 0; columnIndex < columns.size(); columnIndex++ )
		{
			final String columnName = columns.get( columnIndex );
			columnToValues.put( columnName, new ArrayList<>( ) );
		}

		for ( int row = 1; row < rowsInTable.size(); ++row )
		{
			StringTokenizer st = new StringTokenizer( rowsInTable.get( row ), delim );

			for ( String column : columns )
			{
				final String string = st.nextToken();
				columnToValues.get( column ).add( string );
			}
		}

		return columnToValues;
	}

	public static LinkedHashMap< String, List< Object > > addLabelImageIdColumn(
			LinkedHashMap< String, List< Object > > columns,
			String columnNameLabelImageId,
			String labelImageId )
	{
		final int numRows = columns.values().iterator().next().size();

		final List< Object > labelImageIdColumn = new ArrayList<>();
		for ( int row = 0; row < numRows; row++ )
		{
			labelImageIdColumn.add( labelImageId );
		}

		columns.put( columnNameLabelImageId, labelImageIdColumn );

		return columns;
	}
}
