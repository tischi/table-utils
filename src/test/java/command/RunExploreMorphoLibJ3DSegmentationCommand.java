package command;

import de.embl.cba.tables.command.ExploreMorphoLibJLabelImageCommand;
import ij.IJ;
import net.imagej.ImageJ;

public class RunExploreMorphoLibJ3DSegmentationCommand
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		final ExploreMorphoLibJLabelImageCommand command = new ExploreMorphoLibJLabelImageCommand();

		command.intensityImage = IJ.openImage( RunExploreMorphoLibJ3DSegmentationCommand.class.getResource(
				"../test-data/3d-image.zip" ).getFile() );

		command.labelImage = IJ.openImage( RunExploreMorphoLibJ3DSegmentationCommand.class.getResource(
				"../test-data/3d-image-lbl.zip" ).getFile() );

		IJ.open( RunExploreMorphoLibJ3DSegmentationCommand.class.getResource(
				"../test-data/3d-image-lbl-morpho.csv" ).getFile() );

		command.resultsTableTitle = "3d-image-lbl-morpho";

		command.run();
	}


}

