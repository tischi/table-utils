package de.embl.cba.tables;

import bdv.util.Bdv;
import de.embl.cba.bdv.utils.BdvUserInterfaceUtils;
import de.embl.cba.bdv.utils.BdvUtils;
import de.embl.cba.bdv.utils.behaviour.BehaviourRandomColorLutSeedChangeEventHandler;
import de.embl.cba.bdv.utils.converters.MappingLinearARGBConverter;
import de.embl.cba.bdv.utils.converters.MappingRandomARGBConverter;
import de.embl.cba.bdv.utils.converters.RandomARGBConverter;
import de.embl.cba.bdv.utils.lut.Luts;
import de.embl.cba.bdv.utils.selection.BdvLabelSourceSelectionListener;
import de.embl.cba.bdv.utils.selection.BdvSelectionEventHandler;
import de.embl.cba.tables.modelview.segments.ImageSegmentCoordinate;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
	private ConcurrentHashMap< Object, Object > currentObjectAttributeMap;
	private boolean isSelectionByAttribute;

	public static final String OBJECT_ATTRIBUTE_ASSIGNMENT_TRIGGER = "ctrl A";
	private Set< Integer > selectedRows;

	public TableBdvConnector( ObjectTablePanel objectTablePanel,
							  BdvSelectionEventHandler bdvSelectionEventHandler )
	{
		this.bdvSelectionEventHandler = bdvSelectionEventHandler;
		this.objectTablePanel = objectTablePanel;

		this.table = objectTablePanel.getTable();
		this.bdv = bdvSelectionEventHandler.getBdv();

		isCategoricalColoring = false;
		isSelectionByAttribute = false;

		selectedRows = new HashSet<>(  );

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
			if ( selectedRows.size() > 0 )
			{
				assignObjectAttributesUI.showUI( selectedRows );
			}
		}
		, "fetch-selected-segments-" + objectTablePanel.getName(), OBJECT_ATTRIBUTE_ASSIGNMENT_TRIGGER );
	}

	public void setSelectionByAttribute( boolean selectionByAttribute )
	{
		isSelectionByAttribute = selectionByAttribute;
	}

	private void configureBdvTableConnection()
	{
		bdvSelectionEventHandler.addSelectionEventListener( new BdvLabelSourceSelectionListener()
		{
			@Override
			public void selectionChanged( double label, int timepoint, boolean selected )
			{
				if ( ! objectTablePanel.hasCoordinate( ImageSegmentCoordinate.Label ) ) return;

				int row = getRow( label, timepoint );
				selectedRows.add( row );

				objectTablePanel.highlightRow( row );

				if ( isCategoricalColoring && isSelectionByAttribute )
				{
					selectAllObjectsWithSameAttribute(
							label,
							getCurrentTimepoint(),
							currentObjectAttributeMap,
							bdvSelectionEventHandler );
				}
			}

			//@Override
			public void valueUnselected( double objectLabel, int timepoint )
			{
				if ( ! objectTablePanel.hasCoordinate( ImageSegmentCoordinate.Label ) ) return;

				int row = getRow( objectLabel, timepoint );
				selectedRows.remove( row );
			}

			public int getRow( double objectLabel, int timepoint )
			{
				int row;

				if ( objectTablePanel.hasCoordinate( ImageSegmentCoordinate.T ) )
				{
					row = objectTablePanel.getRowIndex( objectLabel, timepoint );
				}
				else
				{
					row = objectTablePanel.getRowIndex( objectLabel );
				}
				return row;
			}

			public int getCurrentTimepoint()
			{
				return bdvSelectionEventHandler.getBdv().getBdvHandle().getViewerPanel().getState().getCurrentTimepoint();
			}
		} );
	}



	public static void selectAllObjectsWithSameAttribute(
			double objectLabel,
			int currentTimepoint,
			ConcurrentHashMap< Object, Object > currentObjectAttributeMap,
			BdvSelectionEventHandler bdvSelectionEventHandler )
	{
		final Object objectAttribute = currentObjectAttributeMap.get( objectLabel );

		for ( Map.Entry< Object, Object > entry : currentObjectAttributeMap.entrySet() )
		{
			if ( entry.getValue().equals( objectAttribute ) )
			{
				bdvSelectionEventHandler.selectionChanged( (Double) entry.getKey(), currentTimepoint, true  );
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
					if ( objectTablePanel.hasCoordinate( ImageSegmentCoordinate.Label ) )
					{
						final int selectedRow = table.getSelectedRow();

						final Double objectLabel = objectTablePanel.getObjectCoordinate( ImageSegmentCoordinate.Label, selectedRow );

						Integer timepoint = getTimepoint( selectedRow );

						bdvSelectionEventHandler.selectionChanged( objectLabel, timepoint, true );

						moveBdvToObjectPosition( selectedRow );
					}
					else
					{
						Logger.error( "Please specify the Object Label getColumn!" );
					}
				}
			}
		});

	}

	public Integer getTimepoint( int selectedRow )
	{
		Integer timepoint = 0;
		if ( objectTablePanel.hasCoordinate( ImageSegmentCoordinate.Label.T ) )
		{
			final Double timepointDouble = (Double) objectTablePanel.getObjectCoordinate( ImageSegmentCoordinate.Label.T, selectedRow );
			timepoint = timepointDouble.intValue();
		}
		return timepoint;
	}

	private void moveBdvToObjectPosition( int row )
	{
		final Double x = objectTablePanel.getObjectCoordinate( ImageSegmentCoordinate.X, row );
		final Double y = objectTablePanel.getObjectCoordinate( ImageSegmentCoordinate.Y, row );

		if ( x != null && y != null )
		{
			Double z = objectTablePanel.getObjectCoordinate( ImageSegmentCoordinate.Z, row );
			if ( z == null ) z = 0.0;

			Double t = objectTablePanel.getObjectCoordinate( ImageSegmentCoordinate.T, row );
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
				if ( ! objectTablePanel.hasCoordinate( ImageSegmentCoordinate.Label ) )
				{
					Logger.warn( "Please specify the object labelId getColumn:\n" +
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
			new BehaviourRandomColorLutSeedChangeEventHandler( bdv, ( RandomARGBConverter ) converter, "colorByColumn" );
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
