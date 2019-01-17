package de.embl.cba.tables.cellprofiler;

import de.embl.cba.tables.objects.ObjectTablePanel;
import ij.IJ;
import ij.ImagePlus;

import javax.swing.*;
import java.util.HashMap;

public class CellProfilerOutputExplorer
{
	public CellProfilerOutputExplorer(
			JTable table,
			HashMap< Object, CellProfilerDataset > datasets )
	{
		final ObjectTablePanel tablePanel = new ObjectTablePanel( table );
		tablePanel.showTable();

		new CellProfilerDatasetSelectionUI( this, datasets );
	}

	public void loadImages( CellProfilerDataset cellProfilerDataset )
	{
		for ( String imagePath : cellProfilerDataset.getImagePaths() )
		{
			final ImagePlus imagePlus = IJ.openImage( imagePath );
			imagePlus.show();
		};
	}
}
