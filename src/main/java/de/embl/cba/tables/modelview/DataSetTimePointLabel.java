package de.embl.cba.tables.modelview;

public class DataSetTimePointLabel
{
	final public String dataSet;
	final public Double label;
	final public Integer timepoint;

	public DataSetTimePointLabel( String dataSet, double label, int timePoint)
	{
		this.dataSet = dataSet;
		this.label = label;
		this.timepoint = timePoint;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof DataSetTimePointLabel )) return false;
		DataSetTimePointLabel dataSetTimePointLabel = (DataSetTimePointLabel ) o;
		return label == dataSetTimePointLabel.label && timepoint == dataSetTimePointLabel.timepoint;
	}

	@Override
	public int hashCode() {
		return label.hashCode() * timepoint.hashCode();
	}

}
