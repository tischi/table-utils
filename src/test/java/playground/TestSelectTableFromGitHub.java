package playground;

import de.embl.cba.tables.TableUIs;

import java.io.IOException;

public class TestSelectTableFromGitHub
{
	public static void main( String[] args ) throws IOException
	{
//		TableUIs.selectGitRepoTablePathUI(  "https://raw.githubusercontent.com/platybrowser/platybrowser/mobie/data/1.0.1/tables/prospr-6dpf-1-whole-virtual-cells/" );

		TableUIs.selectGitHubTablePathUI(  "https://raw.githubusercontent.com/platybrowser/platybrowser/master/data/1.0.1/tables/prospr-6dpf-1-whole-virtual-cells/" );

	}
}
