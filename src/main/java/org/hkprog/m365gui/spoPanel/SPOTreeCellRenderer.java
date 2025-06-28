package org.hkprog.m365gui.spoPanel;

import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.hkprog.m365gui.MyColor;

/**
 *
 * @author peter
 */
public class SPOTreeCellRenderer extends DefaultTreeCellRenderer {

	private ImageIcon spoIcon;
	private ImageIcon siteIcon;

	public SPOTreeCellRenderer() {
		java.net.URL iconUrl = getClass().getResource("/m365icon/sharepointOnline.png");
		spoIcon = new ImageIcon(iconUrl);
		java.net.URL siteIconUrl = getClass().getResource("/m365icon/site.png");
		siteIcon = new ImageIcon(siteIconUrl);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		if (sel) {
			setBackgroundSelectionColor(MyColor.selectedBackground);
			setBackground(MyColor.selectedBackground);
			setTextSelectionColor(Color.BLACK);
			setForeground(Color.BLACK);
		}

		if (value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object userObject = node.getUserObject();
			if (userObject instanceof SiteInfo) {
				SiteInfo siteInfo = (SiteInfo) userObject;

				// Set icon for site nodes
				if (spoIcon != null) {
					if (siteInfo.type.equals("site")) {
						setIcon(siteIcon);
					} else {
						setIcon(spoIcon);
					}
				}

				// Create HTML text with colored title and URL
				String htmlText = "<html><span style='color: black; font-weight: bold;'>"
						+ escapeHtml(siteInfo.title) + "</span><br>"
						+ "<span style='color: #999; font-size: 10px;'>"
						+ escapeHtml(siteInfo.url) + "</span></html>";
				setText(htmlText);
			} else {
				// For category nodes (Team Sites, Communication Sites, etc.)
				if (spoIcon != null && !node.isRoot()) {
					setIcon(spoIcon);
				}
			}
		}

		return this;
	}

	private String escapeHtml(String text) {
		if (text == null) {
			return "";
		}
		return text.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
	}
}
