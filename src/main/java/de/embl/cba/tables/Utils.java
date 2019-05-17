package de.embl.cba.tables;

import ij.ImagePlus;

public class Utils
{
	public static void removeCalibration( ImagePlus labelImage )
	{
		labelImage.getCalibration().setUnit( "pixel" );
		labelImage.getCalibration().pixelHeight = 1;
		labelImage.getCalibration().pixelWidth = 1;
		labelImage.getCalibration().pixelDepth = 1;
	}
}
