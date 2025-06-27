package org.hkprog.m365gui.spoPanel;

/**
 *
 * @author peter
 */
public class SiteInfo {

	final String title;
	final String url;
	final String template;
	final String type;

	SiteInfo(String title, String url, String template, String type) {
		this.title = title;
		this.url = url;
		this.template = template;
		this.type = type;
	}
}
