package commands;

import de.embl.cba.tables.ui.ExploreMorphoLibJCommand;
import ij.IJ;
import ij.measure.ResultsTable;
import net.imagej.ImageJ;

public class RunExploreMorphoLibJ2DSegmentationCommand
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		IJ.open( RunExploreMorphoLibJ2DSegmentationCommand.class.getResource(
				"../blobs.zip" ).getFile() );

		IJ.open( RunExploreMorphoLibJ2DSegmentationCommand.class.getResource(
				"../mask-lbl.zip" ).getFile() );

		IJ.open( RunExploreMorphoLibJ2DSegmentationCommand.class.getResource(
				"../blobs-lbl-Morphometry.csv" ).getFile() );

		ij.command().run( ExploreMorphoLibJCommand.class, true );
	}

}

