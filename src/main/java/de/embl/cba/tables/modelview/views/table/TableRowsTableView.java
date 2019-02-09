package de.embl.cba.tables.modelview.views.table;

import de.embl.cba.bdv.utils.lut.BlueWhiteRedARGBLut;
import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.tables.TableUIs;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.coloring.*;
import de.embl.cba.tables.modelview.combined.TableRowsModel;
import de.embl.cba.tables.modelview.segments.TableRow;
import de.embl.cba.tables.modelview.selection.SelectionListener;
import de.embl.cba.tables.modelview.selection.SelectionModel;
import de.embl.cba.tables.objects.attributes.AssignValuesToTableRowsUI;
import net.imglib2.type.numeric.ARGBType;

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
import java.util.List;
import java.util.Map;


public class TableRowsTableView < T extends TableRow > extends JPanel
{
//	public static final String NO_COLUMN_SELECTED = "No column selected";

	private final SelectionModel< T > selectionModel;
	private final TableRowsModel< T > tableRowsModel;
	private final SelectionColoringModel< T > selectionColoringModel;

	private JFrame frame;
    private JScrollPane scrollPane;
    private JMenuBar menuBar;
	private Map< String, double[] > columnsMinMaxMap;
	private JTable table;
	private int categoricalLabelColoringRandomSeed;
	private int recentlySelectedRowInView;
	private AssignValuesToTableRowsUI assignObjectAttributesUI;

	public TableRowsTableView(
			final TableRowsModel< T > tableRowsModel,
			final SelectionModel< T > selectionModel,
			final SelectionColoringModel< T > selectionColoringModel )
	{
		super( new GridLayout(1, 0 ) );
		this.tableRowsModel = tableRowsModel;
		this.selectionColoringModel = selectionColoringModel;
		this.selectionModel = selectionModel;

		registerAsSelectionListener( selectionModel );

		registerAsColoringListener( selectionColoringModel );

		categoricalLabelColoringRandomSeed = 50;

		createTable();
		createMenuBar();
		showTable();
		registerAsListSelectionListener();
		configureTableRowColoring();
	}


	public void configureTableRowColoring( )
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

				c.setBackground( getColour(row, column) );

				return c;
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

				c.setBackground( getColour(row, column) );

