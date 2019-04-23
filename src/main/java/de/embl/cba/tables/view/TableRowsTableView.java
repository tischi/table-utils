package de.embl.cba.tables.view;

import bdv.tools.HelpDialog;
import de.embl.cba.tables.TableUIs;
import de.embl.cba.tables.Tables;
import de.embl.cba.tables.color.*;
import de.embl.cba.tables.tablerow.TableRow;
import de.embl.cba.tables.select.SelectionListener;
import de.embl.cba.tables.select.SelectionModel;
import de.embl.cba.tables.select.AssignValuesToSelectedRowsDialog;
import de.embl.cba.tables.color.ColorByColumn;
import de.embl.cba.tables.measure.MeasureDistance;
import ij.IJ;
import net.imglib2.type.numeric.ARGBType;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.*;
import java.util.List;


public class TableRowsTableView < T extends TableRow > extends JPanel
{
	private final List< T > tableRows;
	private final SelectionModel< T > selectionModel;
	private final SelectionColoringModel< T > selectionColoringModel;
	private final String tableName;

	private JFrame frame;
    private JScrollPane scrollPane;
    private JMenuBar menuBar;

	private Set< String > categoricalColumnNames;
	private JTable table;
	private int recentlySelectedRowInView;
	private AssignValuesToSelectedRowsDialog assignObjectAttributesUI;
	private HelpDialog helpDialog;
	private Set< String > customColumns;
	private ColorByColumn< T > colorByColumn;
	private MeasureDistance< T > measureDistance;

	public TableRowsTableView(
			final List< T > tableRows,
			final SelectionModel< T > selectionModel,
			final SelectionColoringModel< T > selectionColoringModel )
	{
		this( tableRows, selectionModel, selectionColoringModel, "Table" );
	}
	
	public TableRowsTableView(
			final List< T > tableRows,
			final SelectionModel< T > selectionModel,
			final SelectionColoringModel< T > selectionColoringModel,
			String tableName )
	{
		super( new GridLayout(1, 0 ) );
		this.tableRows = tableRows;
		this.selectionColoringModel = selectionColoringModel;
		this.selectionModel = selectionModel;
		this.tableName = tableName;

		this.categoricalColumnNames = new HashSet<>(  );
		this.customColumns = new HashSet<>(  );

		recentlySelectedRowInView = -1;

		registerAsSelectionListener( selectionModel );
		registerAsColoringListener( selectionColoringModel );

		createTable();
		createTableUIAndShow();

		registerAsListSelectionListener();
		configureTableRowColoring();
	}

	public Set< String > categoricalColumnNames( )
	{
		return categoricalColumnNames;
	}

	private void configureTableRowColoring()
	{
		table.setDefaultRenderer( Double.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(
					JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				Component c = super.getTableCellRendererComponent(
						table,
						value,
						isSelected,
						hasFocus,
						row,
						column);

				c.setBackground( getColor(row, column) );

				return c;
			}
		} );

