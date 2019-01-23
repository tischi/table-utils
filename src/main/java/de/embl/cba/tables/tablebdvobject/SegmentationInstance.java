package de.embl.cba.tables.tablebdvobject;

import de.embl.cba.tables.TableUtils;

import java.util.ArrayList;

public class SegmentationInstance implements TableRow, LabelImageRegion
{
	private final SegmentationInstancesModel model;
	private final int tableRowIndex;
	private final LabelAndTimePoint labelAndTimePoint;

	public SegmentationInstance( SegmentationInstancesModel model,
								 int tableRowIndex,
								 LabelAndTimePoint labelAndTimePoint )
	{
		this.model = model;
		this.tableRowIndex = tableRowIndex;
		this.labelAndTimePoint = labelAndTimePoint;
	}

	@Override
	public ArrayList< String > tableColumnNames()
	{
		return TableUtils.getColumnNames( model.getTable() );
	}

	@Override
	public Object valueInTableColumn( String columnName )
	{
		final int columnIndex = model.getTable().getColumnModel().getColumnIndex( columnName );
		final Object value = model.getTable().getModel().getValueAt( tableRowIndex, columnIndex );
		return value;
	}

	@Override
	public int tableRowIndex()
	{
		return tableRowIndex;
	}

	@Override
	public LabelAndTimePoint labelAndTimepoint()
	{
		return labelAndTimePoint;
	}

}
