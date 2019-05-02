package de.embl.cba.tables;

import ij.measure.ResultsTable;

import javax.activation.UnsupportedDataTypeException;
import java.io.File;
import java.util.*;

public class TableColumns
{
	public static Map< String, List< String > >
			columnsFromImageJ1ResultsTable(
			ResultsTable resultsTable )
	{

		List< String > columnNames = Arrays.asList( resultsTable.getHeadings() );
		final int numRows = resultsTable.size();

		final Map< String, List< String > > columnNamesToValues
				= new LinkedHashMap<>();

		for ( String columnName : columnNames )
		{
			System.out.println( "Parsing column: " + columnName );

			final double[] columnValues = getColumnValues( resultsTable, columnName );

			final List< String > list = new ArrayList<>( );
			for ( int row = 0; row < numRows; ++row )
				list.add( "" + columnValues[ row ] );

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

	public static Map< String, List< String > >
	stringColumnsFromTableFile( final File file )
	{
		return stringColumnsFromTableFile( file, null );
	}

	public static Map< String, List< String > >
	stringColumnsFromTableFile(
			final File file,
			String delim )
	{
		final List< String > rowsInTableIncludingHeader = Tables.readRows( file );

		delim = Tables.autoDelim( delim, rowsInTableIncludingHeader );

		List< String > columnNames = Tables.getColumnNames( rowsInTableIncludingHeader, delim );

		final Map< String, List< String > > columnToStringValues = new LinkedHashMap<>();

		for ( int columnIndex = 0; columnIndex < columnNames.size(); columnIndex++ )
		{
			final String columnName = columnNames.get( columnIndex );
			columnToStringValues.put( columnName, new ArrayList<>( ) );
		}

		final int numRows = rowsInTableIncludingHeader.size() - 1;

		for ( int row = 0; row < numRows; ++row )
		{
			StringTokenizer st = new StringTokenizer( rowsInTableIncludingHeader.get( row + 1 ), delim );

			for ( String column : columnNames )
			{
				String string = st.nextToken();
				string = string.replace( "\"", "" );
				columnToStringValues.get( column ).add( string );
			}
		}

		return columnToStringValues;
	}

	public static Map< String, List< ? > >
	asTypedColumns( Map< String, List< String > > columnToStringValues )
			throws UnsupportedDataTypeException
	{
		final Set< String > columnNames = columnToStringValues.keySet();

		final LinkedHashMap< String, List< ? > > columnToValues = new LinkedHashMap<>();

		for ( String columnName : columnNames )
		{
			final List< ? > values = asTypedList( columnToStringValues.get( columnName ) );
			columnToValues.put( columnName, values );
		}
		return columnToValues;
	}

	public static List< ? > asTypedList( List< String > strings )
			throws UnsupportedDataTypeException
	{
		final Class columnType = getColumnType( strings.get( 0 ) );

		int numRows = strings.size();

		if ( columnType == Double.class )
		{
			final ArrayList< Double > doubles = new ArrayList<>( numRows );

			for ( int row = 0; row < numRows; ++row )
				toDouble( strings, doubles, row );

			return doubles;
		}
		else if ( columnType == Integer.class ) // cast to Double anyway...
		{
			final ArrayList< Double > doubles = new ArrayList<>( numRows );

			for ( int row = 0; row < numRows; ++row )
				toDouble( strings, doubles, row );

			return doubles;
		}
		else if ( columnType == String.class )
		{
			return strings;
		}
		else
		{
			throw new UnsupportedDataTypeException("");
		}
	}

	public static Object[] asTypedArray( List< String > strings )
			throws UnsupportedDataTypeException
	{
		final Class columnType = getColumnType( strings.get( 0 ) );

		int numRows = strings.size();

		if ( columnType == Double.class )
		{
			return toDoubles( strings, numRows );
		}
		else if ( columnType == Integer.class ) // cast to Double anyway...
		{
			return toDoubles( strings, numRows );
		}
		else if ( columnType == String.class )
		{
			final String[] stringsArray = new String[ strings.size() ];
			strings.toArray( stringsArray );
			return stringsArray;
		}
		else
		{
			throw new UnsupportedDataTypeException("");
		}

	}

	public static Object[] toDoubles( List< String > strings, int numRows )
	{
		final Double[] doubles = new Double[ numRows ];

		for ( int row = 0; row < numRows; ++row )
			toDouble( strings, doubles, row );

		return doubles;
	}

	public static void toDouble( List< String > strings, ArrayList< Double > doubles, int row )
	{
		final String s = strings.get( row );
		if ( isNaN( s ) )
			doubles.add( Double.NaN );
		else
			doubles.add( Double.parseDouble( s ) );
	}

	public static void toDouble( List< String > strings, Double[] doubles, int row )
	{
		final String s = strings.get( row );
		if ( isNaN( s ) )
			doubles[ row ] =  Double.NaN ;
		else
			doubles[ row ] =  Double.parseDouble( s );
	}


	public static boolean isNaN( String s )
	{
		return s.equals( "NA" );
	}

	private static Class getColumnType( String string )
	{
		try
		{
			Double.parseDouble( string );
			return Double.class;
		}
		catch ( Exception e2 )
		{
			return String.class;
		}
	}

	public static Map< String, List< String > > addLabelImageIdColumn(
			Map< String, List< String > > columns,
			String columnNameLabelImageId,
			String labelImageId )
	{
		final int numRows = columns.values().iterator().next().size();

		final List< String > labelImageIdColumn = new ArrayList<>();

		for ( int row = 0; row < numRows; row++ )
			labelImageIdColumn.add( labelImageId );

		columns.put( columnNameLabelImageId, labelImageIdColumn );

		return columns;
	}
}