		table.setDefaultRenderer( String.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(
					JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				Component c = super.getTableCellRendererComponent(
						table,
						value,
						isSelected,
						hasFocus,
						row,
						column);

				c.setBackground( getColor(row, column) );

				return c;
			}

		} );

		table.setDefaultRenderer( Long.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(
					JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column )
			{
				Component c = super.getTableCellRendererComponent(
						table,
						value,
						isSelected,
						hasFocus,
						row,
						column );

				c.setBackground( getColor( row, column ) );

				return c;
			}
		});

		table.setDefaultRenderer( Integer.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(
					JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column)
			{
				Component c = super.getTableCellRendererComponent(
						table,
						value,
						isSelected,
						hasFocus,
						row,
						column);

				c.setBackground( getColor(row, column) );

				return c;
			}
		} );

		table.setDefaultRenderer( Object.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(
					JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column )
			{
				Component c = super.getTableCellRendererComponent(
						table,
						value,
						isSelected,
						hasFocus,
						row,
						column );

				c.setBackground( getColor( row, column ) );

				return c;
			}
		});
	}

	private Color getColor( int rowInView, int columnInView )
	{
		final int row = table.convertRowIndexToModel( rowInView );

//		if ( selectionModel.isFocused( tableRows.getTableRows().get( row ) ) )
//		{
//			return Color.BLUE;
//		}

		final ARGBType argbType = new ARGBType();
		selectionColoringModel.convert( tableRows.get( row ), argbType );

		if ( argbType.get() == 0 )
			return Color.WHITE;
		else
			return ColorUtils.getColor( argbType );
	}

	public void registerAsColoringListener( SelectionColoringModel< T > selectionColoringModel )
	{
		selectionColoringModel.listeners().add( () -> table.repaint( ) );
	}

	private void createTable()
    {
		table = Tables.jTableFromTableRows( tableRows );

		table.setPreferredScrollableViewportSize( new Dimension(500, 200) );
        table.setFillsViewportHeight( true );
        table.setAutoCreateRowSorter( true );
        table.setRowSelectionAllowed( true );
		table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

		scrollPane = new JScrollPane(
				table,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        this.add( scrollPane );
        table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

    }

	private void createMenuBar()
	{
		menuBar = new JMenuBar();

		menuBar.add( createTableMenu() );

		menuBar.add( createColoringMenu() );

		menuBar.add( createMeasureMenu() );

		menuBar.add( createHelpMenu() );
	}

	private JMenu createMeasureMenu()
	{
		JMenu menu = new JMenu( "Measure" );

		addMeasureSimilarityMenuItem( menu );

		return menu;
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
		assignObjectAttributesUI = new AssignValuesToSelectedRowsDialog( this );

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
		final JMenuItem menuItem = new JMenuItem( "New Column..." );

		final TableRowsTableView tableView = this;

		menuItem.addActionListener( e ->
				SwingUtilities.invokeLater( () ->
						TableUIs.addColumnUI( tableView ) ) );

		return menuItem;
	}

    private void createTableUIAndShow()
	{
		frame = new JFrame( tableName );

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
	}

	public void addColumn( String column, Object defaultValue )
	{
		customColumns.add( column );
		Tables.addColumn( table.getModel(), column, defaultValue );
	}

	public List< String > getColumnNames()
	{
		return Tables.getColumnNames( table );
	}

	public JTable getTable()
	{
		return table;
	}


	public void moveToRowInView( int rowInView )
	{
		recentlySelectedRowInView = rowInView;
		//table.getSelectionModel().setSelectionInterval( rowInView, rowInView );
		final Rectangle visibleRect = table.getVisibleRect();
		final Rectangle cellRect = table.getCellRect( rowInView, 0, true );
		visibleRect.y = cellRect.y;
		table.scrollRectToVisible( visibleRect );
		table.repaint();
	}

	public void registerAsListSelectionListener()
	{
		table.getSelectionModel().addListSelectionListener( e ->
				SwingUtilities.invokeLater( () ->
				{
					if ( e.getValueIsAdjusting() ) return;

					final int selectedRowInView = table.getSelectedRow();

					if ( selectedRowInView == -1 ) return;

					if ( selectedRowInView == recentlySelectedRowInView ) return;

					recentlySelectedRowInView = selectedRowInView;

					final int row = table.convertRowIndexToModel( recentlySelectedRowInView );

					final T object = tableRows.get( row );

					selectionModel.toggle( object );
					if ( selectionModel.isSelected( object ) )
						selectionModel.focus( object );

					table.repaint();
				}) );
	}

	public void registerAsSelectionListener( SelectionModel< T > selectionModel )
	{
		selectionModel.listeners().add( new SelectionListener< T >()
		{
			@Override
			public synchronized void selectionChanged()
			{
				if ( selectionModel.isEmpty() )
				{
					recentlySelectedRowInView = -1;
					table.getSelectionModel().clearSelection();
				}
				SwingUtilities.invokeLater( () -> table.repaint() );
			}

			@Override
			public synchronized void focusEvent( T selection )
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
		JMenu coloringMenu = new JMenu( "Color" );

		addColorByColumnMenuItem( coloringMenu );

		return coloringMenu;
	}

	private void addColorByColumnMenuItem( JMenu coloringMenu )
	{
		final JMenuItem menuItem = new JMenuItem( "Color by Column..." );

		this.colorByColumn = new ColorByColumn( table, selectionColoringModel );

		menuItem.addActionListener( e ->
				new Thread( () ->
						colorByColumn.showDialog() ).start() );

		coloringMenu.add( menuItem );
	}

	private void addMeasureSimilarityMenuItem( JMenu menu )
	{
		final JMenuItem menuItem = new JMenuItem( "Measure Distance to Selected Rows..." );

		this.measureDistance = new MeasureDistance( table, tableRows );

		menuItem.addActionListener( e ->
				new Thread( () ->
				{
					if ( selectionModel.isEmpty() )
					{
						IJ.showMessage( "Please select one or more objects." );
						return;
					}
					else
					{
						if ( measureDistance.showDialog( selectionModel.getSelected() ) )
						{
							colorByColumn.colorByColumn(
									measureDistance.getNewColumnName(),
									ColorByColumn.LINEAR_BLUE_WHITE_RED );

							selectionColoringModel.setSelectionMode( SelectionColoringModel.SelectionMode.SelectionColor );
						}
					}
				}
 				).start() );

		menu.add( menuItem );
	}

	public MeasureDistance< T > getMeasureDistance()
	{
		return measureDistance;
	}

	public void initHelpDialog()
	{
		helpDialog =
				new HelpDialog(
						frame,
						Tables.class.getResource( "/TableUtilsHelp.html" ) );
	}


	public void close()
	{
		// TODO
	}
}
