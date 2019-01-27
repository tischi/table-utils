package de.embl.cba.tables.modelview.views.table;

import de.embl.cba.bdv.utils.lut.BlueWhiteRedARGBLut;
import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.tables.TableUIs;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.coloring.*;
import de.embl.cba.tables.modelview.datamodels.TableRowsModel;
import de.embl.cba.tables.modelview.objects.TableRow;
import de.embl.cba.tables.modelview.selection.SelectionListener;
import de.embl.cba.tables.modelview.selection.SelectionModel;
import de.embl.cba.tables.objects.attributes.AssignValuesToTableRowsUI;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TableRowsTableView < T extends TableRow > extends JPanel
{
//	public static final String NO_COLUMN_SELECTED = "No column selected";

	private final SelectionModel< T > selectionModel;
	private final ArrayList< String > categoricalColumns;
	private final TableRowsModel< T > tableRowsModel;
	private final SelectionColoringModel< T > selectionColoringModel;

	private JFrame frame;
    private JScrollPane scrollPane;
    private JMenuBar menuBar;
//    private Map< ImageSegmentCoordinate, String > segmentCoordinateToColumnMap;
//	private ConcurrentHashMap< String, Integer > objectRowMap;
	private Map< String, double[] > columnsMinMaxMap;
	private JTable table;
	private int categoricalLabelColoringRandomSeed;
	private ArrayList< Integer > selectedRowsInView;
	private int recentlySelectedRowInView;

	public TableRowsTableView(
			final TableRowsModel< T > tableRowsModel,
			final SelectionModel< T > selectionModel,
			final SelectionColoringModel< T > selectionColoringModel,
			ArrayList< String > categoricalColumns )
	{
		super( new GridLayout(1, 0 ) );
		this.tableRowsModel = tableRowsModel;
		this.selectionColoringModel = selectionColoringModel;
		this.selectionModel = selectionModel;
		this.categoricalColumns = categoricalColumns;

		selectedRowsInView = new ArrayList<>(  );

		registerAsSelectionListener( selectionModel );

//		registerAsColoringListener( selectionColoringModel );

//		segmentCoordinateToColumnMap = emptyObjectCoordinateColumnMap();
//
//		segmentCoordinateToColumnMap.put(
//				ImageSegmentCoordinate.Label,
//				tableRowsModel.getLabelFeatureName() );

		categoricalLabelColoringRandomSeed = 50;

		createTable();
		createMenuBar();
		showTable();
		installRowSelectionListener();
		configureTableRowColoring( tableRowsModel, selectionModel );
	}


	public void configureTableRowColoring( TableRowsModel< T > tableRowsModel, SelectionModel< T > selectionModel )
	{
		table.setDefaultRenderer( Double.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

				Component c = super.getTableCellRendererComponent(
						table,
						value,
						isSelected,
						hasFocus,
						row,
						column);

				c.setBackground( this.getRowColour(row, column) );

				return c;
			}

			private Color getRowColour( int rowInView, int column )
			{
				if ( column == 0 )
				{
					int a = 1;
				}

				if ( column == 1 )
				{
					int a = 1;
				}

				final int row = table.convertRowIndexToModel( rowInView );
				if ( selectionModel.isSelected( tableRowsModel.getTableRows().get( row ) ) )
				{
					return Color.YELLOW;
				}
				else
				{
					return Color.WHITE;
				}
			}
		} );
		table.setDefaultRenderer( String.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

				Component c = super.getTableCellRendererComponent(
						table,
						value,
						isSelected,
						hasFocus,
						row,
						column);

				c.setBackground( this.getRowColour(row, column) );

				return c;
			}

			private Color getRowColour( int rowInView, int column )
			{
				if ( column == 0 )
				{
					int a = 1;
				}

				if ( column == 1 )
				{
					int a = 1;
				}

				final int row = table.convertRowIndexToModel( rowInView );
				if ( selectionModel.isSelected( tableRowsModel.getTableRows().get( row ) ) )
				{
					return Color.YELLOW;
				}
				else
				{
					return Color.WHITE;
				}
			}
		} );
		table.setDefaultRenderer( Integer.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

				Component c = super.getTableCellRendererComponent(
						table,
						value,
						isSelected,
						hasFocus,
						row,
						column);

				c.setBackground( this.getRowColour(row, column) );

				return c;
			}

			private Color getRowColour( int rowInView, int column )
			{
				if ( column == 0 )
				{
					int a = 1;
				}

				if ( column == 1 )
				{
					int a = 1;
				}

				final int row = table.convertRowIndexToModel( rowInView );
				if ( selectionModel.isSelected( tableRowsModel.getTableRows().get( row ) ) )
				{
					return Color.YELLOW;
				}
				else
				{
					return Color.WHITE;
				}
			}
		} );
		table.setDefaultRenderer( Object.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

				Component c = super.getTableCellRendererComponent(
						table,
						value,
						isSelected,
						hasFocus,
						row,
						column);

				c.setBackground( this.getRowColour(row, column) );

				return c;
			}

			private Color getRowColour( int rowInView, int column )
			{
				if ( column == 0 )
				{
					int a = 1;
				}

				if ( column == 1 )
				{
					int a = 1;
				}

				final int row = table.convertRowIndexToModel( rowInView );
				if ( selectionModel.isSelected( tableRowsModel.getTableRows().get( row ) ) )
				{
					return Color.YELLOW;
				}
				else
				{
					return Color.WHITE;
				}
			}
		} );
	}

	public void registerAsColoringListener( SelectionColoringModel< T > selectionColoringModel )
	{
		selectionColoringModel.listeners().add( new ColoringListener()
		{
			@Override
			public void coloringChanged()
			{
				// ...
			}
		} );
	}



	private void createTable()
    {
		table = TableUtils.jTableFromSegmentList( tableRowsModel.getTableRows() );

		table.setPreferredScrollableViewportSize( new Dimension(500, 200) );
        table.setFillsViewportHeight( true );
        table.setAutoCreateRowSorter( true );
        table.setRowSelectionAllowed( true );
		table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

		scrollPane = new JScrollPane( table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add( scrollPane );
        table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

		columnsMinMaxMap = new HashMap<>();
    }

	private void createMenuBar()
	{
		menuBar = new JMenuBar();

		menuBar.add( createTableMenu() );

		// menuBar.add( createObjectCoordinateMenu() );

		menuBar.add( createColoringMenu() );
	}


//	public synchronized void setCoordinateColumn( ImageSegmentCoordinate imageSegmentCoordinate, String column )
//	{
//		if ( ! getColumnNames().contains( column ) )
//		{
//			Logger.error( column + " does not exist." );
//			return;
//		}
//
//		segmentCoordinateToColumnMap.put( imageSegmentCoordinate, column );
//	}

//	public String getCoordinateColumn( ImageSegmentCoordinate imageSegmentCoordinate )
//	{
//		return segmentCoordinateToColumnMap.get( imageSegmentCoordinate );
//	}
//
//    private Map< ImageSegmentCoordinate, String > emptyObjectCoordinateColumnMap()
//    {
//		Map< ImageSegmentCoordinate, String > segmentCoordinateToColumnMap = new HashMap<>( );
//
//        for ( ImageSegmentCoordinate imageSegmentCoordinate : ImageSegmentCoordinate.values() )
//        {
//            segmentCoordinateToColumnMap.put( imageSegmentCoordinate, NO_COLUMN_SELECTED );
//        }
//
//        return segmentCoordinateToColumnMap;
//    }


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

		menu.add( valueAssignmentMenuItem() );

		return menu;
    }

	private JMenuItem valueAssignmentMenuItem()
	{
		final JMenuItem menuItem = new JMenuItem( "Assign Values..." );
		final TableRowsTableView tableRowsTableView = this;
		menuItem.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				final AssignValuesToTableRowsUI assignObjectAttributesUI =
						new AssignValuesToTableRowsUI( tableRowsTableView );
				assignObjectAttributesUI.showUI( selectionModel.getSelected() );
			}
		} );
		return menuItem;
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
		final JMenuItem menuItem = new JMenuItem( "Add Column..." );

		final TableRowsTableView tableView = this;

		menuItem.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				TableUIs.addColumnUI( tableView );
			}
		} );

		return menuItem;
	}

