package de.embl.cba.tables.modelview.datamodels;

import de.embl.cba.tables.modelview.objects.Segment;

import javax.swing.*;
import java.util.ArrayList;

public class SegmentsFeaturesModel < T extends Segment
{
	private final JTable table;
	private final String labelColumn;
	private final String timePointColumn;

	public SegmentsFeaturesModel(
			ArrayList< Segment >
			String labelColumn,
			String timePointColumn )
	{
		this.table = table;
		this.labelColumn = labelColumn;
		this.timePointColumn = timePointColumn;
	}

	public JTable getTable()
	{
		return table;
	}

	public String getLabelColumn()
	{
		return labelColumn;
	}

	public String getTimePointColumn()
	{
		return timePointColumn;
	}
}
