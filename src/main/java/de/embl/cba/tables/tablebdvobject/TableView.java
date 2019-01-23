package de.embl.cba.tables.tablebdvobject;

import de.embl.cba.tables.Logger;
import de.embl.cba.tables.TableUIs;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.objects.ObjectCoordinate;
import de.embl.cba.tables.objects.ObjectCoordinateColumnsSelectionUI;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 *
 *
 * Notes:
 * - https://coderanch.com/t/345383/java/JTable-Paging
 */

public class TableView < T extends TableRow > extends JPanel implements SelectionListener< T >
{
	private final SelectionModel< T > selectionModel;
	private final SegmentationInstancesModel model;
	private final Listeners.List< SelectionListener > listeners;

	final private JTable table;

	final String name;
	public static final String NO_COLUMN_SELECTED = "No valueInTableColumn selected";

	private final TableModel tableModel;
	private JFrame frame;
    private JScrollPane scrollPane;
    private JMenuBar menuBar;
    private HashMap< ObjectCoordinate, String > objectCoordinateColumnMap;
	private ConcurrentHashMap< String, Integer > objectRowMap;
	private HashMap< String, double[] > columnsMinMaxMap;
	private int highlightedRow;

	public TableView( SegmentationInstancesModel model, SelectionModel< T > selectionModel )
	{
		super( new GridLayout(1, 0 ) );

		this.model = model;
		this.selectionModel = selectionModel;

		this.table = model.getTable();
		this.tableModel = model.getTable().getModel();
		this.name = "Table";

		listeners = new Listeners.SynchronizedList< SelectionListener >(  );

		prepareTableView();
		showTable();
	}

	private void prepareTableView()
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


	public synchronized void setCoordinateColumn( ObjectCoordinate objectCoordinate, String column )
	{
		if ( ! getColumnNames().contains( column ) )
		{
			Logger.error( column + " does not exist." );
			return;
		}

		objectCoordinateColumnMap.put( objectCoordinate, column );
	}

	public String getCoordinateColumn( ObjectCoordinate objectCoordinate )
	{
		return objectCoordinateColumnMap.get( objectCoordinate );
	}

    private void initCoordinateColumns()
    {
        this.objectCoordinateColumnMap = new HashMap<>( );

        for ( ObjectCoordinate objectCoordinate : ObjectCoordinate.values() )
        {
            objectCoordinateColumnMap.put( objectCoordinate, NO_COLUMN_SELECTED );
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
		final JMenuItem menuItem = new JMenuItem( "Add valueInTableColumn..." );

		final TableView objectTablePanel = this;
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

		final TableView objectTablePanel = this;

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

    public boolean hasCoordinate( ObjectCoordinate objectCoordinate )
    {
        if( objectCoordinateColumnMap.get( objectCoordinate ) == NO_COLUMN_SELECTED ) return false;
        return true;
    }

    public Double getObjectCoordinate( ObjectCoordinate objectCoordinate, int row )
    {
        if ( objectCoordinateColumnMap.get( objectCoordinate ) != NO_COLUMN_SELECTED )
        {
            final int columnIndex = table.getColumnModel().getColumnIndex( objectCoordinateColumnMap.get( objectCoordinate ) );
            return ( Double ) table.getValueAt( row, columnIndex );
        }
        else
        {
            return null;
        }
    }

	public void addColumn( String column, Object defaultValue )
	{
		TableUtils.addColumn( tableModel, column, defaultValue );
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
				table.getColumnModel().getColumnIndex( getCoordinateColumn( ObjectCoordinate.Label ) );

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
			key = getObjectKey( label, timePoint.intValue() );
		}

		return key;
	}

	private int getTimeColumnIndex()
	{
		int timeColumnIndex = -1;
		if ( hasCoordinate( ObjectCoordinate.T ) )
		{
			timeColumnIndex = table.getColumnModel().getColumnIndex(
					getCoordinateColumn( ObjectCoordinate.T ) );
		}
		return timeColumnIndex;
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

		final String objectKey = getObjectKey( label, timepoint );

		final Integer rowIndex = objectRowMap.get( objectKey );

		return rowIndex;
	}


	private String getObjectKey( Double label )
	{
		return getObjectKey( label, null );
	}

	private String getObjectKey( Double label, Integer time )
	{
		if ( time == null ) return label.toString();
		else return label.toString() + "_" + time.toString();
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
		if ( row != highlightedRow  )
		{
			final int rowInView = table.convertRowIndexToView( row );
			table.setRowSelectionInterval( rowInView, rowInView );
			table.scrollRectToVisible( table.getCellRect( rowInView, 0, true ) );

			selectionModel.setSelected( (T) model.getSegmentationInstance( row ) , true );

			highlightedRow = row;
		}
	}

	public int getHighlightedRow()
	{
		return highlightedRow;
	}

	public void highlightRowOfMostRecentSelection( SelectionModel< T > selection )
	{
		final Set< T > selected = selection.getSelected();
		final T lastSelection = ( T ) selected.toArray()[ selected.size() - 1 ];
		this.highlightRow( lastSelection.tableRowIndex() );
	}

	public Listeners< SelectionListener > listeners()
	{
		return listeners;
	}

	public void installRowSelectionListener()
	{
		table.addMouseListener(new MouseInputAdapter()
		{
			public void mousePressed( MouseEvent me )
			{
				if ( me.isControlDown() )
				{
					if ( hasCoordinate( ObjectCoordinate.Label ) )
					{
						final int selectedRow = table.getSelectedRow();

						final Double label = getObjectCoordinate( ObjectCoordinate.Label, selectedRow );

						Integer timePoint = getTimePoint( selectedRow );

						selectionModel.setSelected(
								( T ) model.getSegmentationInstance( label, timePoint  ),
								true );
					}
					else
					{
						Logger.error( "Please specify the Object Label Column!" );
					}
				}
			}
		} );
	}


	public Integer getTimePoint( int row )
	{
		Integer timepoint = 0;
		if ( hasCoordinate( ObjectCoordinate.Label.T ) )
		{
			final Double timepointDouble = (Double) getObjectCoordinate( ObjectCoordinate.Label.T, row );
			timepoint = timepointDouble.intValue();
		}
		return timepoint;
	}

	@Override
	public void selectionChanged()
	{
	}

	@Override
	public void selectionAdded( T selection )
	{
		highlightRow( selection.tableRowIndex() );
	}

	@Override
	public void selectionRemoved( T selection )
	{

	}
}
