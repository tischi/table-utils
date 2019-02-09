package de.embl.cba.tables.ui;

import de.embl.cba.tables.modelview.segments.ImageSegmentCoordinate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


import static de.embl.cba.tables.SwingUtils.horizontalLayoutPanel;


public class ImageSegmentCoordinateColumnsSelectionUI extends JPanel
{
	private static final String NO_COLUMN_SELECTED = "None";

	private ArrayList< String > columnChoices;
	private JFrame frame;
	private static Point frameLocation;

	public ImageSegmentCoordinateColumnsSelectionUI( ArrayList< String > columns )
	{
		setColumnChoices( columns );

		addColumnSelectionUIs();

		addOKButton();

		showUI();
	}

	private void addColumnSelectionUIs()
	{
		for ( ImageSegmentCoordinate coordinate : ImageSegmentCoordinate.values() )
		{
			addColumnSelectionUI( this, coordinate );
		}
	}

	private void addOKButton()
	{
		final JButton okButton = new JButton( "OK" );
		okButton.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				frameLocation = frame.getLocation();
				frame.dispose();
			}
		} );

		add( okButton );
	}

	private void setColumnChoices( ArrayList< String > columns )
	{
		columnChoices = new ArrayList<>( );
		columnChoices.add( NO_COLUMN_SELECTED );
		columnChoices.addAll( columns );
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


	private void showUI()
	{
		this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

		//Create and set up the window.
		frame = new JFrame("Object coordinates");

		//Create and set up the content pane.
		this.setOpaque(true); //content panes must be opaque
		frame.setContentPane(this);

		//Display the window.
		frame.pack();
		if ( frameLocation != null ) frame.setLocation( frameLocation );
		frame.setVisible( true );
	}



}
