package org.hkprog.m365gui;

/**
 *
 * @author Peter <peter@quantr.hk>
 */
public class TreeNodeData {

	String name;
	String iconPath;
	String command;

	TreeNodeData(String name, String iconPath, String command) {
		this.name = name;
		this.iconPath = iconPath;
		this.command = command;
	}

	public String toString() {
		return name;
	}
}
