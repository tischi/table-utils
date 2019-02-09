package de.embl.cba.tables;

import de.embl.cba.tables.modelview.views.TableRowsTableView;
import ij.gui.GenericDialog;

import javax.swing.*;
import java.io.File;


public class TableUIs
{
	public static void addColumnUI( TableRowsTableView tableView )
	{
		final GenericDialog gd = new GenericDialog( "New Column" );
		gd.addStringField( "Column name", "Column", 30 );
		gd.addStringField( "Default value [Text or Numeric]", "None", 30 );

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

	public static void saveTableUI( JTable table )
	{
		final JFileChooser jFileChooser = new JFileChooser( "" );

		if ( jFileChooser.showSaveDialog( null ) == JFileChooser.APPROVE_OPTION )
		{
			final File selectedFile = jFileChooser.getSelectedFile();

			TableUtils.saveTable( table, selectedFile );
		}
	}


}
