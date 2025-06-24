package org.hkprog.m365gui;

/**
 *
 * @author Peter <peter@quantr.hk>
 */
public class TreeNodeData {

	String name;
	String iconPath;

	TreeNodeData(String name, String iconPath) {
		this.name = name;
		this.iconPath = iconPath;
	}

	public String toString() {
		return name;
	}
}
