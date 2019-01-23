package de.embl.cba.tables.tablebdvobject;

public class LabelAndTimePoint
{
	final public Double label;
	final public Integer timepoint;

	public LabelAndTimePoint( double label, int timepoint)
	{
		this.label = label;
		this.timepoint = timepoint;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof LabelAndTimePoint )) return false;
		LabelAndTimePoint labelAndTimePoint = (LabelAndTimePoint ) o;
		return label == labelAndTimePoint.label && timepoint == labelAndTimePoint.timepoint;
	}

	@Override
	public int hashCode() {
		return label.hashCode() * timepoint.hashCode();
	}

}
