package de.embl.cba.tables.annotate;

import com.jgoodies.common.collect.ArrayListModel;
import de.embl.cba.tables.TableRows;
import de.embl.cba.tables.color.ColoringListener;
import de.embl.cba.tables.color.ColoringModel;
import de.embl.cba.tables.ij3d.AnimatedViewAdjuster;
import de.embl.cba.tables.select.SelectionModel;
import de.embl.cba.tables.tablerow.TableRow;
import de.embl.cba.tables.tablerow.TableRowImageSegment;
import ij.gui.GenericDialog;
import net.imglib2.type.numeric.ARGBType;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Annotator < T extends TableRow >
{
	private final String annotationColumnName;
	private final List< T > tableRows;
	private final JTable table;
	private final SelectionModel< T > selectionModel;
	private final ColoringModel< T > coloringModel;
	private final JPanel panel;
	private JFrame frame;
	private ArrayList< ButtonAndTableRow< T > > buttons;

	public Annotator(
			String annotationColumnName,
			List tableRows,
			JTable table,
			SelectionModel selectionModel,
			ColoringModel< T > coloringModel )
	{
		this.annotationColumnName = annotationColumnName;
		this.tableRows = tableRows;
		this.table = table;
		this.selectionModel = selectionModel;
		this.coloringModel = coloringModel;

		buttons = new ArrayList<>(  );

		coloringModel.listeners().add( () -> {
			for ( ButtonAndTableRow< T > bt : buttons )
				setButtonColor( bt.button, bt.tableRow );
		} );

		panel = new JPanel();
	}

	public void showDialog()
	{
		addNewAnnotationButton();
		addAnnotationButtons();
		showFrame();
	}

	private void showFrame()
	{
		frame = new JFrame( "" );
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
		final JButton button = new JButton( "Add new annotation" );
		panel.add( button );
		button.addActionListener( e -> {

			final GenericDialog gd = new GenericDialog( "" );
			gd.addStringField( "New annotation", "", 10 );
			gd.showDialog();
			if( gd.wasCanceled() ) return;
			addAnnotationButton( gd.getNextString(), null );
			refreshDialog();
		});
	}

	private void addAnnotationButtons()
	{
		final HashMap< String, T > annotations = getAnnotations();
		for ( String annotation : annotations.keySet() )
			addAnnotationButton( annotation, annotations.get( annotation ) );
	}

	class ButtonAndTableRow < T >
	{
		public JButton button;
		public T tableRow;

		public ButtonAndTableRow( JButton button, T tableRow )
		{
			this.button = button;
			this.tableRow = tableRow;
		}
	}

	private void addAnnotationButton( String annotation, T tableRow )
	{
		final JButton button = new JButton( String.format("%1$15s", annotation) );
		button.setOpaque( true );
		setButtonColor( button, tableRow );
		button.setAlignmentX( Component.CENTER_ALIGNMENT );
		buttons.add( new ButtonAndTableRow<>( button, tableRow ) );
		panel.add( button );

		button.addActionListener( e -> {

			final Set< T > selected = selectionModel.getSelected();

			TableRows.assignValues(
					annotationColumnName,
					selected,
					annotation,
					table );

			selectionModel.clearSelection();

		} );
	}

	private void setButtonColor( JButton button, T tableRow )
	{
		if ( tableRow != null )
		{
			final ARGBType argbType = new ARGBType();
			coloringModel.convert( tableRow, argbType );
			button.setBackground( new Color( argbType.get() ) );
		}
	}


	private HashMap< String, T > getAnnotations()
	{
		final HashMap< String, T > annotationToTableRow = new HashMap<>();

		for ( int row = 0; row < tableRows.size(); row++ )
		{
			final T tableRow = tableRows.get( row );
			annotationToTableRow.put( tableRow.getCell( annotationColumnName ), tableRow );
		}

		return annotationToTableRow;
	}

	private void refreshDialog()
	{
		panel.revalidate();
		panel.repaint();
		frame.pack();
	}


}
