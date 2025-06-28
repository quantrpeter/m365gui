package org.hkprog.m365gui.spoPanel;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.hkprog.m365gui.MyColor;

import javax.swing.table.DefaultTableCellRenderer;

import java.awt.Color;
import java.awt.Component;

public class MyTableCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (isSelected) {
			c.setBackground(MyColor.selectedBackground);
			c.setForeground(Color.black);
			if (hasFocus) {
				c.setFont(c.getFont().deriveFont(1));
			} else {
				c.setFont(c.getFont().deriveFont(0));
			}
		} else {

			c.setBackground(Color.white);
			c.setForeground(Color.black);
		}
		return c;
	}
}
