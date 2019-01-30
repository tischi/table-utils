package de.embl.cba.tables.modelview.images;

import de.embl.cba.tables.FileUtils;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.cellprofiler.FolderAndFileColumn;
import org.fife.rsta.ac.js.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class PlatynereisImageSourcesModelCreator
{
	private final PlatynereisImageSourcesModel sourcesModel;

	public PlatynereisImageSourcesModelCreator( File directory ) throws IOException
	{
		ArrayList< File > imageFiles = getImageFiles( directory, ".*.xml" );

		sourcesModel = new PlatynereisImageSourcesModel();

		for ( File imageFile : imageFiles )
		{
			sourcesModel.addSource( imageFile );
		}

	}

	public PlatynereisImageSourcesModel getModel()
	{
		return sourcesModel;
	}

	public ArrayList< File > getImageFiles( File inputDirectory, String filePattern )
	{
		Logger.log( "Fetching image files..." );
		final ArrayList< File > fileList = FileUtils.getFileList( inputDirectory, filePattern );
		Logger.log( "Number of image files: " +  fileList.size() );
		Collections.sort( fileList, new SortFilesIgnoreCase());
		return fileList;
	}


	public class SortFilesIgnoreCase implements Comparator<File>
	{
		public int compare( File o1, File o2 )
		{
			String s1 = o1.getName();
			String s2 = o2.getName();
			return s1.toLowerCase().compareTo(s2.toLowerCase());
		}
	}
}