				return c;
			}

		} );
		table.setDefaultRenderer( Integer.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				Component c = super.getTableCellRendererComponent(
						table,
						value,
						isSelected,
						hasFocus,
						row,
						column);

				c.setBackground( getColour(row, column) );

				return c;
			}
		} );
		table.setDefaultRenderer( Object.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column )
			{

				Component c = super.getTableCellRendererComponent(
						table,
						value,
						isSelected,
						hasFocus,
						row,
						column );

				c.setBackground( getColour( row, column ) );

				return c;
			}
		});
	}

	private Color getColour( int rowInView, int columnInView )
	{
		final int row = table.convertRowIndexToModel( rowInView );

		if ( selectionModel.isFocused( tableRowsModel.getTableRows().get( row ) ) )
		{
			return Color.BLUE;
		}

		if ( selectionModel.isSelected( tableRowsModel.getTableRows().get( row ) ) )
		{
			final ARGBType argbType = new ARGBType();
			selectionColoringModel.convert( tableRowsModel.getTableRows().get( row ), argbType );
			final Color color = new Color( ARGBType.red( argbType.get() ), ARGBType.green( argbType.get() ), ARGBType.blue( argbType.get() ) );
			return color;
		}

		return Color.WHITE;
	}

	public void registerAsColoringListener( SelectionColoringModel< T > selectionColoringModel )
	{
		selectionColoringModel.listeners().add( () -> table.repaint( ) );
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
//		return segmentCoordinateToColumnMap.sources( imageSegmentCoordinate );
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

		menu.add( assignValuesMenuItem() );

		return menu;
    }

	private JMenuItem assignValuesMenuItem()
	{
		assignObjectAttributesUI = new AssignValuesToTableRowsUI( this );

		final JMenuItem menuItem = new JMenuItem( "Assign values to selected rows" );
		menuItem.addActionListener( e -> {
			assignObjectAttributesUI.showUI( selectionModel.getSelected() );
		} );
		return menuItem;
	}

	private JMenuItem createSaveAsMenuItem()
	{
		final JMenuItem menuItem = new JMenuItem( "Save as..." );
		menuItem.addActionListener( e -> TableUIs.saveTableUI( table ) );
		return menuItem;
	}

	private JMenuItem addColumnMenuItem()
	{
		final JMenuItem menuItem = new JMenuItem( "New Column..." );

		final TableRowsTableView tableView = this;

		menuItem.addActionListener( e -> TableUIs.addColumnUI( tableView ) );

		return menuItem;
	}

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
//    	if( segmentCoordinateToColumnMap.sources( imageSegmentCoordinate ) == NO_COLUMN_SELECTED )
//			return false;
//
//        return true;
//    }
//
//    public Double getObjectCoordinate( ImageSegmentCoordinate imageSegmentCoordinate, int row )
//    {
//        if ( segmentCoordinateToColumnMap.sources( imageSegmentCoordinate ) != NO_COLUMN_SELECTED )
//        {
//            final int columnIndex = table.getColumnModel().getColumnIndex( segmentCoordinateToColumnMap.sources( imageSegmentCoordinate ) );
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

	public List< String > getColumnNames()
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
//				table.getColumnModel().getColumnIndex( getCoordinateColumn( ImageSegmentCoordinate.LabelId ) );
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
//		final Double labelId = ( Double ) table.getValueAt( rowInModel, labelColumnIndex );
//
//		String key;
//		if ( timeColumnIndex == -1 )
//		{
//			key = getObjectKey( labelId );
//		}
//		else
//		{
//			final Double timePoint = ( Double ) table.getValueAt( rowInModel, timeColumnIndex );
//			key = getObjectKey( labelId, timePoint.intValue() );
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

//	public int getRowIndex( Double labelId, Integer timePoint )
//	{
//		if ( objectRowMap == null ) createObjectRowMap();
//
//		final String objectKey = getObjectKey( labelId, timePoint );
//
//		final Integer rowIndex = objectRowMap.sources( objectKey );
//
//		return rowIndex;
//	}

//	private String getObjectKey( Double labelId )
//	{
//		return getObjectKey( labelId, null );
//	}

//	private String getObjectKey( Double labelId, Integer time )
//	{
//		if ( time == null ) return labelId.toString();
//		else return labelId.toString() + "_" + time.toString();
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

	public void registerAsListSelectionListener()
	{
		table.getSelectionModel().addListSelectionListener( new ListSelectionListener()
		{
			@Override
			public void valueChanged( ListSelectionEvent e )
			{
				if ( e.getValueIsAdjusting() ) return;

				if ( table.getSelectedRow() == -1 )
				{
					return;
				}

				recentlySelectedRowInView = table.getSelectedRow();

				final int row = table.convertRowIndexToModel( recentlySelectedRowInView );

				selectionModel.focus( tableRowsModel.getTableRows().get( row ) );
				table.repaint();
			}
		} );
	}

	public void registerAsSelectionListener( SelectionModel< T > selectionModel )
	{
		selectionModel.listeners().add( new SelectionListener< T >()
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
			public void focusEvent( T selection )
			{
				moveToSelectedTableRow( selection );
			}

		} );
	}

	public void moveToSelectedTableRow( TableRow selection )
	{
		final int rowInView = table.convertRowIndexToView( selection.rowIndex() );

		if ( rowInView == recentlySelectedRowInView ) return;

		moveToRowInView( rowInView );
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

		if ( Number.class.isAssignableFrom( table.getColumnClass( columnIndex ) ) )
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


			// TODO: whether and how to automatically close this dialog?
			new NumericColoringModelDialog(
					column,
					coloringModel );


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
