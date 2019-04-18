package command;

import de.embl.cba.tables.command.ExploreMorphoLibJLabelImageCommand;
import ij.IJ;
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

		ij.command().run( ExploreMorphoLibJLabelImageCommand.class, true );
	}

}

