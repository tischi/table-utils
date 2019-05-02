package de.embl.cba.tables.annotate;

import de.embl.cba.tables.select.SelectionModel;
import de.embl.cba.tables.tablerow.TableRowImageSegment;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Annotator
{
	private final String annotationColumnName;
	private final List< TableRowImageSegment > tableRows;
	private final JTable table;
	private final SelectionModel< TableRowImageSegment > selectionModel;
	private final JPanel panel;

	public Annotator(
			String annotationColumnName,
			List< TableRowImageSegment > tableRows,
			JTable table,
			SelectionModel< TableRowImageSegment > selectionModel )
	{
		this.annotationColumnName = annotationColumnName;
		this.tableRows = tableRows;
		this.table = table;
		this.selectionModel = selectionModel;

		panel = new JPanel();
	}

	public void showAnnotationUI()
	{
		addAnnotationButtons();
		addNewAnnotationButton();
		showFrame();
	}

	private void showFrame()
	{
		final JFrame frame = new JFrame( "" );
		//Create and set up the window.
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

		//Create and set up the content pane.
		panel.setOpaque( true ); //content panes must be opaque
		panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );

		frame.setContentPane( panel );

		//Display the window.
		frame.pack();
		frame.setVisible( true );
	}


	private void addNewAnnotationButton()
	{
	}

	private void addAnnotationButtons()
	{
		final Set< String > annotations = getAnnotations();
		for ( String annotation : annotations )
			addAnnotationButton( annotation );
	}

	private void addAnnotationButton( String annotation )
	{
		final JButton button = new JButton( annotation );
		panel.add( button );
	}

	private Set< String > getAnnotations()
	{
		final HashSet< String > annotations = new HashSet<>();
		for ( int row = 0; row < tableRows.size(); row++ )
			annotations.add( tableRows.get( row ).getCell( annotationColumnName ) );

		return annotations;
	}


}
