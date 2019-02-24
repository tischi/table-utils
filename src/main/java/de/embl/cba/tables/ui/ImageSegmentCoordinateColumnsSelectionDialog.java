package de.embl.cba.tables.ui;

import de.embl.cba.tables.modelview.segments.ImageSegmentCoordinate;
import ij.Prefs;
import ij.gui.GenericDialog;

import javax.swing.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static de.embl.cba.tables.SwingUtils.horizontalLayoutPanel;


public class ImageSegmentCoordinateColumnsSelectionDialog
{
	private static final String NO_COLUMN_SELECTED = "None";
	public static final String IMAGE_SEGMENT_COORDINATE_COLUMN_PREFIX = "ImageSegmentCoordinateColumn.";

	private String[] columnChoices;
	private final GenericDialog gd;

	public ImageSegmentCoordinateColumnsSelectionDialog( Collection< String > columns )
	{
		setColumnChoices( columns );

		gd = new GenericDialog( "Coordinate Columns Selection" );

		addColumnSelectionUIs();
	}

	private void addColumnSelectionUIs()
	{
		for ( ImageSegmentCoordinate coordinate : ImageSegmentCoordinate.values() )
		{
			final String previousChoice =
					Prefs.get( getKey( coordinate ), columnChoices[ 0 ] );
			gd.addChoice( coordinate.toString(), columnChoices, previousChoice );
		}
	}

	private Map< ImageSegmentCoordinate, String > collectChoices()
	{
		final HashMap< ImageSegmentCoordinate, String > coordinateToColumnName = new HashMap<>();

		for ( ImageSegmentCoordinate coordinate : ImageSegmentCoordinate.values() )
		{
			final String columnName = gd.getNextChoice();
			coordinateToColumnName.put( coordinate, columnName );
			Prefs.set( getKey( coordinate ), columnName );
		}

		Prefs.savePreferences();

		return coordinateToColumnName;
	}

	private String getKey( ImageSegmentCoordinate coordinate )
	{
		return IMAGE_SEGMENT_COORDINATE_COLUMN_PREFIX + coordinate.toString();
	}

	private void setColumnChoices( Collection< String > columns )
	{
		final int numColumns = columns.size();

		columnChoices = new String[ numColumns + 1 ];

		columnChoices[ 0 ] = NO_COLUMN_SELECTED;

		int i = 1;
		for ( String column : columns )
		{
			columnChoices[ i++ ] = column;
		}
	}


	public Map< ImageSegmentCoordinate, String > fetchUserInput()
	{
		gd.showDialog();

		if ( gd.wasCanceled() ) return null;

		final Map< ImageSegmentCoordinate, String > coordinateToColumn = collectChoices();

		return coordinateToColumn;
	}


}
