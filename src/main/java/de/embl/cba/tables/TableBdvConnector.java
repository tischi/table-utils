package de.embl.cba.tables;

import bdv.util.Bdv;
import de.embl.cba.bdv.utils.BdvUserInterfaceUtils;
import de.embl.cba.bdv.utils.BdvUtils;
import de.embl.cba.bdv.utils.behaviour.BehaviourRandomColorShufflingEventHandler;
import de.embl.cba.bdv.utils.converters.MappingLinearARGBConverter;
import de.embl.cba.bdv.utils.converters.MappingRandomARGBConverter;
import de.embl.cba.bdv.utils.converters.RandomARGBConverter;
import de.embl.cba.bdv.utils.lut.Luts;
import de.embl.cba.bdv.utils.selection.BdvSelectionEventHandler;
import de.embl.cba.bdv.utils.selection.SelectionEventListener;
import de.embl.cba.tables.objects.ObjectCoordinate;
import de.embl.cba.tables.objects.ObjectTablePanel;
import de.embl.cba.tables.objects.attributes.AssignObjectAttributesUI;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.volatiles.VolatileARGBType;
import org.scijava.ui.behaviour.ClickBehaviour;
import org.scijava.ui.behaviour.io.InputTriggerConfig;
import org.scijava.ui.behaviour.util.Behaviours;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class TableBdvConnector
{
	final private ObjectTablePanel objectTablePanel;
	final private BdvSelectionEventHandler bdvSelectionEventHandler;
	private final JTable table;
	private final Converter< RealType, VolatileARGBType > originalConverter;
	private final Bdv bdv;
	private boolean isCategoricalColoring;
	private ConcurrentHashMap< Double, Object > currentObjectAttributeMap;
	private boolean isSelectionByAttribute;

	public static final String OBJECT_GROUPING_TRIGGER = "ctrl G";


	public TableBdvConnector( ObjectTablePanel objectTablePanel,
							  BdvSelectionEventHandler bdvSelectionEventHandler )
	{
		this.bdvSelectionEventHandler = bdvSelectionEventHandler;
		this.objectTablePanel = objectTablePanel;

		this.table = objectTablePanel.getTable();
		this.bdv = bdvSelectionEventHandler.getBdv();

		isCategoricalColoring = false;
		isSelectionByAttribute = false;

		originalConverter = bdvSelectionEventHandler.getSelectableConverter().getWrappedConverter();

		configureBdvTableConnection();

		configureTableBdvConnection();

		installObjectAttributeAssignment();

		objectTablePanel.addMenu( createColoringMenuItem() );
	}

	public void installObjectAttributeAssignment()
	{

		final AssignObjectAttributesUI assignObjectAttributesUI = new AssignObjectAttributesUI( objectTablePanel );

		final Behaviours behaviours = new Behaviours( new InputTriggerConfig() );
		behaviours.install( bdv.getBdvHandle().getTriggerbindings(),
				"bdv-object-attributes-" + objectTablePanel.getName() );

		behaviours.behaviour( ( ClickBehaviour ) ( x, y ) ->
		{
			assignObjectAttributesUI.showUI( bdvSelectionEventHandler.getSelectedValues()  );
		}
		, "fetch-selected-objects-" + objectTablePanel.getName(), OBJECT_GROUPING_TRIGGER );
	}

	public void setSelectionByAttribute( boolean selectionByAttribute )
	{
		isSelectionByAttribute = selectionByAttribute;
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

				if ( isCategoricalColoring && isSelectionByAttribute )
				{
					selectAllObjectsWithSameAttribute( objectLabel, currentObjectAttributeMap, bdvSelectionEventHandler );
				}
			}
		} );
	}

	public static void selectAllObjectsWithSameAttribute( double objectLabel, ConcurrentHashMap< Double, Object > currentObjectAttributeMap, BdvSelectionEventHandler bdvSelectionEventHandler )
	{
		final Object objectAttribute = currentObjectAttributeMap.get( objectLabel );

		for ( Map.Entry< Double, Object > entry : currentObjectAttributeMap.entrySet() )
		{
			if ( entry.getValue().equals( objectAttribute ) )
			{
				bdvSelectionEventHandler.addSelection( entry.getKey() );
			}
		}

		bdvSelectionEventHandler.requestRepaint();
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
			converter = createMappingLinearARGBConverter( colorByColumn );
		}
		else if ( firstValueInColumn instanceof String )
		{
			isCategoricalColoring = true;
			converter = createMappingRandomARGBConverter( colorByColumn );
			new BehaviourRandomColorShufflingEventHandler( bdv, ( RandomARGBConverter ) converter, "colorByColumn" );
		}
		else
		{
			Logger.warn( "Column types must be Number or String");
		}

		return converter;
	}

	public MappingLinearARGBConverter createMappingLinearARGBConverter( String selectedColumn )
	{
		currentObjectAttributeMap = objectTablePanel.getLabelHashMap( selectedColumn );

		final Function< Double, Double > labelColumnMapper = new Function< Double, Double >()
		{
			@Override
			public Double apply( Double objectLabel )
			{
				return ( Double ) currentObjectAttributeMap.get( objectLabel );
			};
		};

		final double[] minMaxValues = objectTablePanel.getMinMaxValues( selectedColumn );

		final MappingLinearARGBConverter converter = new MappingLinearARGBConverter(
				minMaxValues[ 0 ],
				minMaxValues[ 1 ],
				Luts.BLUE_WHITE_RED,
				labelColumnMapper );

		BdvUserInterfaceUtils.showBrightnessDialog( bdv, selectedColumn, converter );

		return converter;
	}

	private MappingRandomARGBConverter createMappingRandomARGBConverter( String selectedColumn )
	{
		currentObjectAttributeMap = objectTablePanel.getLabelHashMap( selectedColumn );

		final Function< Double, Object > labelColumnMapper = new Function< Double, Object >()
		{
			@Override
			public Object apply( Double objectLabel )
			{
				return currentObjectAttributeMap.get( objectLabel );
			};
		};

		return new MappingRandomARGBConverter(
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
