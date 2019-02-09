package de.embl.cba.tables.ui;

import de.embl.cba.tables.modelview.segments.ImageSegmentCoordinate;
import ij.gui.GenericDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static de.embl.cba.tables.SwingUtils.horizontalLayoutPanel;


public class ImageSegmentCoordinateColumnsSelectionDialog
{
	private static final String NO_COLUMN_SELECTED = "None";

	private String[] columnChoices;
	private final GenericDialog gd;

	public ImageSegmentCoordinateColumnsSelectionDialog( ArrayList< String > columns )
	{
		setColumnChoices( columns );

		gd = new GenericDialog( "Coordinate Columns Selection" );

		addColumnSelectionUIs();
	}

	private void addColumnSelectionUIs()
	{
		for ( ImageSegmentCoordinate coordinate : ImageSegmentCoordinate.values() )
		{
			gd.addChoice( coordinate.name(), columnChoices, columnChoices[ 0 ] );
		}
	}

	private Map< ImageSegmentCoordinate, String > collectChoices()
	{
		final HashMap< ImageSegmentCoordinate, String > coordinateToColumn = new HashMap<>();

		for ( ImageSegmentCoordinate coordinate : ImageSegmentCoordinate.values() )
		{
			coordinateToColumn.put( coordinate, gd.getNextString() );
		}

		return coordinateToColumn;
	}

	private void setColumnChoices( ArrayList< String > columns )
	{
		final int numColumns = columns.size();

		columnChoices = new String[ numColumns + 1 ];

		columnChoices[ 0 ] = NO_COLUMN_SELECTED;

		for ( int i = 0; i < numColumns; i++ )
		{
			columnChoices[ i + 1 ] = columns.get( i );
		}
	}

	private void addColumnSelectionUI( final JPanel panel, final ImageSegmentCoordinate coordinate )
	{
		final JPanel horizontalLayoutPanel = horizontalLayoutPanel();

		horizontalLayoutPanel.add( new JLabel( coordinate.toString() ) );

		final JComboBox jComboBox = new JComboBox();
		horizontalLayoutPanel.add( jComboBox );

		for ( String choice : columnChoices )
		{
			jComboBox.addItem( choice );
		}

		// +1 is due to the option to select no Column
		jComboBox.setSelectedItem( objectTablePanel.getCoordinateColumn( coordinate ) );

		jComboBox.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				objectTablePanel.setCoordinateColumn( coordinate, ( String ) jComboBox.getSelectedItem() );
			}
		} );

		panel.add( horizontalLayoutPanel );
	}

	public Map< ImageSegmentCoordinate, String > fetchUserInput()
	{
		gd.showDialog();

		if ( gd.wasCanceled() ) return null;

		final Map< ImageSegmentCoordinate, String > coordinateToColumn = collectChoices();

		return coordinateToColumn;

	}


}
