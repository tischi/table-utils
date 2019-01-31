package de.embl.cba.tables.modelview.images;

import java.util.Map;


// TODO: make it implement map? or just use a map?
// the point of putting a sources() into it was to be able to make it lazy...
public interface ImageSourcesModel
{
	/**
	 *
	 */
	Map< String, SourceAndMetadata > sources();

}
