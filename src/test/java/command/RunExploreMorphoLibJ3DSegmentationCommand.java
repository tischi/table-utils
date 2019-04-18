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

		IJ.open( RunExploreMorphoLibJ3DSegmentationCommand.class.getResource(
				"../3d-image.zip" ).getFile() );

		IJ.open( RunExploreMorphoLibJ3DSegmentationCommand.class.getResource(
				"../3d-image-lbl.zip" ).getFile() );

		IJ.open( RunExploreMorphoLibJ3DSegmentationCommand.class.getResource(
				"../3d-image-lbl-morpho.csv" ).getFile() );

		ij.command().run( ExploreMorphoLibJLabelImageCommand.class, true );
	}


}

