package de.embl.cba.tables.objects.attributes;

import de.embl.cba.tables.SwingUtils;
import de.embl.cba.tables.modelview.segments.TableRow;
import de.embl.cba.tables.modelview.views.table.TableRowsTableView;
import org.fife.rsta.ac.js.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

public class AssignValuesToTableRowsUI< T extends TableRow > extends JPanel
{
	public static final String NEW_ATTRIBUTE = "None";
	final TableRowsTableView< T > tableView;
	Set< T > selectedRows;
	private JComboBox attributeComboBox;
	private JComboBox columnComboBox;
	private JFrame frame;

	private String selectedColumn;
	private String selectedAttribute;
	private Set< String > selectedAttributes;
	private Point location;


	// TODO: make this only work on TableRows (sources rid of TableView dependency)

	public AssignValuesToTableRowsUI( TableRowsTableView tableView )
	{
		this.tableView = tableView;
		selectedAttributes = new HashSet<>();

		this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

		final JPanel attributeSelectionUI = createAttributeSelectionUI();
		final JPanel columnSelectionUI = createColumnSelectionUI();
		final JButton okButton = createOkButton();

		this.add( columnSelectionUI );
		this.add( attributeSelectionUI );
		this.add( okButton );
	}

	public void showUI( Set< T > selectedRows )
	{
		this.selectedRows = selectedRows;
		updateColumnComboBox();
		showFrame();
	}

	private JButton createOkButton()
	{
		final JButton okButton = new JButton( "OK" );
		okButton.addActionListener( e -> {

			selectedColumn = ( String ) columnComboBox.getSelectedItem();
			selectedAttribute = ( String ) attributeComboBox.getSelectedItem();
			attributeComboBox.addItem( selectedAttribute );
			columnComboBox.setSelectedItem( selectedColumn );

			assignAttributes(
					selectedColumn,
					selectedRows,
					selectedAttribute
					);

			updateUIComponents();
			frame.dispose();
		} );

		return okButton;
	}

	public void updateUIComponents()
	{
		location = frame.getLocation();

		if ( ! selectedAttributes.contains( selectedAttribute ) )
		{
			selectedAttributes.add( selectedAttribute );
			attributeComboBox.removeItem( NEW_ATTRIBUTE );
			attributeComboBox.addItem( selectedAttribute );
		}

		columnComboBox.setSelectedItem( selectedColumn );
	}

	private void showFrame()
	{
		this.revalidate();
		this.repaint();
		frame = new JFrame();
		if ( location != null ) frame.setLocation( location );
		frame.add( this );
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frame.pack();
		frame.setVisible( true );
	}

	private JPanel createAttributeSelectionUI()
	{
		final JPanel horizontalLayoutPanel = SwingUtils.horizontalLayoutPanel();

		horizontalLayoutPanel.add( new JLabel( "Attribute: " ) );

		attributeComboBox = new JComboBox();
		attributeComboBox.setEditable( true );
		attributeComboBox.addItem( NEW_ATTRIBUTE );
		horizontalLayoutPanel.add( attributeComboBox );

		return horizontalLayoutPanel;
	}

	private JPanel createColumnSelectionUI()
	{
		final JPanel horizontalLayoutPanel = SwingUtils.horizontalLayoutPanel();

		horizontalLayoutPanel.add( new JLabel( "Column: " ) );

		columnComboBox = new JComboBox();

		horizontalLayoutPanel.add( columnComboBox );

		updateColumnComboBox();

		columnComboBox.addActionListener( e -> {
			// TODO: maybe change content of attributeComboBox
		} );

		return horizontalLayoutPanel;
	}

	private void updateColumnComboBox()
	{
		columnComboBox.removeAllItems();

		for ( String name : tableView.getColumnNames() )
		{
			columnComboBox.addItem( name );
		}
	}

	private void assignAttributes(
			final String column,
			final Set< T > rows,
			final String attribute )
	{
		for ( T row : rows )
		{
			assignAttribute( column, row, attribute );
		}
	}

	/**
	 * Write the values both in the TableRows and the actual table
	 *
	 * @param column
	 * @param row
	 * @param attribute
	 */
	private void assignAttribute( String column,
								  T row,
								  String attribute )
	{

		final int columnIndex = getColumnIndex( column );

		final Object valueToBeReplaced = tableView.getTable().getModel().getValueAt(
				row.rowIndex(),
				columnIndex
		);

		if ( valueToBeReplaced.getClass().equals( Double.class ) )
		{
			try
			{
				final double number = Double.parseDouble( attribute );
				tableView.getTable().getModel().setValueAt(
						number,
						row.rowIndex(),
						columnIndex );

				row.cells().put( column, number );
			}
			catch ( Exception e )
			{
				Logger.logError( "Entered value must be numeric for column: " + column );
			}
		}
		else
		{
			tableView.getTable().getModel().setValueAt(
					attribute,
					row.rowIndex(),
					columnIndex );

			row.cells().put( column, attribute );
		}
	}


	private int getColumnIndex( String column )
	{
		return tableView.getTable().getColumnModel().getColumnIndex( column );
	}
}
