package example;

import de.embl.cba.table.ui.TableUIs;

import java.io.IOException;

public class TestSelectTableFromGitUI
{
	public static void main( String[] args ) throws IOException
	{
		TableUIs.selectGitRepoTablePathUI(  "https://git.embl.de/tischer/platy-browser-tables/raw/master/data/0.6.0/tables/sbem-6dpf-1-whole-segmented-cells-labels" );
	}
}
