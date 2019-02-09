package de.embl.cba.tables;

import de.embl.cba.tables.modelview.segments.DefaultTableRowImageSegment;
import ij.measure.ResultsTable;

import java.io.File;
import java.util.*;

public class TableColumns
{
	public static LinkedHashMap< String,List< Object > > columnsFromImageJ1ResultsTable(
			ResultsTable resultsTable )
	{
		List< String > columns = Arrays.asList( resultsTable.getHeadings() );

		final LinkedHashMap< String, List< Object > > columnToValues
				= new LinkedHashMap<>();

		for ( int columnIndex = 0; columnIndex < columns.size(); columnIndex++ )
		{
			final String columnName = columns.get( columnIndex );
			final List< Object > columnEntries = Arrays.asList( resultsTable.getColumn( columnIndex ) );
			columnToValues.put( columnName, columnEntries );
		}

		return columnToValues;
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
			columnToValues.put( columnName, new ArrayList<>(  ) );
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
