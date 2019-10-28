package de.embl.cba.tables;

import de.embl.cba.tables.morpholibj.ExploreMorphoLibJLabelImage;
import de.embl.cba.tables.view.TableRowsTableView;
import ij.gui.GenericDialog;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TableUIs
{
	public static void addColumnUI( TableRowsTableView tableView )
	{
		final GenericDialog gd = new GenericDialog( "Add Custom Column" );
		gd.addStringField( "Column Name", "Column", 30 );
		gd.addStringField( "Default Value", "None", 30 );

		gd.showDialog();
		if( gd.wasCanceled() ) return;

		final String columnName = gd.getNextString();
		final String defaultValueString = gd.getNextString();

		Object defaultValue;
		try	{
			defaultValue = Double.parseDouble( defaultValueString );
		}
		catch ( Exception e )
		{
			defaultValue = defaultValueString;
		}

		tableView.addColumn( columnName, defaultValue );
	}

	public static String selectColumnNameUI( JTable table, String text )
	{
		final String[] columnNames = Tables.getColumnNamesAsArray( table );
		final GenericDialog gd = new GenericDialog( "" );
		gd.addChoice( text, columnNames, columnNames[ 0 ] );
		gd.showDialog();
		if ( gd.wasCanceled() ) return null;
		final String columnName = gd.getNextChoice();
		return columnName;
	}

	public static ArrayList< String > selectColumnNamesUI( JTable table, String text )
	{
		final String[] columnNames = Tables.getColumnNamesAsArray( table );
		final int n = (int) Math.ceil( Math.sqrt( columnNames.length ) );
		final GenericDialog gd = new GenericDialog( "" );
		boolean[] booleans = new boolean[ columnNames.length ];
		gd.addCheckboxGroup( n, n, columnNames, booleans );
		gd.showDialog();
		if ( gd.wasCanceled() ) return null;

		final ArrayList< String > selectedColumns = new ArrayList<>();
		for ( int i = 0; i < columnNames.length; i++ )
			if ( gd.getNextBoolean() )
				selectedColumns.add( columnNames[ i ] );

		return selectedColumns;
	}

	public static void saveTableUI( JTable table )
	{
		final JFileChooser jFileChooser = new JFileChooser( "" );

		if ( jFileChooser.showSaveDialog( null ) == JFileChooser.APPROVE_OPTION )
		{
			final File selectedFile = jFileChooser.getSelectedFile();

			Tables.saveTable( table, selectedFile );
		}
	}

	public static void saveColumnsUI( JTable table )
	{
		final ArrayList< String > selectedColumns
				= TableUIs.selectColumnNamesUI( table, "Select columns" );

		final JTable newTable = Tables.createNewTableFromSelectedColumns( table, selectedColumns );

		final JFileChooser jFileChooser = new JFileChooser( "" );

		if ( jFileChooser.showSaveDialog( null ) == JFileChooser.APPROVE_OPTION )
		{
			final File selectedFile = jFileChooser.getSelectedFile();

			Tables.saveTable( newTable, selectedFile );
		}
	}

	public static Map< String, List< String > > openTableUI( )
	{
		final JFileChooser jFileChooser = new JFileChooser( "" );

		if ( jFileChooser.showOpenDialog( null ) == JFileChooser.APPROVE_OPTION )
		{
			final File selectedFile = jFileChooser.getSelectedFile();

			return TableColumns.stringColumnsFromTableFile( selectedFile.toString() );
		}

		return null;
	}

	public static Map< String, List< String > > openTableForMergingUI( JTable table )
	{
		final String mergeByColumnName = selectColumnNameUI( table, "Merge by " );

		final ArrayList< Double > orderColumn = TableColumns.getNumericColumnAsDoubleList(
				table,
				mergeByColumnName );

		final JFileChooser jFileChooser = new JFileChooser( "" );

		if ( jFileChooser.showOpenDialog( null ) == JFileChooser.APPROVE_OPTION )
		{
			final File selectedFile = jFileChooser.getSelectedFile();

			Map< String, List< String > > columns =
					TableColumns.orderedStringColumnsFromTableFile(
							selectedFile.getAbsolutePath(),
							null,
							mergeByColumnName,
							orderColumn );

			return columns;
		}

		return null;
	}

}