//	private JMenu createObjectCoordinateMenu()
//	{
//		JMenu menu = new JMenu( "Objects" );
//
//		final TableRowsTableView objectTablePanel = this;
//
//		final JMenuItem coordinatesMenuItem = new JMenuItem( "Select coordinates..." );
//		coordinatesMenuItem.addActionListener( new ActionListener()
//		{
//			@Override
//			public void actionPerformed( ActionEvent e )
//			{
//				new ObjectCoordinateColumnsSelectionUI( objectTablePanel );
//			}
//		} );
//
//
//		menu.add( coordinatesMenuItem );
//		return menu;
//	}

    public void showTable() {

        //Create and set up the window.
        frame = new JFrame( "Table" );

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

//    public boolean hasCoordinate( ImageSegmentCoordinate imageSegmentCoordinate )
//    {
//    	if ( ! segmentCoordinateToColumnMap.containsKey( imageSegmentCoordinate ) )
//			return false;
//
//    	if( segmentCoordinateToColumnMap.get( imageSegmentCoordinate ) == NO_COLUMN_SELECTED )
//			return false;
//
//        return true;
//    }
//
//    public Double getObjectCoordinate( ImageSegmentCoordinate imageSegmentCoordinate, int row )
//    {
//        if ( segmentCoordinateToColumnMap.get( imageSegmentCoordinate ) != NO_COLUMN_SELECTED )
//        {
//            final int columnIndex = table.getColumnModel().getColumnIndex( segmentCoordinateToColumnMap.get( imageSegmentCoordinate ) );
//            return ( Double ) table.getValueAt( row, columnIndex );
//        }
//        else
//        {
//            return null;
//        }
//    }

	public void addColumn( String column, Object defaultValue )
	{
		TableUtils.addColumn( table.getModel(), column, defaultValue );
	}

	public ArrayList< String > getColumnNames()
	{
		return TableUtils.getColumnNames( table );
	}

	public JTable getTable()
	{
		return table;
	}

//	private void createObjectRowMap()
//	{
//		objectRowMap = new ConcurrentHashMap();
//
//		final int labelColumnIndex =
//				table.getColumnModel().getColumnIndex( getCoordinateColumn( ImageSegmentCoordinate.Label ) );
//
//		int timeColumnIndex = getTimeColumnIndex();
//
//		final int rowCount = table.getRowCount();
//		for ( int row = 0; row < rowCount; row++ )
//		{
//			String key = createObjectKey( labelColumnIndex, timeColumnIndex, row );
//			objectRowMap.put( key, row );
//		}
//	}

//	private String createObjectKey(
//			final int labelColumnIndex,
//			final int timeColumnIndex,
//			final int rowInView )
//	{
//		final int rowInModel = table.convertRowIndexToModel( rowInView );
//		final Double label = ( Double ) table.getValueAt( rowInModel, labelColumnIndex );
//
//		String key;
//		if ( timeColumnIndex == -1 )
//		{
//			key = getObjectKey( label );
//		}
//		else
//		{
//			final Double timePoint = ( Double ) table.getValueAt( rowInModel, timeColumnIndex );
//			key = getObjectKey( label, timePoint.intValue() );
//		}
//
//		return key;
//	}

//	private int getTimeColumnIndex()
//	{
//		int timeColumnIndex = -1;
//		if ( hasCoordinate( ImageSegmentCoordinate.T ) )
//		{
//			timeColumnIndex = table.getColumnModel().getColumnIndex(
//					getCoordinateColumn( ImageSegmentCoordinate.T ) );
//		}
//		return timeColumnIndex;
//	}

//	public int getRowIndex( Double label, Integer timePoint )
//	{
//		if ( objectRowMap == null ) createObjectRowMap();
//
//		final String objectKey = getObjectKey( label, timePoint );
//
//		final Integer rowIndex = objectRowMap.get( objectKey );
//
//		return rowIndex;
//	}

//	private String getObjectKey( Double label )
//	{
//		return getObjectKey( label, null );
//	}

//	private String getObjectKey( Double label, Integer time )
//	{
//		if ( time == null ) return label.toString();
//		else return label.toString() + "_" + time.toString();
//	}

	public double[] getMinMaxValues( String column )
	{
		if ( ! columnsMinMaxMap.containsKey( column ) )
		{
			final double[] minMaxValues = TableUtils.determineMinMaxValues( column, table );
			columnsMinMaxMap.put( column, minMaxValues );
		}

		return columnsMinMaxMap.get( column );
	}

	public void moveToRowInView( int rowInView )
	{
		table.getSelectionModel().setSelectionInterval( rowInView, rowInView );
		final Rectangle visibleRect = table.getVisibleRect();
		final Rectangle cellRect = table.getCellRect( rowInView, 0, true );
		visibleRect.y = cellRect.y;
		table.scrollRectToVisible( visibleRect );
		table.repaint();
	}

	public void installRowSelectionListener()
	{
		table.getSelectionModel().addListSelectionListener( new ListSelectionListener()
		{
			@Override
			public void valueChanged( ListSelectionEvent e )
			{
				if ( e.getValueIsAdjusting() ) return;

				recentlySelectedRowInView = table.getSelectedRow();

				final int row = table.convertRowIndexToModel( recentlySelectedRowInView );

				// TODO: currently one can only select single rows (not even unselect,
				// because it was to complex with all the listeners)
				selectionModel.setSelected( tableRowsModel.getTableRows().get( row ), true );
			}
		} );
	}

	public void registerAsSelectionListener( SelectionModel< ? extends TableRow > selectionModel )
	{
		selectionModel.listeners().add( new SelectionListener< TableRow >()
		{
			@Override
			public synchronized void selectionChanged()
			{
//				table.getSelectionModel().setValueIsAdjusting( true );

//				table.getSelectionModel().clearSelection();

//				final Set< ? extends TableRow > selected = selectionModel.getSelected();
//
//				for ( TableRow tableRow : selected )
//				{
//					final int row = tableRow.rowIndex();
//					final int rowInView = table.convertRowIndexToView( row );
////				    if ( ! selectedRowsInView.contains( rowInView ) )
////					{
////					table.getSelectionModel().addSelectionInterval( rowInView, rowInView );
////					}
//				}
//
////				table.getSelectionModel().setValueIsAdjusting( false );
//
//				int a = 1;
				table.repaint();
			}

			@Override
			public void selectionEvent( TableRow selection, boolean selected )
			{
				if ( selected )
				{
					final int rowInView = table.convertRowIndexToView( selection.rowIndex() );

					if ( rowInView == recentlySelectedRowInView ) return;

					moveToRowInView( rowInView );
				}
				else
				{
					// TODO: change color of selected rows
				}
			}
		} );
	}

	private JMenu createColoringMenu()
	{
		JMenu coloringMenu = new JMenu( "Coloring" );

		// coloringMenu.add( createRestoreOriginalColorMenuItem() );

		for ( int col = 0; col < table.getColumnCount(); col++ )
		{
			coloringMenu.add( createColorByColumnMenuItem( table.getColumnName( col ) ) );
		}

		return coloringMenu;
	}

//	private JMenuItem createRestoreOriginalColorMenuItem()
//	{
//		final JMenuItem restoreOriginalColorMenuItem =
//				new JMenuItem( "Restore original coloring");
//
//		restoreOriginalColorMenuItem.addActionListener( new ActionListener()
//		{
//			@Override
//			public void actionPerformed( ActionEvent e )
//			{
//				//bdvSelectionEventHandler.getSelectableConverter().setWrappedConverter( originalConverter );
//				//BdvUtils.repaint( bdvSelectionEventHandler.getBdv() );
//			}
//		} );
//		return restoreOriginalColorMenuItem;
//	}


	private JMenuItem createColorByColumnMenuItem( final String column )
	{
		// TODO: also table cells could be colored

		final JMenuItem colorByColumnMenuItem = new JMenuItem( "Color " + column );

		colorByColumnMenuItem.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				colorBy( column );
			}

		} );

		return colorByColumnMenuItem;
	}

	public void colorBy( String column )
	{
		// TODO: clean this up this suffers from the fact that I do not know
		// the column classes....(and even if, some are numeric but should be treated
		// categorical

		final int columnIndex = table.getColumnModel().getColumnIndex( column );

		if ( ! categoricalColumns.contains( column )
				&&
				Number.class.isAssignableFrom( table.getColumnClass( columnIndex ) ) )
		{
			final double[] minMaxValues = getMinMaxValues( column );

			final NumericTableRowColumnColoringModel< T > coloringModel
					= new NumericTableRowColumnColoringModel< T >(
							column,
							new BlueWhiteRedARGBLut( 1000 ),
							minMaxValues[ 0 ],
							minMaxValues[ 1 ]
			);

			selectionColoringModel.setWrappedColoringModel( coloringModel );

			final NumericColoringModelDialog dialog =
					new NumericColoringModelDialog(
							column,
							coloringModel );

			// TODO: whether and how to automatically close this dialog?
		}
		else
		{
			final DynamicCategoryColoringModel< T > coloringModel
					= new DynamicCategoryColoringModel< T >(
					new GlasbeyARGBLut(),
					categoricalLabelColoringRandomSeed++
			);

			selectionColoringModel.setWrappedColoringModel( coloringModel );
		}
	}


}
