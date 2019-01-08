package de.embl.cba.tables;

import bdv.util.Bdv;
import de.embl.cba.bdv.utils.BdvUserInterfaceUtils;
import de.embl.cba.bdv.utils.BdvUtils;
import de.embl.cba.bdv.utils.converters.CategoricalMappingARGBConverter;
import de.embl.cba.bdv.utils.converters.LinearMappingARGBConverter;
import de.embl.cba.bdv.utils.lut.Luts;
import de.embl.cba.bdv.utils.selection.BdvSelectionEventHandler;
import de.embl.cba.bdv.utils.selection.SelectionEventListener;
import de.embl.cba.tables.objects.ObjectCoordinate;
import de.embl.cba.tables.objects.ObjectTablePanel;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.volatiles.VolatileARGBType;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class TableBdvConnector
{
	final private ObjectTablePanel objectTablePanel;
	final private BdvSelectionEventHandler bdvSelectionEventHandler;
	private final JTable table;
	private final Converter< RealType, VolatileARGBType > originalConverter;
	private final Bdv bdv;

	public TableBdvConnector( ObjectTablePanel objectTablePanel,
							  BdvSelectionEventHandler bdvSelectionEventHandler )
	{
		this.bdvSelectionEventHandler = bdvSelectionEventHandler;
		this.objectTablePanel = objectTablePanel;
		this.table = objectTablePanel.getTable();

		this.bdv = bdvSelectionEventHandler.getBdv();

		originalConverter = bdvSelectionEventHandler.getSelectableConverter().getWrappedConverter();

		configureBdvTableConnection();

		configureTableBdvConnection();

		objectTablePanel.addMenu( createColoringMenuItem() );
	}

	private void configureBdvTableConnection()
	{
		bdvSelectionEventHandler.addSelectionEventListener( new SelectionEventListener()
		{
			@Override
			public void valueSelected( double objectLabel )
			{
				final int row = objectTablePanel.getRowIndex( objectLabel );

				table.setRowSelectionInterval( row, row );

				table.scrollRectToVisible( table.getCellRect( row,0, true ) );
			}
		} );
	}

	private void configureTableBdvConnection( )
	{
		table.addMouseListener(new MouseInputAdapter()
		{
			public void mousePressed(MouseEvent me)
			{
				if( me.isControlDown() )
				{
					final int selectedRow = table.getSelectedRow();

					bdvSelectionEventHandler.addSelection(
									objectTablePanel.getObjectCoordinate(
											ObjectCoordinate.Label, selectedRow ) );

					moveBdvToObjectPosition( selectedRow );
				}
			}
		});

	}

	private void moveBdvToObjectPosition( int row )
	{
		final Double x = objectTablePanel.getObjectCoordinate( ObjectCoordinate.X, row );
		final Double y = objectTablePanel.getObjectCoordinate( ObjectCoordinate.Y, row );

		if ( x != null && y != null )
		{
			Double z = objectTablePanel.getObjectCoordinate( ObjectCoordinate.Z, row );
			if ( z == null ) z = 0.0;

			Double t = objectTablePanel.getObjectCoordinate( ObjectCoordinate.T, row );
			if ( t == null ) t = 0.0;

			BdvUtils.moveToPosition(
					bdv,
					new double[]{ x, y, z},
					Double.valueOf( t ).intValue(),
					500);
		}
	}

	private JMenu createColoringMenuItem()
	{
		JMenu coloringMenu = new JMenu( "Coloring" );

		coloringMenu.add( getRestoreOriginalColorMenuItem() );

		for ( int col = 0; col < table.getColumnCount(); col++ )
		{
			coloringMenu.add( createColorByColumnMenuItem( table.getColumnName( col ) ) );
		}

		return coloringMenu;
	}

	private JMenuItem createColorByColumnMenuItem( final String colorByColumn )
	{
		final JMenuItem colorByColumnMenuItem = new JMenuItem( "Color by " + colorByColumn );

		colorByColumnMenuItem.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if ( ! objectTablePanel.isCoordinateColumnSet( ObjectCoordinate.Label ) )
				{
					Logger.warn( "Please specify the object label column:\n" +
							"[ Objects > Select coordinates... ]" );
					return;
				}

				final Object firstValueInColumn = table.getValueAt( 0, table.getColumnModel().getColumnIndex( colorByColumn ) );

				Converter< RealType, VolatileARGBType > converter = createSuitableConverter( firstValueInColumn, colorByColumn );

				bdvSelectionEventHandler.getSelectableConverter().setWrappedConverter( converter );

				BdvUtils.repaint( bdvSelectionEventHandler.getBdv() );
			}

		} );

		return colorByColumnMenuItem;
	}

	private Converter< RealType, VolatileARGBType > createSuitableConverter( Object firstValueInColumn, String colorByColumn )
	{
		Converter< RealType, VolatileARGBType > converter = null;

		if ( firstValueInColumn instanceof Number )
		{
			converter = createLinearMappingARGBConverter( colorByColumn );
		}
		else if ( firstValueInColumn instanceof String )
		{
			converter = createCategoricalMappingRandomARGBConverter( colorByColumn );
		}
		else
		{
			Logger.warn( "Column types must be Number or String");
		}

		return converter;
	}

	public LinearMappingARGBConverter createLinearMappingARGBConverter( String selectedColumn )
	{
		final ConcurrentHashMap< Object, Object > map = objectTablePanel.getLabelHashMap(
				objectTablePanel.getCoordinateColumn( ObjectCoordinate.Label ),
				selectedColumn );

		final Function< Double, Double > labelColumnMapper = new Function< Double, Double >()
		{
			@Override
			public Double apply( Double objectLabel )
			{
				return ( Double ) map.get( objectLabel );
			};
		};

		final double[] minMaxValues = objectTablePanel.getMinMaxValues( selectedColumn );

		final LinearMappingARGBConverter converter = new LinearMappingARGBConverter(
				minMaxValues[ 0 ],
				minMaxValues[ 1 ],
				Luts.BLUE_WHITE_RED,
				labelColumnMapper );

		BdvUserInterfaceUtils.showBrightnessDialog( bdv, selectedColumn, converter );

		return converter;
	}

	private CategoricalMappingARGBConverter createCategoricalMappingRandomARGBConverter( String selectedColumn )
	{

		final ConcurrentHashMap< Object, Object > map = objectTablePanel.getLabelHashMap(
				objectTablePanel.getCoordinateColumn( ObjectCoordinate.Label ),
				selectedColumn );

		final Function< Double, Object > labelColumnMapper = new Function< Double, Object >()
		{
			@Override
			public Object apply( Double objectLabel )
			{
				return map.get( objectLabel );
			};
		};

		return new CategoricalMappingARGBConverter(
				labelColumnMapper,
				Luts.GLASBEY
		);
	}

	private JMenuItem getRestoreOriginalColorMenuItem()
	{
		final JMenuItem restoreOriginalColorMenuItem = new JMenuItem( "Restore original coloring");

		restoreOriginalColorMenuItem.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				bdvSelectionEventHandler.getSelectableConverter().setWrappedConverter( originalConverter );
				BdvUtils.repaint( bdvSelectionEventHandler.getBdv() );
		}
		} );
		return restoreOriginalColorMenuItem;
	}


}