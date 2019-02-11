package de.embl.cba.tables.modelview.views;

import bdv.tools.HelpDialog;
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
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;


public class TableRowsTableView < T extends TableRow > extends JPanel
{
	private final SelectionModel< T > selectionModel;
	private final TableRowsModel< T > tableRowsModel;
	private final SelectionColoringModel< T > selectionColoringModel;

	private JFrame frame;
    private JScrollPane scrollPane;
    private JMenuBar menuBar;
	private Map< String, double[] > columnsMinMaxMap;
	private Set< String > categoricalColumns;
	private JTable table;
	private int recentlySelectedRowInView;
	private AssignValuesToTableRowsUI assignObjectAttributesUI;
	private HelpDialog helpDialog;
	private Set< String > customColumns;

	public TableRowsTableView(
			final TableRowsModel< T > tableRowsModel,
			final SelectionModel< T > selectionModel,
			final SelectionColoringModel< T > selectionColoringModel )
	{
		super( new GridLayout(1, 0 ) );
		this.tableRowsModel = tableRowsModel;
		this.selectionColoringModel = selectionColoringModel;
		this.selectionModel = selectionModel;

		this.categoricalColumns = new HashSet<>(  );
		this.customColumns = new HashSet<>(  );

		registerAsSelectionListener( selectionModel );
		registerAsColoringListener( selectionColoringModel );

		createTable();
		createMenusAndTableAndShow();

		registerAsListSelectionListener();
		configureTableRowColoring();
	}

	public Set< String > categoricalColumns( )
	{
		return categoricalColumns;
	}

	private void configureTableRowColoring()
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
			final int colorIndex = argbType.get();
			final Color color = new Color( ARGBType.red( colorIndex ), ARGBType.green( colorIndex ), ARGBType.blue( colorIndex ) );
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

		menuBar.add( createColoringMenu() );

		menuBar.add( createHelpMenu() );
	}

	private JMenu createHelpMenu()
	{
		JMenu menu = new JMenu( "Help" );

		menu.add( createShowHelpMenuItem() );

		return menu;
	}

	private JMenuItem createShowHelpMenuItem()
	{
		initHelpDialog();
		final JMenuItem menuItem = new JMenuItem( "Show help" );
		menuItem.addActionListener( e ->
				SwingUtilities.invokeLater( () ->
						helpDialog.setVisible( ! helpDialog.isVisible() ) ) );
		return menuItem;
	}

	public void addMenu( JMenuItem menuItem )
	{
		SwingUtilities.invokeLater( () ->
		{
			menuBar.add( menuItem );
			if ( frame != null ) SwingUtilities.updateComponentTreeUI( frame );
		});
	}

	private JMenu createTableMenu()
    {
        JMenu menu = new JMenu( "Table" );

        menu.add( createSaveAsMenuItem() );

		menu.add( createAddColumnMenuItem() );

		menu.add( assignValueMenuItem() );

		return menu;
    }

	private JMenuItem assignValueMenuItem()
	{
		assignObjectAttributesUI = new AssignValuesToTableRowsUI( this );

		final JMenuItem menuItem = new JMenuItem( "Assign Value to Selected Objects..." );

		// TODO: make only show custom columns
		menuItem.addActionListener( e ->
				SwingUtilities.invokeLater( () ->
						assignObjectAttributesUI.showUI( selectionModel.getSelected() ) ) );

		return menuItem;
	}

	private JMenuItem createSaveAsMenuItem()
	{
		final JMenuItem menuItem = new JMenuItem( "Save as..." );
		menuItem.addActionListener( e ->
				SwingUtilities.invokeLater( () ->
						TableUIs.saveTableUI( table ) ) );

		return menuItem;
	}

	private JMenuItem createAddColumnMenuItem()
	{
		final JMenuItem menuItem = new JMenuItem( "Add Custom Column..." );

		final TableRowsTableView tableView = this;

		menuItem.addActionListener( e ->
				SwingUtilities.invokeLater( () ->
						TableUIs.addColumnUI( tableView ) ) );

		return menuItem;
	}

    public void createMenusAndTableAndShow()
	{
		try
		{
			SwingUtilities.invokeAndWait( () ->
			{
				frame = new JFrame( "Table" );

				createMenuBar();

				frame.setJMenuBar( menuBar );

				//Show the table
				//frame.add( scrollPane );

				//Create and set up the content pane.
				this.setOpaque( true ); //content panes must be opaque
				frame.setContentPane( this );

				//Display the window.
				frame.pack();
				frame.setVisible( true );
			});
		} catch ( InterruptedException e )
		{
			e.printStackTrace();
		} catch ( InvocationTargetException e )
		{
			e.printStackTrace();
		}
	}

	public void addColumn( String column, Object defaultValue )
	{
		customColumns.add( column );
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
		table.getSelectionModel().addListSelectionListener( e ->
		{
			SwingUtilities.invokeLater( () ->
			{
				if ( e.getValueIsAdjusting() ) return;

				if ( table.getSelectedRow() == -1 ) return;

				recentlySelectedRowInView = table.getSelectedRow();

				final int row = table.convertRowIndexToModel( recentlySelectedRowInView );

				selectionModel.focus( tableRowsModel.getTableRows().get( row ) );

				table.repaint();
			});

		});
	}

	public void registerAsSelectionListener( SelectionModel< T > selectionModel )
	{
		selectionModel.listeners().add( new SelectionListener< T >()
		{
			@Override
			public synchronized void selectionChanged()
			{
				SwingUtilities.invokeLater( () -> table.repaint() );
			}

			@Override
			public void focusEvent( T selection )
			{
				SwingUtilities.invokeLater( () -> moveToSelectedTableRow( selection ) );
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
		JMenu coloringMenu = new JMenu( "Color by" );

		for ( int col = 0; col < table.getColumnCount(); col++ )
		{
			coloringMenu.add( createColorByColumnMenuItem( table.getColumnName( col ) ) );
		}

		return coloringMenu;
	}

	private JMenuItem createColorByColumnMenuItem( final String column )
	{
		final JMenuItem colorByColumnMenuItem = new JMenuItem( column );

		colorByColumnMenuItem.addActionListener( e -> {
			new Thread( () -> colorBy( column ) ).start();
		} );

		return colorByColumnMenuItem;
	}

	public void colorBy( String columnName )
	{
		// TODO: clean this up
		// this suffers from the fact that I do not know
		// the column classes...
		// and even if, some are numeric but should be treated
		// categorical

		final int columnIndex = table.getColumnModel().getColumnIndex( columnName );

		if ( Number.class.isAssignableFrom( table.getColumnClass( columnIndex ) )
				&& ! categoricalColumns.contains( columnName ) )
		{
			final double[] minMaxValues = getMinMaxValues( columnName );

			final NumericTableRowColumnColoringModel< T > coloringModel
					= new NumericTableRowColumnColoringModel< >(
							columnName,
							new BlueWhiteRedARGBLut( 1000 ),
							minMaxValues[ 0 ],
							minMaxValues[ 1 ]
			);

			selectionColoringModel.setWrappedColoringModel( coloringModel );

			new Thread( () -> new NumericColoringModelDialog( columnName, coloringModel ) ).start();
		}
		else
		{
			final LazyCategoryColoringModel< T > coloringModel
					= new LazyCategoryColoringModel< >( new GlasbeyARGBLut(), 50
			);

			selectionColoringModel.setWrappedColoringModel( coloringModel );
		}
	}

	public void initHelpDialog()
	{
		helpDialog = new HelpDialog( frame, TableUtils.class.getResource( "/TableUtilsHelp.html" ) );
	}


}
