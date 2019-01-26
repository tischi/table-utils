package de.embl.cba.tables.modelview.views;

import de.embl.cba.bdv.utils.lut.BlueWhiteRedARGBLut;
import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.tables.Logger;
import de.embl.cba.tables.TableUIs;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.coloring.ColoringModelDialogs;
import de.embl.cba.tables.modelview.coloring.ColumnColoringModel;
import de.embl.cba.tables.modelview.coloring.SelectionColoringModel;
import de.embl.cba.tables.modelview.datamodels.AnnotatedSegmentsModel;
import de.embl.cba.tables.modelview.objects.AnnotatedImageSegment;
import de.embl.cba.tables.modelview.objects.TableRow;
import de.embl.cba.tables.modelview.selection.SelectionListener;
import de.embl.cba.tables.objects.SegmentCoordinate;
import de.embl.cba.tables.objects.ObjectCoordinateColumnsSelectionUI;
import de.embl.cba.tables.modelview.selection.SelectionModel;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 *
 *
 * Notes:
 * - https://coderanch.com/t/345383/java/JTable-Paging
 */

public class SegmentsTableView extends JPanel
{
	public static final String NO_COLUMN_SELECTED = "No column selected";

	private final SelectionModel< AnnotatedImageSegment > selectionModel;
	private final AnnotatedSegmentsModel segmentsModel;
	private final SelectionColoringModel< AnnotatedImageSegment > coloringModel;

	private JFrame frame;
    private JScrollPane scrollPane;
    private JMenuBar menuBar;
    private Map< SegmentCoordinate, String > segmentCoordinateToColumnMap;
	private ConcurrentHashMap< String, Integer > objectRowMap;
	private Map< String, double[] > columnsMinMaxMap;
	private int highlightedRow;
	private JTable table;

	public SegmentsTableView(
			final AnnotatedSegmentsModel segmentsModel,
			final SelectionModel< AnnotatedImageSegment > selectionModel,
			final SelectionColoringModel< AnnotatedImageSegment > coloringModel )
	{
		super( new GridLayout(1, 0 ) );
		this.segmentsModel = segmentsModel;
		this.coloringModel = coloringModel;
		this.selectionModel = selectionModel;

		registerAsSelectionListener( selectionModel );

		segmentCoordinateToColumnMap = emptyObjectCoordinateColumnMap();

		segmentCoordinateToColumnMap.put(
				SegmentCoordinate.Label,
				segmentsModel.getLabelFeatureName() );

		createTable();
		createMenuBar();
		showTable();
		installRowSelectionListener();
	}

	public void registerAsSelectionListener( SelectionModel< ? extends TableRow > selectionModel )
	{
		selectionModel.listeners().add( new SelectionListener< TableRow >()
		{
			@Override
			public void selectionChanged()
			{
				table.getSelectionModel().clearSelection();

				final Set< ? extends TableRow > selected = selectionModel.getSelected();

				for ( TableRow tableRow : selected )
				{
					final int row = tableRow.rowIndex();
					final int rowInView = table.convertRowIndexToView( row );
					table.getSelectionModel().addSelectionInterval( rowInView, rowInView );
				}
			}

			@Override
			public void selectionEvent( TableRow selection, boolean selected )
			{
				if ( selected )
				{
					moveToRow( selection.rowIndex() );
				}
//				final int rowInView = table.convertRowIndexToView( selection.rowIndex() );
//				final boolean isAlreadySelected = table.getSelectionModel().isSelectedIndex( rowInView );
//				if ( ! isAlreadySelected )
//				{
//					selectRow( selection.rowIndex(), selected );
//				}
			}
		} );
	}

