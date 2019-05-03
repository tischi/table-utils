package de.embl.cba.tables;

import de.embl.cba.tables.view.TableRowsTableView;
import ij.gui.GenericDialog;

import javax.swing.*;
import java.io.File;


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

	public static void saveTableUI( JTable table )
	{
		final JFileChooser jFileChooser = new JFileChooser( "" );

		if ( jFileChooser.showSaveDialog( null ) == JFileChooser.APPROVE_OPTION )
		{
			final File selectedFile = jFileChooser.getSelectedFile();

			Tables.saveTable( table, selectedFile );
		}
	}


}
