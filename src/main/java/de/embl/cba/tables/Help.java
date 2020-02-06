package de.embl.cba.tables;

import bdv.tools.HelpDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class Help
{
	public static void showObjectSelectionHelp()
	{
		HelpDialog helpDialog = new HelpDialog( null, Help.class.getResource( "/SegmentationImageActionsHelp.html" ) );
		helpDialog.setVisible( true );

		AbstractAction help = new AbstractAction()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				helpDialog.setVisible( ! helpDialog.isVisible() );
			}
		};
	}
}
