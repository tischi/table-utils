package de.embl.cba.tables.view.dialogs;

import de.embl.cba.tables.view.SegmentsBdvView;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;


import static de.embl.cba.tables.SwingUtils.horizontalLayoutPanel;


public class BdvViewSourceSetSelectionDialog extends JPanel
{
	private final SegmentsBdvView bdvView;

	public BdvViewSourceSetSelectionDialog( SegmentsBdvView bdvView )
	{
		this.bdvView = bdvView;
		configPanel();
		addSourceSetSelectionPanel();
		showFrame();
	}


	private void configPanel()
	{
		setLayout( new BoxLayout(this, BoxLayout.Y_AXIS ) );
		setAlignmentX( Component.LEFT_ALIGNMENT );
	}

	private void showFrame()
	{
		final JFrame frame = new JFrame();
		frame.setContentPane( this );
		frame.setLocation( MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y );
		frame.pack();
		frame.setVisible( true );
	}


	private void addSourceSetSelectionPanel( )
	{
		final JPanel horizontalLayoutPanel = horizontalLayoutPanel();

		final JComboBox< String > comboBox = new JComboBox();

		final ArrayList< String > sourceSetIds = bdvView.getSourceSetIds();

		for ( String sourceSet : sourceSetIds )
			comboBox.addItem( new File( sourceSet ).getName() );

		final JButton button = new JButton( "Show image set" );

		button.addActionListener( e -> {
			bdvView.updateImageSet(
					sourceSetIds.get(
						comboBox.getSelectedIndex() ) );
		} );

		horizontalLayoutPanel.add( button );
		horizontalLayoutPanel.add( comboBox );
		this.add( horizontalLayoutPanel );
	}


}
