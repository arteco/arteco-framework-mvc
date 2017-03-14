package com.arteco.mvc.model;

import org.apache.commons.lang.StringUtils;

/**
 * Created by rarnau on 11/11/16.
 * Arteco Consulting Sl.
 * mailto: info@arteco-consulting.com
 */
public enum RequestVerb {
	GET, POST;

	public boolean is(String method) {
		return StringUtils.endsWithIgnoreCase(this.name(),method);
	}
}
