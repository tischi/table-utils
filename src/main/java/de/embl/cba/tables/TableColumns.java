package de.embl.cba.tables;

import ij.measure.ResultsTable;

import javax.activation.UnsupportedDataTypeException;
import javax.swing.*;
import javax.swing.table.TableModel;
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
	stringColumnsFromTableFile( final String path )
	{
		return stringColumnsFromTableFile( path, null );
	}

	public static Map< String, List< String > > stringColumnsFromTableFile(
			final String path,
			String delim )
	{
		final List< String > rowsInTableIncludingHeader = Tables.readRows( path );

		delim = Tables.autoDelim( delim, rowsInTableIncludingHeader );

		List< String > columnNames = Tables.getColumnNames( rowsInTableIncludingHeader, delim );

		final Map< String, List< String > > columnNameToStrings = new LinkedHashMap<>();

		final int numColumns = columnNames.size();

		for ( int columnIndex = 0; columnIndex < numColumns; columnIndex++ )
		{
			final String columnName = columnNames.get( columnIndex );
			columnNameToStrings.put( columnName, new ArrayList<>( ) );
		}

		final int numRows = rowsInTableIncludingHeader.size() - 1;

		final long start = System.currentTimeMillis();

		for ( int row = 1; row <= numRows; ++row )
		{
			final String[] split = rowsInTableIncludingHeader.get( row ).split( delim );
			for ( int columnIndex = 0; columnIndex < numColumns; columnIndex++ )
			{
				columnNameToStrings.get( columnNames.get( columnIndex ) ).add( split[ columnIndex ].replace( "\"", "" ) );
			}
		}

		// System.out.println( ( System.currentTimeMillis() - start ) / 1000.0 ) ;

		return columnNameToStrings;
	}

	public static Map< String, List< String > >
	orderedStringColumnsFromTableFile(
			final String path,
			String delim,
			String mergeByColumnName,
			ArrayList< Double > mergeByColumnValues )
	{
		final List< String > rowsInTableIncludingHeader = Tables.readRows( path );

		delim = Tables.autoDelim( delim, rowsInTableIncludingHeader );

		List< String > columnNames = Tables.getColumnNames( rowsInTableIncludingHeader, delim );

		final Map< String, List< String > > columnNameToStrings = new LinkedHashMap<>();

		int mergeByColumnIndex = -1;

		final int numRowsTargetTable = mergeByColumnValues.size();
		final int numColumns = columnNames.size();

		for ( int columnIndex = 0; columnIndex < numColumns; columnIndex++ )
		{
			final String columnName = columnNames.get( columnIndex );
			final ArrayList< String > values = new ArrayList< >( Collections.nCopies( numRowsTargetTable, "NaN"));
			columnNameToStrings.put( columnName, values );
			if ( columnName.equals( mergeByColumnName ) )
				mergeByColumnIndex = columnIndex;
		}

		if ( mergeByColumnIndex == -1 )
			throw new UnsupportedOperationException( "Column by which to merge not found: " + mergeByColumnName );

//		final long start = System.currentTimeMillis();
		final int numRowsSourceTable = rowsInTableIncludingHeader.size() - 1;

		for ( int rowIndex = 0; rowIndex < numRowsSourceTable; ++rowIndex )
		{
			final String[] split = rowsInTableIncludingHeader.get( rowIndex + 1 ).split( delim );
			final Double orderValue = Double.parseDouble( split[ mergeByColumnIndex ] );
			final int targetRowIndex = mergeByColumnValues.indexOf( orderValue );

			for ( int columnIndex = 0; columnIndex < numColumns; columnIndex++ )
			{
				final String columName = columnNames.get( columnIndex );
				columnNameToStrings.get( columName ).set( targetRowIndex, split[ columnIndex ].replace( "\"", "" ) );
			}
		}

//		System.out.println( ( System.currentTimeMillis() - start ) / 1000.0 ) ;

		return columnNameToStrings;
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
		else if ( isInf( s ) )
			doubles[ row ] =  Double.POSITIVE_INFINITY;
		else
			doubles[ row ] =  Double.parseDouble( s );
	}

	public static boolean isNaN( String s )
	{
		return s.toLowerCase().equals( "na" ) || s.toLowerCase().equals( "nan" ) || s.toLowerCase().equals( "none" );
	}

	public static boolean isInf( String s )
	{
		return s.equals( "Inf" );
	}

	private static Class getColumnType( String cell )
	{
		try
		{
			Double.parseDouble( cell );
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

	public static ArrayList< Double > getNumericColumnAsDoubleList( JTable table, String columnName )
	{
		final int objectLabelColumnIndex = table.getColumnModel().getColumnIndex( columnName );

		final TableModel model = table.getModel();
		final int numRows = model.getRowCount();
		final ArrayList< Double > orderColumn = new ArrayList<>();
		for ( int rowIndex = 0; rowIndex < numRows; ++rowIndex )
			orderColumn.add( Double.parseDouble( model.getValueAt( rowIndex, objectLabelColumnIndex ).toString() ) );
		return orderColumn;
	}

	public static Map< String, List< String > > openAndOrderNewColumns( JTable table, String mergeByColumnName, String newTablePath )
	{
		final ArrayList< Double > orderColumn = getNumericColumnAsDoubleList(
				table,
				mergeByColumnName );

		return orderedStringColumnsFromTableFile(
				newTablePath,
				null,
				mergeByColumnName,
				orderColumn );
	}
}
