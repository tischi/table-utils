package de.embl.cba.tables;

import de.embl.cba.tables.models.ColumnClassAwareTableModel;
import de.embl.cba.tables.modelview.datamodels.SegmentUtils;
import de.embl.cba.tables.modelview.objects.*;
import de.embl.cba.tables.modelview.objects.ImageSegmentCoordinate;
import net.imglib2.util.ValuePair;
import org.scijava.table.GenericTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class TableUtils
{
	public static JTable asJTable( GenericTable genericTable )
	{
		final int numCols = genericTable.getColumnCount();

		DefaultTableModel model = new DefaultTableModel();

		for ( int col = 0; col < numCols; ++col )
		{
			model.addColumn( genericTable.getColumnHeader( col ) );
		}

		for ( int row = 0; row < genericTable.getRowCount(); ++row )
		{
			final String[] rowEntries = new String[ numCols ];

			for ( int col = 0; col < numCols; ++col )
			{
				rowEntries[ col ] = ( String ) genericTable.get( col, row );
			}

			model.addRow( rowEntries );
		}

		return new JTable( model );
	}


	public static void saveTable( JTable table, File tableOutputFile )
	{
		try
		{
			TableUtils.saveTableWithIOException( table, tableOutputFile );
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
	}

	private static void saveTableWithIOException( JTable table, File file ) throws IOException
	{
		BufferedWriter bfw = new BufferedWriter( new FileWriter( file ) );

		// header
		for ( int column = 0; column < table.getColumnCount(); column++ )
		{
			bfw.write( table.getColumnName( column ) + "\t" );
		}
		bfw.write( "\n" );

		// content
		for ( int row = 0; row < table.getRowCount(); row++ )
		{
			for ( int column = 0; column < table.getColumnCount(); column++ )
			{
				bfw.write( table.getValueAt( row, column ) + "\t" );
			}
			bfw.write( "\n" );
		}

		bfw.close();
	}

	public static JTable loadTable( final File file, String delim ) throws IOException
	{
		ArrayList< String > rows = readRows( file );

		return createJTableFromStringList( rows, delim );
	}

	
	public static ArrayList< String > readRows( File file )
	{
		ArrayList< String > rows = new ArrayList<>();

		try
		{
			FileInputStream fin = new FileInputStream( file );
			BufferedReader br = new BufferedReader( new InputStreamReader( fin ) );

			String aRow;

			while ( ( aRow = br.readLine() ) != null )
			{
				rows.add( aRow );
			}

			br.close();
		} catch ( Exception e )
		{
			e.printStackTrace();
		}
		return rows;
	}


	public static ArrayList< String > getColumnNames( ArrayList< String > strings, String delim )
	{
		StringTokenizer st = new StringTokenizer( strings.get( 0 ), delim );

		ArrayList< String > featureNames = new ArrayList<>();

		while ( st.hasMoreTokens() )
		{
			featureNames.add( st.nextToken() );
		}
		return featureNames;
	}

	public static String autoFixDelim( String delim, ArrayList< String > strings )
	{
		if ( delim == null )
		{
			if ( strings.get( 0 ).contains( "\t" ) )
			{
				delim = "\t";
			}
			else if ( strings.get( 0 ).contains( "," )  )
			{
				delim = ",";
			}
		}
		return delim;
	}

	public static JTable createJTableFromStringList( ArrayList< String > strings, String delim )
	{

		delim = autoFixDelim( delim, strings );

		StringTokenizer st = new StringTokenizer( strings.get( 0 ), delim );

		ArrayList< String > colNames = new ArrayList<>();

		while ( st.hasMoreTokens() )
		{
			colNames.add( st.nextToken() );
		}

		/**
		 * Init model and columns
		 */

		ColumnClassAwareTableModel model = new ColumnClassAwareTableModel();

		for ( String colName : colNames )
		{
			model.addColumn( colName );
		}

		int numCols = colNames.size();

		/**
		 * Add rows entries
		 */

		for ( int iString = 1; iString < strings.size(); ++iString )
		{
			model.addRow( new Object[ numCols ] );

			st = new StringTokenizer( strings.get( iString ), delim );

			for ( int iCol = 0; iCol < numCols; iCol++ )
			{
				String stringValue = st.nextToken();

				try
				{
					final Double numericValue = Double.parseDouble( stringValue );
					model.setValueAt( numericValue, iString - 1, iCol );
				}
				catch ( Exception e )
				{
					model.setValueAt( stringValue, iString - 1, iCol );
				}
			}

		}

		model.refreshColumnClassesFromObjectColumns();

		return new JTable( model );
	}

	public static ArrayList< DefaultAnnotatedImageSegment > segmentsFromTableFile (
			File file,
			String delim,
			Map< ImageSegmentCoordinate, ValuePair< String, Integer > > coordinateColumnMap
	)
	{

		final ArrayList< DefaultAnnotatedImageSegment > segments = new ArrayList<>();

		final ArrayList< String > tableRows = readRows( file );

		delim = autoFixDelim( delim, tableRows );

		ArrayList< String > columns = getColumnNames( tableRows, delim );

		for ( int columnIndex = 0; columnIndex < columns.size(); columnIndex++ )
		{
			final String columnName = columns.get( columnIndex );

			setColumnIndex( coordinateColumnMap, columnIndex, columnName );
		}

		for ( int row = 1; row < tableRows.size(); ++row )
		{
			final LinkedHashMap< String, Object > columnValueMap = new LinkedHashMap<>();

			StringTokenizer st = new StringTokenizer( tableRows.get( row ), delim );

			for ( String feature : columns )
			{
				final String string = st.nextToken();

				addColumn( columnValueMap, feature, string );
			}

			final DefaultImageSegment segment = SegmentUtils.segmentFromFeatures(
					coordinateColumnMap,
					columnValueMap );

			final TableRow tableRow = new DefaultTableRow( columnValueMap, row - 1  );

			segments.add( new DefaultAnnotatedImageSegment( segment, tableRow ) );
		}

		return segments;

	}

	public static void addColumn( HashMap< String, Object > columnValueMap, String column, String string )
	{
		try
		{
			final double number = Integer.parseInt( string );
			columnValueMap.put( column, number );
		}
		catch ( Exception e )
		{
			try
			{
				final double number = Double.parseDouble( string );
				columnValueMap.put( column, number );
			}
			catch ( Exception e2 )
			{
				columnValueMap.put( column, string );
			}
		}
	}

	public static void setColumnIndex( Map< ImageSegmentCoordinate, ValuePair< String, Integer > > coordinateColumnMap, int columnIndex, String columnName )
	{
		for ( ImageSegmentCoordinate coordinate : coordinateColumnMap.keySet() )
		{
			if ( coordinateColumnMap.get( coordinate ).getA().equals( columnName ) )
			{
				coordinateColumnMap.put(
						coordinate,
						new ValuePair< String, Integer>( columnName, columnIndex ) );
				break;
			}
		}
	}

	public static int getInteger( int columnIndex, String[] rowEntries  )
	{
		if ( columnIndex == -1 )
		{
			return 0;
		}
		else
		{
			return Integer.parseInt( rowEntries[ columnIndex ] );
		}
	}

	public static String getString( int columnIndex, String[] rowEntries )
	{
		if ( columnIndex == -1 )
		{
			return "";
		}
		else
		{
			return rowEntries[ columnIndex ];
		}
	}

	public static double getDouble( int columnIndex, String[] rowEntries )
	{
		return Double.parseDouble( rowEntries[ columnIndex ] );
	}

	public static JTable jTableFromSegmentList( ArrayList< ? extends TableRow > tableRows )
	{

		/**
		 * Init model and columns
		 */

		ColumnClassAwareTableModel model = new ColumnClassAwareTableModel();

		final Set< String > columns = tableRows.get( 0 ).cells().keySet();

		for ( String column : columns )
		{
			model.addColumn( column );
		}

		for ( int row = 0; row < tableRows.size(); ++row )
		{
			model.addRow( new Object[ columns.size() ] );

			final Collection< Object > values = tableRows.get( row ).cells().values();

			int col = 0;
			for ( Object value : values )
			{
				model.setValueAt( value, row, col++ );
			}
		}

		model.refreshColumnClassesFromObjectColumns();

		return new JTable( model );
	}


	public static ArrayList< String > getColumnNames( JTable jTable )
	{
		final ArrayList< String > columnNames = new ArrayList<>();

		for ( int columnIndex = 0; columnIndex < jTable.getColumnCount(); columnIndex++ )
		{
			columnNames.add( jTable.getColumnName( columnIndex ) );
		}
		return columnNames;
	}

	public static < A, B > TreeMap< A, B > columnsAsTreeMap(
			final JTable table,
			final String fromColumn,
			final String toColumn )
	{

		final int fromColumnIndex = table.getColumnModel().getColumnIndex( fromColumn );
		final int toColumnIndex = table.getColumnModel().getColumnIndex( toColumn );

		final TreeMap< A, B > treeMap = new TreeMap();

		final int rowCount = table.getRowCount();

		for ( int row = 0; row < rowCount; row++ )
		{
			treeMap.put( ( A ) table.getValueAt( row, fromColumnIndex ), ( B ) table.getValueAt( row, toColumnIndex ) );
		}

		return treeMap;
	}

	public static double columnMin( JTable jTable, int col )
	{
		double min = Double.MAX_VALUE;

		final int rowCount = jTable.getRowCount();

		for ( int row = 0; row < rowCount; row++ )
		{
			final Double valueAt = ( Double ) jTable.getValueAt( row, col );

			if ( valueAt < min )
			{
				min = valueAt;
			}
		}

		return min;
	}

	public static double columnMax( JTable jTable, int col )
	{
		double max =  - Double.MAX_VALUE;

		final int rowCount = jTable.getRowCount();

		for ( int row = 0; row < rowCount; row++ )
		{
			final Double valueAt = ( Double ) jTable.getValueAt( row, col );

			if ( valueAt > max )
			{
				max = valueAt;
			}
		}

		return max;
	}

	public static void addColumn( JTable table, String column, Object defaultValue )
	{
		addColumn( table.getModel(), column, defaultValue );
	}

	public static void addColumn( TableModel model, String column, Object defaultValue )
	{
		if ( model instanceof ColumnClassAwareTableModel )
		{
			((ColumnClassAwareTableModel ) model ).addColumnClass( defaultValue );
		}

		if ( model instanceof DefaultTableModel )
		{
			final Object[] rows = new Object[ model.getRowCount() ];
			Arrays.fill( rows, defaultValue );
			((DefaultTableModel) model ).addColumn( column, rows );
		}
	}

	public static void addRelativeImagePathColumn(
			JTable table,
			File tableFile,
			File imageFile,
			String imageName )
	{
		if ( imageFile == null ) return;
		final Path relativeImagePath = getRelativeImagePath( tableFile, imageFile );
		TableUtils.addColumn( table, "RelativeImagePath_" + imageName, relativeImagePath );
	}

	public static Path getRelativeImagePath( File tableFile, File imageFile )
	{
		final Path imagePath = Paths.get( imageFile.toString() );
		final Path tablePath = Paths.get( tableFile.toString() );

		return tablePath.relativize( imagePath );
	}


	public static double asDouble( Object featureValue )
	{
		double value;
		if ( featureValue instanceof Number )
		{
			value = (( Number ) featureValue).doubleValue();
		}
		else
		{
			value = Double.parseDouble( ( String ) featureValue );
		}
		return value;
	}

	public static double[] determineMinMaxValues( String column, JTable table )
	{
		final int columnIndex = table.getColumnModel().getColumnIndex( column );

		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;

		final int rowCount = table.getRowCount();
		for ( int row = 0; row < rowCount; row++ )
		{
			final double value = ( Double ) table.getValueAt( row, columnIndex );
			if ( value < min ) min = value;
			if ( value > max ) max = value;
		}

		return new double[]{ min, max };
	}
}
