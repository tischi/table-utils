package de.embl.cba.tables.command;

import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.bdv.utils.wrap.Wraps;
import de.embl.cba.tables.Calibrations;
import de.embl.cba.tables.FileUtils;
import de.embl.cba.tables.Logger;
import de.embl.cba.tables.Tables;
import de.embl.cba.tables.color.LazyCategoryColoringModel;
import de.embl.cba.tables.color.SelectionColoringModel;
import de.embl.cba.tables.image.DefaultImageSourcesModel;
import de.embl.cba.tables.image.Metadata;
import de.embl.cba.tables.imagesegment.ImageSegment;
import de.embl.cba.tables.imagesegment.LazyImageSegmentsModel;
import de.embl.cba.tables.select.DefaultSelectionModel;
import de.embl.cba.tables.select.SelectionModel;
import de.embl.cba.tables.table.ConcatenatedTableModel;
import ij.ImagePlus;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Plugin(type = Command.class, menuPath = "Plugins>Tables>Merge Tables" )
public class MergeTablesCommand< R extends RealType< R > > implements Command
{
	@Parameter ( label = "Input directory", style = "directory" )
	File directory;

	@Parameter ( label = "Output table file", style = "save" )
	File outputTable;

	@Parameter ( label = "Regular expression" )
	String regExp = ".*";

	@Override
	public void run()
	{
		final List< File > files = FileUtils.getFileList( directory, regExp, true );

		final ArrayList< TableModel > models = new ArrayList<>();
		for ( File file : files )
		{
			Logger.info( "Loading: " + file );
			models.add( Tables.loadTable( file ).getModel() );
		}

		final ConcatenatedTableModel concat = new ConcatenatedTableModel( models );

		Logger.info( "Saving: " + outputTable );
		Tables.saveTable( new JTable( concat ), outputTable );

		Logger.info( "Done!" );
	}


}
