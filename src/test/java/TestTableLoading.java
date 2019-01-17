import de.embl.cba.tables.objects.ObjectTablePanel;
import de.embl.cba.tables.TableUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class TestTableLoading
{
	public static void main( String[] args ) throws IOException
	{
		final File file = new File( TestTableLoading.class.getResource( "tab-delim-numeric-and-string-table.csv" ).getFile() );

		final JTable jTable = TableUtils.loadTable( file, "\t" );

		new ObjectTablePanel( jTable ).showTable();
	}
}
