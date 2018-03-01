/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.dspace.app.rest.model.MetadataEntryRest;
import org.dspace.browse.BrowsableDSpaceObject;
import org.dspace.content.DSpaceObject;
import org.dspace.content.IMetadataValue;
import org.dspace.content.Item;

/**
 * 
 * This is the base converter from/to objects in the DSpace API data model and
 * the REST data model
 * 
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 *
 * @param <M>
 *            the Class in the DSpace API data model
 * @param <R>
 *            the Class in the DSpace REST data model
 */
public abstract class BrowsableDSpaceObjectConverter<M extends BrowsableDSpaceObject, R extends org.dspace.app.rest.model.DSpaceObjectRest>
		extends DSpaceConverter<M, R> {

	@Override
	public R fromModel(M obj) {
		R resource = newInstance();
		resource.setHandle(obj.getHandle());
		if (obj.getID() != null) {
			resource.setUuid(obj.getID().toString());
		}
		resource.setName(obj.getName());
		List<MetadataEntryRest> metadata = new ArrayList<MetadataEntryRest>();
		for (IMetadataValue mv : obj.getMetadata(Item.ANY, Item.ANY, Item.ANY, Item.ANY)) {
			MetadataEntryRest me = new MetadataEntryRest();
			me.setKey(mv.getMetadataField().toString('.'));
			me.setValue(mv.getValue());
			me.setLanguage(mv.getLanguage());
			if(StringUtils.isNotBlank(mv.getAuthority())) {
				me.setAuthority(mv.getAuthority());
				me.setConfidence(mv.getConfidence());
			}
			metadata.add(me);
		}
		resource.setMetadata(metadata);
		return resource;
	}

	@Override
	public M toModel(R obj) {
		return null;
	}

	public boolean supportsModel(BrowsableDSpaceObject object) {
		return object != null && object.getClass().equals(getModelClass());
	}

	protected abstract R newInstance();

	protected abstract Class<M> getModelClass();

}