	private void createTable()
    {
		table = TableUtils.jTableFromSegmentList( segmentsModel.getAnnotatedSegments() );

		table.setPreferredScrollableViewportSize( new Dimension(500, 200) );
        table.setFillsViewportHeight( true );
        table.setAutoCreateRowSorter( true );
        table.setRowSelectionAllowed( true );

        scrollPane = new JScrollPane( table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add( scrollPane );
        table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

		columnsMinMaxMap = new HashMap<>();
    }

	private void createMenuBar()
	{
		menuBar = new JMenuBar();

		menuBar.add( createTableMenu() );

		menuBar.add( createObjectCoordinateMenu() );

		menuBar.add( createColoringMenu() );
	}


	public synchronized void setCoordinateColumn( SegmentCoordinate segmentCoordinate, String column )
	{
		if ( ! getColumnNames().contains( column ) )
		{
			Logger.error( column + " does not exist." );
			return;
		}

		segmentCoordinateToColumnMap.put( segmentCoordinate, column );
	}

	public String getCoordinateColumn( SegmentCoordinate segmentCoordinate )
	{
		return segmentCoordinateToColumnMap.get( segmentCoordinate );
	}

    private Map< SegmentCoordinate, String > emptyObjectCoordinateColumnMap()
    {
		Map< SegmentCoordinate, String > segmentCoordinateToColumnMap = new HashMap<>( );

        for ( SegmentCoordinate segmentCoordinate : SegmentCoordinate.values() )
        {
            segmentCoordinateToColumnMap.put( segmentCoordinate, NO_COLUMN_SELECTED );
        }

        return segmentCoordinateToColumnMap;
    }


	public void addMenu( JMenuItem menuItem )
	{
		menuBar.add( menuItem );

		if ( frame != null ) SwingUtilities.updateComponentTreeUI( frame );
	}

	private JMenu createTableMenu()
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

		final SegmentsTableView objectTablePanel = this;
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


	private JMenu createObjectCoordinateMenu()
	{
		JMenu menu = new JMenu( "Objects" );

		final SegmentsTableView objectTablePanel = this;

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
        frame = new JFrame( segmentsModel.getName() );

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
    	if ( ! segmentCoordinateToColumnMap.containsKey( segmentCoordinate ) )
			return false;

    	if( segmentCoordinateToColumnMap.get( segmentCoordinate ) == NO_COLUMN_SELECTED )
			return false;

        return true;
    }

    public Double getObjectCoordinate( SegmentCoordinate segmentCoordinate, int row )
    {
        if ( segmentCoordinateToColumnMap.get( segmentCoordinate ) != NO_COLUMN_SELECTED )
        {
            final int columnIndex = table.getColumnModel().getColumnIndex( segmentCoordinateToColumnMap.get( segmentCoordinate ) );
            return ( Double ) table.getValueAt( row, columnIndex );
        }
        else
        {
            return null;
        }
    }

	public void addColumn( String column, Object defaultValue )
	{
		// TODO: this must be propagated back to the segments...
		TableUtils.addFeature( table.getModel(), column, defaultValue );
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
			key = getObjectKey( label, timePoint.intValue() );
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

	public void selectRow( int row, boolean select )
	{
		final int rowInView = table.convertRowIndexToView( row );

		if ( select )
		{
			final boolean isAlreadySelected = table.getSelectionModel().isSelectedIndex( rowInView );

			if ( ! isAlreadySelected )
			{
				table.addRowSelectionInterval( rowInView, rowInView );
				table.scrollRectToVisible( table.getCellRect( rowInView, 0, true ) );
				selectionModel.setSelected( segmentsModel.getSegment( row ), true );
			}
		}
		else
		{
			// TODO: remove from selection
		}
	}

	public void moveToRow( int row )
	{
		final int rowInView = table.convertRowIndexToView( row );
		table.scrollRectToVisible( table.getCellRect( rowInView, 0, true ) );
	}

	public void installRowSelectionListener()
	{
		table.addMouseListener(new MouseInputAdapter()
		{
			public void mousePressed( MouseEvent me )
			{
				final int selectedRowInView = table.getSelectedRow();

				final boolean isAlreadySelected = table.getSelectionModel().isSelectedIndex( selectedRowInView );

				final int row = table.convertRowIndexToModel( selectedRowInView );

				selectionModel.setSelected(
						segmentsModel.getSegment( row ),
						true );
			}
		} );
	}


	private JMenu createColoringMenu()
	{
		JMenu coloringMenu = new JMenu( "Coloring" );

		coloringMenu.add( createRestoreOriginalColorMenuItem() );

		for ( int col = 0; col < table.getColumnCount(); col++ )
		{
			coloringMenu.add( createColorByColumnMenuItem( table.getColumnName( col ) ) );
		}

		return coloringMenu;
	}

	private JMenuItem createRestoreOriginalColorMenuItem()
	{
		final JMenuItem restoreOriginalColorMenuItem =
				new JMenuItem( "Restore original coloring");

		restoreOriginalColorMenuItem.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				//bdvSelectionEventHandler.getSelectableConverter().setWrappedConverter( originalConverter );
				//BdvUtils.repaint( bdvSelectionEventHandler.getBdv() );
			}
		} );
		return restoreOriginalColorMenuItem;
	}


	private JMenuItem createColorByColumnMenuItem( final String column )
	{
		final JMenuItem colorByColumnMenuItem = new JMenuItem( "Color by " + column );
		final int columnIndex = table.getColumnModel().getColumnIndex( column );

		colorByColumnMenuItem.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if ( Number.class.isAssignableFrom( table.getColumnClass( columnIndex ) ) )
				{
					final double[] minMaxValues = getMinMaxValues( column );

//					coloringModel.setLinearColoring(
//							column,
//							new BlueWhiteRedARGBLut( 256 ),
//							minMaxValues[ 0 ],
//							minMaxValues[ 1 ]
//					);
//
//					ColoringModelDialogs.showMinMaxDialog( column, coloringModel );
				}
				else
				{
//					coloringModel.setCategoricalColoring(
//							column,
//							new GlasbeyARGBLut() );
				}
			}

		} );

		return colorByColumnMenuItem;
	}


	public Integer getTimePoint( int row )
	{
		Integer timepoint = 0;
		if ( hasCoordinate( SegmentCoordinate.Label.T ) )
		{
			final Double timepointDouble = (Double) getObjectCoordinate( SegmentCoordinate.Label.T, row );
			timepoint = timepointDouble.intValue();
		}
		return timepoint;
	}

}
