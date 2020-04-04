package de.embl.cba.tables.annotate;

import bdv.util.BdvHandle;
import de.embl.cba.bdv.utils.BdvUtils;
import de.embl.cba.bdv.utils.behaviour.BdvBehaviours;
import de.embl.cba.tables.SwingUtils;
import de.embl.cba.tables.TableRows;
import de.embl.cba.tables.color.CategoryTableRowColumnColoringModel;
import de.embl.cba.tables.color.ColorUtils;
import de.embl.cba.tables.select.SelectionModel;
import de.embl.cba.tables.tablerow.TableRow;
import ij.gui.GenericDialog;
import net.imglib2.type.numeric.ARGBType;
import org.scijava.ui.behaviour.ClickBehaviour;
import org.scijava.ui.behaviour.util.Behaviours;
import org.scijava.ui.behaviour.io.InputTriggerConfig;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Annotator < T extends TableRow > extends JFrame
{
	private final String annotationColumnName;
	private final List< T > tableRows;
	private final JTable table;
	private final SelectionModel< T > selectionModel;
	private final CategoryTableRowColumnColoringModel< T > coloringModel;
	private final JPanel panel;
	private BdvHandle bdv = null;

	public Annotator(
			String annotationColumnName,
			List tableRows,
			JTable table,
			SelectionModel< T > selectionModel,
			CategoryTableRowColumnColoringModel< T > coloringModel )
	{
		super("");
		this.annotationColumnName = annotationColumnName;
		this.tableRows = tableRows;
		this.table = table;
		this.selectionModel = selectionModel;
		this.coloringModel = coloringModel;
		coloringModel.fixedColorMode( true );
		this.panel = new JPanel();
	}

	public BdvHandle getBdv() {
		return bdv;
	}

	public void setBdv(BdvHandle bdv) {
		this.bdv = bdv;
	}

	public void showDialog()
	{
		addAddNewAnnotationButton();
		addAnnotationButtons();
		showFrame();
	}

	private void showFrame()
	{
		this.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		panel.setOpaque( true ); //content panes must be opaque
		panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
		this.setContentPane( panel );
		this.setLocation( MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y );
		this.pack();
		this.setVisible( true );
	}

	private void addAddNewAnnotationButton()
	{
		final JButton button = new JButton( "Add New Category" );
		panel.add( button );
		button.addActionListener( e -> {
			final GenericDialog gd = new GenericDialog( "" );
			gd.addStringField( "New Category Name", "", 10 );
			gd.showDialog();
			if( gd.wasCanceled() ) return;
			addAnnotationButtonPanel( gd.getNextString(), null );
			refreshDialog();
		});

        // if we have a bdv handle, set the bdv shortcut for the new label
        if(this.bdv != null) {
		    Behaviours behaviours = new Behaviours( new InputTriggerConfig() );
		    behaviours.install( bdv.getTriggerbindings(), "behaviours" );
            // add key binding that assigns the currently selected label to the
            // to numerical category corresponding to input key (= 1, 2, 3, ..., 0 (equiv to None))
            // TODO unclear to me:
            // - what is the correct behaviour? 'addViewCaptureBehaviour' does not sound right
            // - how do we get the current category id from the lambda passed to 'addActionListener' abvoe?
            // - how do we get access to the current selection here
            // - maybe this needs to go t o'addAnnotationButtonPanel' instead ? 

		    // dummy
			BdvBehaviours.addPositionAndViewLoggingBehaviour( bdv, behaviours, "K" );
        }
	}

	private void addAnnotationButtons()
	{
		final HashMap< String, T > annotations = getAnnotations();
		for ( String annotation : annotations.keySet() )
			addAnnotationButtonPanel( annotation, annotations.get( annotation ) );
	}

	private void addAnnotationButtonPanel( String annotationName, T tableRow )
	{
		final JPanel panel = SwingUtils.horizontalLayoutPanel();

		final JButton button = new JButton( String.format("%1$15s", annotationName) );
		button.setFont( new Font("monospaced", Font.PLAIN, 12) );
		button.setOpaque( true );
		setButtonColor( button, tableRow );
		button.setAlignmentX( Component.CENTER_ALIGNMENT );

		final ARGBType argbType = new ARGBType();
		coloringModel.convert( annotationName, argbType );
		button.setBackground( ColorUtils.getColor( argbType ) );

		button.addActionListener( e -> {
			if ( selectionModel.isEmpty() ) return;

			TableRows.assignValues(
					annotationColumnName,
					selectionModel.getSelected(),
					annotationName,
					table );

			selectionModel.clearSelection();
		} );

		final JButton changeColor = new JButton( "Change Color" );
		changeColor.addActionListener( e -> {
			Color color = JColorChooser.showDialog( this.panel, "", null );
			if ( color == null ) return;
			button.setBackground( color );
			coloringModel.putInputToFixedColor( annotationName, ColorUtils.getARGBType( color ) );
		} );

		panel.add( button );
		panel.add( changeColor );
		this.panel.add( panel );
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
		this.pack();
	}
}
