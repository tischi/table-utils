package de.embl.cba.tables.objects.attributes;

import de.embl.cba.tables.SwingUtils;
import de.embl.cba.tables.objects.ObjectTablePanel;
import org.fife.rsta.ac.js.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

public class AssignObjectAttributesUI extends JPanel
{
	public static final String NEW_ATTRIBUTE = "None";
	final ObjectTablePanel objectTablePanel;
	Set< Integer > selectedRows;
	private JComboBox attributeComboBox;
	private JComboBox columnComboBox;
	private JFrame frame;

	private String selectedColumn;
	private String selectedAttribute;
	private Set< String > selectedAttributes;
	private Point location;

	public AssignObjectAttributesUI( ObjectTablePanel objectTablePanel )
	{
		this.objectTablePanel = objectTablePanel;
		selectedAttributes = new HashSet<>();

		this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

		final JPanel attributeSelectionUI = createAttributeSelectionUI();
		final JPanel columnSelectionUI = createColumnSelectionUI();
		final JButton okButton = createOkButton();

		this.add( columnSelectionUI );
		this.add( attributeSelectionUI );
		this.add( okButton );
	}

	public void showUI( Set< Integer > selectedRows )
	{
		this.selectedRows = selectedRows;
		updateColumnComboBox();
		showFrame();
	}

	private JButton createOkButton()
	{
		final JButton okButton = new JButton( "OK" );
		okButton.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				selectedColumn = ( String ) columnComboBox.getSelectedItem();
				selectedAttribute = ( String ) attributeComboBox.getSelectedItem();

				assignAttributes(
						selectedColumn,
						selectedRows,
						selectedAttribute
						);

				updateUIComponents();
				frame.dispose();
			}
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

		// TODO: maybe add more items, depending on column

		return horizontalLayoutPanel;
	}

	private JPanel createColumnSelectionUI()
	{
		final JPanel horizontalLayoutPanel = SwingUtils.horizontalLayoutPanel();

		horizontalLayoutPanel.add( new JLabel( "Column: " ) );

		columnComboBox = new JComboBox();

		horizontalLayoutPanel.add( columnComboBox );

		updateColumnComboBox();

		columnComboBox.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				// TODO: maybe change content of attributeComboBox
			}
		} );

		return horizontalLayoutPanel;
	}

	private void updateColumnComboBox()
	{
		columnComboBox.removeAllItems();
		for ( String name : objectTablePanel.getColumnNames() )
		{
			columnComboBox.addItem( name );
		}

		if ( selectedColumn != null )
		{
			columnComboBox.setSelectedItem( selectedColumn );
		}
	}

	private void assignAttributes( final String column, final Set< Integer > rows, final String attribute )
	{
		for ( Integer row : rows )
		{
			assignAttribute( column, row, attribute );
		}
	}

	private void assignAttribute( String column, Integer row, String attribute )
	{
		final int columnIndex = getColumnIndex( column );

		final Object previousValue = objectTablePanel.getTable().getModel().getValueAt(
				row,
				columnIndex
		);

		if ( previousValue.getClass().equals( Double.class ) )
		{
			try
			{
				final double parseDouble = Double.parseDouble( attribute );
				objectTablePanel.getTable().getModel().setValueAt(
						parseDouble,
						row,
						columnIndex );
			} catch ( Exception e )
			{
				Logger.logError( "Entered value must be numeric for column: " + column );
			}
		}
		else
		{
			objectTablePanel.getTable().getModel().setValueAt(
					attribute,
					row,
					columnIndex );
		}
	}


	private int getColumnIndex( String column )
	{
		return objectTablePanel.getTable().getColumnModel().getColumnIndex( column );
	}
}
