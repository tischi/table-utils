package de.embl.cba.tables.tablebdvobject;

import bdv.viewer.Source;
import net.imglib2.type.numeric.RealType;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds all the data
 */
public class SegmentationInstancesModel
{
	private final Source< ? extends RealType< ? > > labelSource;
	private final JTable table;
	private final String labelColumn;
	private final String timePointColumn;
	private final boolean is2D;

	private Map< Integer, LabelAndTimePoint > rowLabelMap;
	private Map< LabelAndTimePoint, Integer > labelRowMap;

	private Map< Integer, SegmentationInstance > segmentationInstanceMap;
	private SelectionModel< SegmentationInstance > selection;

	public SegmentationInstancesModel( Source labelSource,
									   JTable table,
									   String labelColumn,
									   String timePointColumn,
									   boolean is2D )
	{
		this.labelSource = labelSource;
		this.table = table;
		this.labelColumn = labelColumn;
		this.timePointColumn = timePointColumn;
		this.is2D = is2D;

		createMappingsAndObjects();
	}

	private void createMappingsAndObjects()
	{
		rowLabelMap = new HashMap<>();
		labelRowMap = new HashMap<>();
		segmentationInstanceMap = new HashMap<>();

		final int labelColumnIndex = table.getColumnModel().getColumnIndex( labelColumn );
		int timePointColumnIndex = getTimePointColumnIndex( timePointColumn );

		final int rowCount = table.getRowCount();
		for ( int row = 0; row < rowCount; row++ )
		{
			final Double label = ( Double ) table.getValueAt( row, labelColumnIndex );
			final int timePoint = getTimePoint( timePointColumnIndex, row );

			final LabelAndTimePoint labelAndTimePoint = new LabelAndTimePoint(
					label,
					timePoint );

			rowLabelMap.put( row, labelAndTimePoint );
			labelRowMap.put( labelAndTimePoint, row );

			segmentationInstanceMap.put(
					row,
					new SegmentationInstance( this, row, labelAndTimePoint ) );
		}
	}

	private int getTimePoint( int timePointColumnIndex, int row )
	{
		int timepoint = 0;
		if ( timePointColumnIndex != -1 )
		{
			timepoint = ( ( Double ) table.getValueAt( row, timePointColumnIndex ) ).intValue();
		}
		return timepoint;
	}

	private int getTimePointColumnIndex( String timePointColumn )
	{
		int timePointColumnIndex = -1;

		if ( this.timePointColumn != null )
		{
			timePointColumnIndex = table.getColumnModel().getColumnIndex( timePointColumn );
		}

		return timePointColumnIndex;
	}

	public Source< ? extends RealType< ? > > getLabelSource()
	{
		return labelSource;
	}

	public JTable getTable()
	{
		return table;
	}

	public SegmentationInstance getSegmentationInstance( int row )
	{
		return segmentationInstanceMap.get( row );
	}

	public SegmentationInstance getSegmentationInstance( Double label, int timePoint  )
	{
		return getSegmentationInstance( new LabelAndTimePoint( label, timePoint ) );
	}

	public SegmentationInstance getSegmentationInstance( LabelAndTimePoint labelAndTimePoint )
	{
		final Integer row = labelRowMap.get( labelAndTimePoint );
		return segmentationInstanceMap.get( row );
	}

	public boolean is2D()
	{
		return is2D;
	}
}
