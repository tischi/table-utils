package de.embl.cba.tables.modelview.datamodels;

import de.embl.cba.tables.modelview.objects.AnnotatedImageSegment;
import de.embl.cba.tables.modelview.objects.TableRow;

import java.util.ArrayList;

public interface TableRowsModel < T extends TableRow >
{
	String getTimePointColumnName();

	String getLabelFeatureName();

	ArrayList< T > getTableRows();
}
