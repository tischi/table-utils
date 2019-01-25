package de.embl.cba.tables.objects;

import de.embl.cba.tables.Logger;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.TableUIs;
import de.embl.cba.tables.modelview.datamodels.SegmentUtils;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 *
 *
 * Notes:
 * - https://coderanch.com/t/345383/java/JTable-Paging
 */

public class ObjectTablePanel extends JPanel
{
	final private JTable table;

	final String name;
	public static final String NO_COLUMN_SELECTED = "No getFeature selected";

	private final TableModel model;
	private JFrame frame;
    private JScrollPane scrollPane;
    private JMenuBar menuBar;
    private HashMap< SegmentCoordinate, String > objectCoordinateColumnMap;
	private ConcurrentHashMap< String, Integer > objectRowMap;
	private HashMap< String, double[] > columnsMinMaxMap;

	public ObjectTablePanel( JTable table )
	{
		super( new GridLayout(1, 0 ) );
		this.table = table;
		this.name = "Table";
		init();
		model = table.getModel();
	}

	public ObjectTablePanel( JTable table, String name )
    {
        super( new GridLayout(1, 0 ) );
        this.table = table;
		this.name = name;
		init();
		model = table.getModel();
	}

	private void init()
    {
        table.setPreferredScrollableViewportSize( new Dimension(500, 200) );
        table.setFillsViewportHeight( true );
        table.setAutoCreateRowSorter( true );
        table.setRowSelectionAllowed( true );

        scrollPane = new JScrollPane( table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add( scrollPane );
        table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

		columnsMinMaxMap = new HashMap<>();

        initCoordinateColumns();

        initMenuBar();
    }

	private void initMenuBar()
	{
		menuBar = new JMenuBar();

		menuBar.add( createTableMenuItem() );

		menuBar.add( createObjectCoordinateMenuItem() );
	}


	public synchronized void setCoordinateColumn( SegmentCoordinate segmentCoordinate, String column )
	{
		if ( ! getColumnNames().contains( column ) )
		{
			Logger.error( column + " does not exist." );
			return;
		}

		objectCoordinateColumnMap.put( segmentCoordinate, column );
	}

	public String getCoordinateColumn( SegmentCoordinate segmentCoordinate )
	{
		return objectCoordinateColumnMap.get( segmentCoordinate );
	}

    private void initCoordinateColumns()
    {
        this.objectCoordinateColumnMap = new HashMap<>( );

        for ( SegmentCoordinate segmentCoordinate : SegmentCoordinate.values() )
        {
            objectCoordinateColumnMap.put( segmentCoordinate, NO_COLUMN_SELECTED );
        }
    }


	public void addMenu( JMenuItem menuItem )
	{
		menuBar.add( menuItem );

		if ( frame != null ) SwingUtilities.updateComponentTreeUI( frame );
	}

	private JMenu createTableMenuItem()
    {
        JMenu menu = new JMenu( "Table" );

        menu.add( createSaveAsMenuItem() );

		menu.add( addColumnMenuItem() );

		return menu;
    }

	private JMenuItem createSaveAsMenuItem()
	{
		final JMenuItem menuItem = new JMenuItem( "Save as..." );
		menuItem.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				try
				{
					TableUIs.saveTableUI( table );
				}
				catch ( IOException e1 )
				{
					e1.printStackTrace();
				}
			}
		} );
		return menuItem;
	}

	private JMenuItem addColumnMenuItem()
	{
		final JMenuItem menuItem = new JMenuItem( "Add getFeature..." );

		final ObjectTablePanel objectTablePanel = this;
		menuItem.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				TableUIs.addColumnUI( objectTablePanel );
			}
		} );

		return menuItem;
	}


	private JMenu createObjectCoordinateMenuItem()
	{
		JMenu menu = new JMenu( "Objects" );

		final ObjectTablePanel objectTablePanel = this;

		final JMenuItem coordinatesMenuItem = new JMenuItem( "Select coordinates..." );
		coordinatesMenuItem.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				new ObjectCoordinateColumnsSelectionUI( objectTablePanel );
			}
		} );


		menu.add( coordinatesMenuItem );
		return menu;
	}

    public void showTable() {

        //Create and set up the window.
        frame = new JFrame( name );

        frame.setJMenuBar( menuBar );

        //Show the table
        //frame.add( scrollPane );

        //Create and set up the content pane.
        this.setOpaque(true); //content panes must be opaque
        frame.setContentPane(this);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public int getSelectedRowIndex()
    {
        return table.convertRowIndexToModel( table.getSelectedRow() );
    }

    public boolean hasCoordinate( SegmentCoordinate segmentCoordinate )
    {
        if( objectCoordinateColumnMap.get( segmentCoordinate ) == NO_COLUMN_SELECTED ) return false;
        return true;
    }

    public Double getObjectCoordinate( SegmentCoordinate segmentCoordinate, int row )
    {
        if ( objectCoordinateColumnMap.get( segmentCoordinate ) != NO_COLUMN_SELECTED )
        {
            final int columnIndex = table.getColumnModel().getColumnIndex( objectCoordinateColumnMap.get( segmentCoordinate ) );
            return ( Double ) table.getValueAt( row, columnIndex );
        }
        else
        {
            return null;
        }
    }

	public void addColumn( String column, Object defaultValue )
	{
		TableUtils.addFeature( model, column, defaultValue );
	}

	public ArrayList< String > getColumnNames()
	{
		return TableUtils.getColumnNames( table );
	}

	public JTable getTable()
	{
		return table;
	}

	private void createObjectRowMap()
	{
		objectRowMap = new ConcurrentHashMap();

		final int labelColumnIndex =
				table.getColumnModel().getColumnIndex( getCoordinateColumn( SegmentCoordinate.Label ) );

		int timeColumnIndex = getTimeColumnIndex();

		final int rowCount = table.getRowCount();
		for ( int row = 0; row < rowCount; row++ )
		{
			String key = createObjectKey( labelColumnIndex, timeColumnIndex, row );
			objectRowMap.put( key, row );
		}
	}

	private String createObjectKey( final int labelColumnIndex, final int timeColumnIndex, final int rowInView )
	{
		final int rowInModel = table.convertRowIndexToModel( rowInView );
		final Double label = ( Double ) table.getValueAt( rowInModel, labelColumnIndex );

		String key;
		if ( timeColumnIndex == -1 )
		{
			key = getObjectKey( label );
		}
		else
		{
			final Double timePoint = ( Double ) table.getValueAt( rowInModel, timeColumnIndex );
			key = SegmentUtils.getKey( label, timePoint.intValue() );
		}

		return key;
	}

	private int getTimeColumnIndex()
	{
		int timeColumnIndex = -1;
		if ( hasCoordinate( SegmentCoordinate.T ) )
		{
			timeColumnIndex = table.getColumnModel().getColumnIndex(
					getCoordinateColumn( SegmentCoordinate.T ) );
		}
		return timeColumnIndex;
	}

	public ConcurrentHashMap< Object, Object > getLabelHashMap( String column1 )
	{
		final ConcurrentHashMap map = new ConcurrentHashMap();

		final int labelColumnIndex0 = table.getColumnModel().getColumnIndex( getCoordinateColumn( SegmentCoordinate.Label ) );
		final int labelColumnIndex1 = table.getColumnModel().getColumnIndex( column1 );

		final int rowCount = table.getRowCount();

		for ( int row = 0; row < rowCount; row++ )
		{
			map.put( getValueAt( row, labelColumnIndex0 ), getValueAt( row, labelColumnIndex1 ));
		}

		return map;
	}

	private synchronized Object getValueAt( int row, int col )
	{
		return table.getValueAt( row, col );
	}

	public int getRowIndex( double label )
	{
		return getRowIndex( label, null );
	}

	public int getRowIndex( Double label, Integer timepoint )
	{
		if ( objectRowMap == null ) createObjectRowMap();

		final String objectKey = SegmentUtils.getKey( label, timepoint );

		final Integer rowIndex = objectRowMap.get( objectKey );

		return rowIndex;
	}


	private String getObjectKey( Double label )
	{
		return SegmentUtils.getKey( label, null );
	}

	public double[] getMinMaxValues( String selectedColumn )
	{
		if ( ! columnsMinMaxMap.containsKey( selectedColumn ) )
		{
			determineMinMaxValues( selectedColumn );
		}

		return columnsMinMaxMap.get( selectedColumn );
	}

	public void determineMinMaxValues( String selectedColumn )
	{
		final int columnIndex =
				table.getColumnModel().getColumnIndex( selectedColumn );

		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;

		final int rowCount = table.getRowCount();
		for ( int row = 0; row < rowCount; row++ )
		{
			final double value = ( Double ) table.getValueAt( row, columnIndex );
			if ( value < min ) min = value;
			if ( value > max ) max = value;
		}

		columnsMinMaxMap.put( selectedColumn, new double[]{ min, max } );
	}


	public void highlightRow( int row )
	{
		final int rowInView = table.convertRowIndexToView( row );
		table.setRowSelectionInterval( rowInView, rowInView );
		table.scrollRectToVisible( table.getCellRect( rowInView,0, true ) );
	}
}
