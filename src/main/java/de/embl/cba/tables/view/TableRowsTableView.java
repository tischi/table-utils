package de.embl.cba.tables.view;

import bdv.tools.HelpDialog;
import de.embl.cba.tables.TableRows;
import de.embl.cba.tables.TableUIs;
import de.embl.cba.tables.Tables;
import de.embl.cba.tables.annotate.Annotator;
import de.embl.cba.tables.color.*;
import de.embl.cba.tables.tablerow.TableRow;
import de.embl.cba.tables.select.SelectionListener;
import de.embl.cba.tables.select.SelectionModel;
import de.embl.cba.tables.select.AssignValuesToSelectedRowsDialog;
import de.embl.cba.tables.color.ColorByColumn;
import de.embl.cba.tables.measure.MeasureDistance;
import de.embl.cba.tables.tablerow.TableRowImageSegment;
import ij.IJ;
import ij.gui.GenericDialog;
import net.imglib2.type.numeric.ARGBType;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.Map;


public class TableRowsTableView < T extends TableRow > extends JPanel
{
	private final List< T > tableRows;
	private final SelectionModel< T > selectionModel;
	private final SelectionColoringModel< T > selectionColoringModel;
	private final String tableName;

	private JFrame frame;
    private JScrollPane scrollPane;
    private JMenuBar menuBar;

	private JTable table;
	private int recentlySelectedRowInView;
	private AssignValuesToSelectedRowsDialog assignObjectAttributesUI;
	private HelpDialog helpDialog;
	private ColorByColumn< T > colorByColumn;
	private MeasureDistance< T > measureDistance;
	private Component parentComponent;

	public TableRowsTableView(
			final List< T > tableRows,
			final SelectionModel< T > selectionModel,
			final SelectionColoringModel< T > selectionColoringModel )
	{
		this( tableRows, selectionModel, selectionColoringModel, "" );
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

		recentlySelectedRowInView = -1;

		registerAsSelectionListener( selectionModel );
		registerAsColoringListener( selectionColoringModel );
	}

	public List< T > getTableRows()
	{
		return tableRows;
	}

	public void showTableAndMenu()
	{
		configureJTable();
		registerAsListSelectionListener();
		configureTableRowColoring();
		createMenuAndShow();
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

	private void configureJTable()
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

		add( scrollPane );

		table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

		updateUI();
	}

	private void createMenuBar()
	{
		menuBar = new JMenuBar();

		menuBar.add( createTableMenu() );

		menuBar.add( createSelectionMenu() );

		menuBar.add( createColoringMenu() );

		menuBar.add( createAnnotateMenu() );

		menuBar.add( createMeasureMenu() );

		menuBar.add( createHelpMenu() );
	}

	private JMenu createSelectionMenu()
	{
		JMenu menu = new JMenu( "Select" );

		menu.add( createSelectAllMenuItem() );

		return menu;
	}

	private JMenu createAnnotateMenu()
	{
		JMenu menu = new JMenu( "Annotate" );

		menu.add( createStartNewAnnotationMenuItem() );

		menu.add( createContinueExistingAnnotationMenuItem() );

		return menu;
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

		return menu;
    }

	private JMenuItem createSaveAsMenuItem()
	{
		final JMenuItem menuItem = new JMenuItem( "Save as..." );
		menuItem.addActionListener( e ->
				SwingUtilities.invokeLater( () ->
						TableUIs.saveTableUI( table ) ) );

		return menuItem;
	}


	private JMenuItem createSelectAllMenuItem()
	{
		final JMenuItem menuItem = new JMenuItem( "Select all" );

		menuItem.addActionListener( e ->
				SwingUtilities.invokeLater( () ->
						selectAll() ) );

		return menuItem;
	}

	private JMenuItem createStartNewAnnotationMenuItem()
	{
		final JMenuItem menuItem = new JMenuItem( "Start new annotation..." );

		menuItem.addActionListener( e ->
				SwingUtilities.invokeLater( () ->
						startNewAnnotation() ) );

		return menuItem;
	}

	private JMenuItem createContinueExistingAnnotationMenuItem()
	{
		final JMenuItem menuItem = new JMenuItem( "Continue existing annotation..." );

		menuItem.addActionListener( e ->
				SwingUtilities.invokeLater( () ->
						{
							final String annotationColumn = TableUIs.selectColumnNameUI( table, "Annotation column" );
							continueExistingAnnotation( annotationColumn );
						}
						) );

		return menuItem;
	}

	private void selectAll()
	{

		selectionModel.setSelected( tableRows, true );
//		for ( T tableRow : tableRows )
//		{
//			selectionModel.setSelected( tableRow, true );
//			selectionModel.focus( tableRow );
//		}

	}

	private void startNewAnnotation()
	{

		final GenericDialog gd = new GenericDialog( "" );
		gd.addStringField( "Annotation column name", "", 30 );
		gd.showDialog();
		if( gd.wasCanceled() ) return;
		final String columnName = gd.getNextString();
		this.addColumn( columnName, "None" );

		continueExistingAnnotation( columnName );
	}

	private void continueExistingAnnotation( String columnName )
	{
		final ColorByColumn< T > colorByColumn = new ColorByColumn<>(
				table,
				selectionColoringModel );

		selectionColoringModel.setSelectionMode( SelectionColoringModel.SelectionMode.SelectionColor );

		final ColoringModel< T > coloringModel = colorByColumn.colorByColumn(
				columnName,
				ColorByColumn.RANDOM_GLASBEY );

//		(( CategoryTableRowColumnColoringModel< TableRowImageSegment > ) coloringModel )
//				.getSpecialInputToColor().put( "None", new ARGBType( 0 ) );

		final Annotator annotator = new Annotator(
				columnName,
				tableRows,
				table,
				selectionModel,
				coloringModel );

		annotator.showDialog();
	}

	private void createMenuAndShow()
	{
		frame = new JFrame( tableName );

		createMenuBar();

		frame.setJMenuBar( menuBar );

		//Show the table
		//frame.add( scrollPane );

		//Create and set up the content pane.
		this.setOpaque( true ); //content panes must be opaque
		frame.setContentPane( this );

		if ( parentComponent != null )
		{
			frame.setLocation(
					parentComponent.getLocationOnScreen().x,
					parentComponent.getLocationOnScreen().y + parentComponent.getHeight() + 10
			);

			frame.setPreferredSize( new Dimension(
					parentComponent.getWidth(),
					parentComponent.getHeight() / 3  ) );
		}


		//Display the window.
		frame.pack();
		frame.setVisible( true );
	}

	public void addColumn( String column, Object defaultValue )
	{
		Tables.addColumn( table.getModel(), column, defaultValue );
		TableRows.addColumn( tableRows, column, defaultValue );
	}

	public void addColumn( String column, Object[] values )
	{
		Tables.addColumn( table.getModel(), column, values );
		TableRows.addColumn( tableRows, column, values );
	}

	public void addColumns( Map< String, List< String > > columns )
	{
		for ( String columnName : columns.keySet() )
			addColumn( columnName, columns.get( columnName ).toArray() );
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

	public void setParentComponent( Component component )
	{
		this.parentComponent = component;
	}
}
