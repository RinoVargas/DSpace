/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.dspace.app.rest.model.AuthorityRest;
import org.dspace.content.authority.factory.ContentAuthorityServiceFactory;
import org.dspace.content.authority.service.ChoiceAuthorityService;
import org.dspace.content.authority.service.MetadataAuthorityService;
import org.springframework.stereotype.Component;

/**
 * Utility methods to expose the authority framework over REST
 * 
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 * 
 */
@Component
public class AuthorityUtils {

	private ChoiceAuthorityService cas = ContentAuthorityServiceFactory.getInstance().getChoiceAuthorityService();
	private MetadataAuthorityService mas = ContentAuthorityServiceFactory.getInstance().getMetadataAuthorityService();

	public AuthorityRest getAuthority(String schema, String element, String qualifier) {
		AuthorityRest authorityRest = new AuthorityRest();
		if (mas.isAuthorityControlled(standardize(schema, element, qualifier, "_"))) {
			authorityRest.setName(cas.getChoiceAuthorityName(schema, element, qualifier));
			authorityRest.setHierarchical(cas.isHierarchical(schema, element, qualifier));
			authorityRest.setScrollable(cas.isScrollable(schema, element, qualifier));
		}
		return authorityRest;
	}

	public AuthorityRest getAuthority(String name) {		
		String metadata = cas.getChoiceMetadatabyAuthorityName(name);
		if(StringUtils.isNotBlank(metadata)) {
			String[] tokens = tokenize(metadata);
			String schema = tokens[0];
			String element = tokens[1];
			String qualifier = tokens[2];
			AuthorityRest authorityRest = new AuthorityRest();
			if (mas.isAuthorityControlled(standardize(schema, element, qualifier, "_"))) {
				authorityRest.setName(name);
				authorityRest.setHierarchical(cas.isHierarchical(schema, element, qualifier));
				authorityRest.setScrollable(cas.isScrollable(schema, element, qualifier));
			}
			return authorityRest;
		}
		return null;
	}

	public List<AuthorityRest> getAuthorities() {
		Set<String> names = cas.getChoiceAuthoritiesNames();
		List<AuthorityRest> authorities = new ArrayList<AuthorityRest>();
		for (String name : names) {
			authorities.add(getAuthority(name));
		}
		return authorities;
	}

	private String[] tokenize(String metadata) {
		String separator = metadata.contains("_") ? "_" : ".";
		StringTokenizer dcf = new StringTokenizer(metadata, separator);

		String[] tokens = { "", "", "" };
		int i = 0;
		while (dcf.hasMoreTokens()) {
			tokens[i] = dcf.nextToken().trim();
			i++;
		}
		// Tokens contains:
		// schema = tokens[0];
		// element = tokens[1];
		// qualifier = tokens[2];
		return tokens;

	}

	private String standardize(String schema, String element, String qualifier, String separator) {
		if (StringUtils.isBlank(qualifier)) {
			return schema + separator + element;
		} else {
			return schema + separator + element + separator + qualifier;
		}
	}
}