package org.hkprog.m365gui.spoPanel;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.hkprog.m365gui.MainFrame;
import org.hkprog.m365gui.MyLib;

/**
 *
 * @author peter
 */
public class SPOPanel extends javax.swing.JPanel {

	/**
	 * Creates new form SPOPanel
	 */
	public SPOPanel() {
		initComponents();
		
		// Set up custom tree cell renderer
		jTree1.setCellRenderer(new SPOTreeCellRenderer());

		// Show loading screen initially
		java.awt.CardLayout cardLayout = (java.awt.CardLayout) getLayout();
		cardLayout.show(this, "loadingCard");

		// Load sites in background
		loadSitesInBackground();
	}

	/**
	 * Loads SPO sites in background and updates the tree
	 */
	private void loadSitesInBackground() {
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {

				try {
					// Get the JSON data from M365 CLI
					String json = MyLib.run(MainFrame.setting.m365Path + " spo site list --output json");

					// Create root node for the tree
					DefaultMutableTreeNode root = new DefaultMutableTreeNode("SharePoint Online Sites");

					if (json != null && !json.trim().isEmpty()) {
						// Parse JSON array
						JSONArray sitesArray = new JSONArray(json);

						// Create lists to collect and sort sites
						List<SiteInfo> teamSitesList = new ArrayList<>();
						List<SiteInfo> communicationSitesList = new ArrayList<>();
						List<SiteInfo> otherSitesList = new ArrayList<>();

						// Process each site
						for (int i = 0; i < sitesArray.length(); i++) {
							JSONObject site = sitesArray.getJSONObject(i);

							// Extract site information
							String title = site.optString("Title", "Untitled Site");
							String url = site.optString("Url", "");
							String template = site.optString("Template", "");

							// Create a site info object
							SiteInfo siteInfo = new SiteInfo(title, url, template, "site");

							// Categorize sites based on template
							if (template.contains("SITEPAGEPUBLISHING")) {
								communicationSitesList.add(siteInfo);
							} else if (template.contains("STS") || template.contains("GROUP")) {
								teamSitesList.add(siteInfo);
							} else {
								otherSitesList.add(siteInfo);
							}
						}

						// Sort sites by title
						Comparator<SiteInfo> titleComparator = (s1, s2) -> s1.title.compareToIgnoreCase(s2.title);
						Collections.sort(teamSitesList, titleComparator);
						Collections.sort(communicationSitesList, titleComparator);
						Collections.sort(otherSitesList, titleComparator);

						// Create tree nodes from sorted lists
						DefaultMutableTreeNode teamSites = new DefaultMutableTreeNode("Team Sites");
						DefaultMutableTreeNode communicationSites = new DefaultMutableTreeNode("Communication Sites");
						DefaultMutableTreeNode otherSites = new DefaultMutableTreeNode("Other Sites");

						// Add sorted team sites
						for (SiteInfo siteInfo : teamSitesList) {
							DefaultMutableTreeNode siteNode = new DefaultMutableTreeNode(siteInfo);
							teamSites.add(siteNode);
						}

						// Add sorted communication sites
						for (SiteInfo siteInfo : communicationSitesList) {
							DefaultMutableTreeNode siteNode = new DefaultMutableTreeNode(siteInfo);
							communicationSites.add(siteNode);
						}

						// Add sorted other sites
						for (SiteInfo siteInfo : otherSitesList) {
							DefaultMutableTreeNode siteNode = new DefaultMutableTreeNode(siteInfo);
							otherSites.add(siteNode);
						}

						// Add categories to root (only if they have children)
						if (teamSites.getChildCount() > 0) {
							root.add(teamSites);
						}
						if (communicationSites.getChildCount() > 0) {
							root.add(communicationSites);
						}
						if (otherSites.getChildCount() > 0) {
							root.add(otherSites);
						}
					} else {
						// No sites found or error
						root.add(new DefaultMutableTreeNode("No sites found"));
					}

					// Update the tree model on EDT
					javax.swing.SwingUtilities.invokeLater(() -> {
						jTree1.setModel(new DefaultTreeModel(root));
						// Expand root and first level
						jTree1.expandRow(0);
						for (int i = 1; i < jTree1.getRowCount(); i++) {
							jTree1.expandRow(i);
						}
					});

				} catch (Exception ex) {
					// Handle errors
					DefaultMutableTreeNode root = new DefaultMutableTreeNode("SharePoint Online Sites");
					root.add(new DefaultMutableTreeNode("Error loading sites: " + ex.getMessage()));

					javax.swing.SwingUtilities.invokeLater(() -> {
						jTree1.setModel(new DefaultTreeModel(root));
						jTree1.expandRow(0);
					});
				}
				return null;
			}

			@Override
			protected void done() {
				// Switch to main view after loading
				java.awt.CardLayout cardLayout = (java.awt.CardLayout) getLayout();
				cardLayout.show(SPOPanel.this, "mainCard");
			}
		};
		worker.execute();
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
	 * Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jPanel2 = new javax.swing.JPanel();

        setLayout(new java.awt.CardLayout());

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel1.setText("Loading");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(120, 120, 120)
                .addComponent(jLabel1)
                .addContainerGap(704, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addComponent(jLabel1)
                .addContainerGap(530, Short.MAX_VALUE))
        );

        add(jPanel3, "loadingCard");

        jSplitPane1.setDividerLocation(450);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(jTree1);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jSplitPane1.setLeftComponent(jPanel1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 434, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 645, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(jPanel2);

        add(jSplitPane1, "mainCard");
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables


}
