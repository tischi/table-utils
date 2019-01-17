package de.embl.cba.tables.cellprofiler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import static de.embl.cba.tables.SwingUtils.horizontalLayoutPanel;

public class CellProfilerDatasetSelectionUI extends JPanel
{
	private final CellProfilerOutputExplorer explorer;
	private Map< Object, CellProfilerDataset > datasets;
	private JFrame frame;
	private static Point frameLocation;

	public CellProfilerDatasetSelectionUI( CellProfilerOutputExplorer explorer, Map< Object, CellProfilerDataset > datasets )
	{
		this.explorer = explorer;
		this.datasets = datasets;
		this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
		addDatasetSelectionUI();
		showUI();
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

	private void addDatasetSelectionUI( )
	{
		final JPanel horizontalLayoutPanel = horizontalLayoutPanel();

		horizontalLayoutPanel.add( new JLabel( "Dataset: " ) );

		final JComboBox jComboBox = new JComboBox();
		horizontalLayoutPanel.add( jComboBox );

		for ( Object datasetIndex : datasets.keySet() )
		{
			jComboBox.addItem( datasetIndex );
		}

		jComboBox.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				explorer.loadImages( datasets.get( jComboBox.getSelectedItem() ) );
			}
		} );

		this.add( horizontalLayoutPanel );
	}


	private void showUI()
	{
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
