package org.hkprog.m365gui.spoPanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.JOptionPane;
import org.json.JSONArray;
import org.json.JSONObject;
import org.hkprog.m365gui.MainFrame;
import org.hkprog.m365gui.MyLib;
import org.hkprog.m365gui.spoPanel.dialog.CreateSiteDialog;

/**
 *
 * @author peter
 */
public class SPOPanel extends javax.swing.JPanel {

	private List<SiteInfo> allSites = new ArrayList<>();
	private DefaultMutableTreeNode originalRoot;
	String rootSiteUrl;

	/**
	 * Creates new form SPOPanel
	 */
	public SPOPanel() {
		initComponents();

		// Set up custom tree cell renderer
		siteTree.setCellRenderer(new SPOTreeCellRenderer());

		// Add filter functionality
		setupFilterTextField();

		// Show loading screen initially
		java.awt.CardLayout cardLayout = (java.awt.CardLayout) getLayout();
		cardLayout.show(this, "loadingCard");

		loadSitesInBackground();
	}

	/**
	 * Sets up the filter text field with document listener
	 */
	private void setupFilterTextField() {
		filterTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				filterTree();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				filterTree();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				filterTree();
			}
		});
	}

	/**
	 * Filters the tree based on the filter text field content
	 */
	private void filterTree() {
		String filterText = filterTextField.getText().toLowerCase().trim();

		if (filterText.isEmpty()) {
			// Show all sites if filter is empty
			siteTree.setModel(new DefaultTreeModel(originalRoot));
		} else {
			// Create filtered tree
			DefaultMutableTreeNode filteredRoot = new DefaultMutableTreeNode("SharePoint Online Sites");

			DefaultMutableTreeNode teamSites = new DefaultMutableTreeNode("Team Sites");
			DefaultMutableTreeNode communicationSites = new DefaultMutableTreeNode("Communication Sites");
			DefaultMutableTreeNode otherSites = new DefaultMutableTreeNode("Other Sites");

			// Filter sites based on title or URL
			for (SiteInfo siteInfo : allSites) {
				if (siteInfo.title.toLowerCase().contains(filterText)
						|| siteInfo.url.toLowerCase().contains(filterText)) {

					DefaultMutableTreeNode siteNode = new DefaultMutableTreeNode(siteInfo);

					// Add to appropriate category
					if (siteInfo.template.contains("SITEPAGEPUBLISHING")) {
						communicationSites.add(siteNode);
					} else if (siteInfo.template.contains("STS") || siteInfo.template.contains("GROUP")) {
						teamSites.add(siteNode);
					} else {
						otherSites.add(siteNode);
					}
				}
			}

			// Add categories to filtered root (only if they have children)
			if (teamSites.getChildCount() > 0) {
				filteredRoot.add(teamSites);
			}
			if (communicationSites.getChildCount() > 0) {
				filteredRoot.add(communicationSites);
			}
			if (otherSites.getChildCount() > 0) {
				filteredRoot.add(otherSites);
			}

			siteTree.setModel(new DefaultTreeModel(filteredRoot));
		}

		// Expand all nodes
		siteTree.expandRow(0);
		for (int i = 1; i < siteTree.getRowCount(); i++) {
			siteTree.expandRow(i);
		}
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
					String json = MyLib.run(MainFrame.setting.m365Path + " spo tenant settings list --output json");
					JSONObject tenantSettings = new JSONObject(json);
					rootSiteUrl = tenantSettings.optString("RootSiteUrl", "");

					// Get the JSON data from M365 CLI
					json = MyLib.run(MainFrame.setting.m365Path + " spo site list --output json");

					// Create root node for the tree
					DefaultMutableTreeNode root = new DefaultMutableTreeNode("SharePoint Online Sites");

					if (json != null && !json.trim().isEmpty()) {
						// Parse JSON array
						JSONArray sitesArray = new JSONArray(json);

						// Clear previous sites and create lists to collect and sort sites
						allSites.clear();
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
							allSites.add(siteInfo); // Store for filtering

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
						originalRoot = root; // Store original root for filtering
						siteTree.setModel(new DefaultTreeModel(root));
						// Expand root and first level
						siteTree.expandRow(0);
						for (int i = 1; i < siteTree.getRowCount(); i++) {
							siteTree.expandRow(i);
						}
					});

				} catch (Exception ex) {
					// Handle errors
					DefaultMutableTreeNode root = new DefaultMutableTreeNode("SharePoint Online Sites");
					root.add(new DefaultMutableTreeNode("Error loading sites: " + ex.getMessage()));

					javax.swing.SwingUtilities.invokeLater(() -> {
						siteTree.setModel(new DefaultTreeModel(root));
						siteTree.expandRow(0);
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        siteTree = new javax.swing.JTree();
        jPanel4 = new javax.swing.JPanel();
        filterTextField = new javax.swing.JTextField();
        createSiteButton = new javax.swing.JButton();
        deleteSiteButton = new javax.swing.JButton();
        refreshTreeButton = new javax.swing.JButton();
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

        siteTree.setModel(null);
        siteTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                siteTreeMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(siteTree);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        filterTextField.setPreferredSize(new java.awt.Dimension(200, 26));
        jPanel4.add(filterTextField);

        createSiteButton.setText("Create");
        createSiteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createSiteButtonActionPerformed(evt);
            }
        });
        jPanel4.add(createSiteButton);

        deleteSiteButton.setText("Del");
        deleteSiteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSiteButtonActionPerformed(evt);
            }
        });
        jPanel4.add(deleteSiteButton);

        refreshTreeButton.setText("Refresh");
        refreshTreeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshTreeButtonActionPerformed(evt);
            }
        });
        jPanel4.add(refreshTreeButton);

        jPanel1.add(jPanel4, java.awt.BorderLayout.PAGE_START);

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

    private void siteTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_siteTreeMouseClicked
		if (evt.getClickCount() == 2) {
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) siteTree.getLastSelectedPathComponent();
			if (selectedNode != null && selectedNode.getUserObject() instanceof SiteInfo) {
				SiteInfo siteInfo = (SiteInfo) selectedNode.getUserObject();
				String siteUrl = siteInfo.url;
				System.out.println("siteUrl=" + siteUrl);
				SPOSiteDetailPanel siteDetailPanel = new SPOSiteDetailPanel(siteInfo.title, rootSiteUrl, siteUrl);
				jSplitPane1.setRightComponent(siteDetailPanel);
			}
		}
    }//GEN-LAST:event_siteTreeMouseClicked

    private void createSiteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createSiteButtonActionPerformed
        showCreateSiteDialog();
    }//GEN-LAST:event_createSiteButtonActionPerformed

    private void refreshTreeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshTreeButtonActionPerformed
        loadSitesInBackground();
    }//GEN-LAST:event_refreshTreeButtonActionPerformed

    private void deleteSiteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSiteButtonActionPerformed
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) siteTree.getLastSelectedPathComponent();
        if (selectedNode == null || !(selectedNode.getUserObject() instanceof SiteInfo)) {
            JOptionPane.showMessageDialog(this, "Please select a site to delete.", "No Site Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        SiteInfo siteInfo = (SiteInfo) selectedNode.getUserObject();
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete site: " + siteInfo.title + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String command = MainFrame.setting.m365Path + " spo site remove -f --url \"" + siteInfo.url + "\"";
                    System.out.println("Deleting site with command: " + command);
                    String result = MyLib.run(command);
                    System.out.println("Site delete result: " + result);
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(SPOPanel.this, "Site '" + siteInfo.title + "' deleted successfully!", "Site Deleted", JOptionPane.INFORMATION_MESSAGE);
                        loadSitesInBackground();
                    });
                } catch (Exception ex) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(SPOPanel.this, "Error deleting site: " + ex.getMessage(), "Delete Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
        };
        worker.execute();
    }//GEN-LAST:event_deleteSiteButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createSiteButton;
    private javax.swing.JButton deleteSiteButton;
    private javax.swing.JTextField filterTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JButton refreshTreeButton;
    private javax.swing.JTree siteTree;
    // End of variables declaration//GEN-END:variables

	/**
	 * Shows dialog for creating a new SharePoint site
	 */
	private void showCreateSiteDialog() {
		CreateSiteDialog dialog = new CreateSiteDialog(null, true);
		dialog.setVisible(true);
		
		if (dialog.isOkPressed()) {
			String title = dialog.getSiteTitle();
			String alias = dialog.getAlias();
			String type = dialog.getSiteType();
			String template = dialog.getTemplate();
			String description = dialog.getDescription();
			String owner = dialog.getOwnerEmail();
			
			createSiteInBackground(title, alias, type, template, description, owner);
		}
	}
	
	/**
	 * Creates a SharePoint site in background using M365 CLI
	 */
	private void createSiteInBackground(String title, String alias, String type, String template, String description, String owners) {
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				try {
					String command;
					
					if ("CommunicationSite".equals(type)) {
						// Create Communication Site
						command = MainFrame.setting.m365Path + " spo site add" +
								" --type CommunicationSite" +
								" --title \"" + title + "\"" +
								" --alias \"" + alias + "\"" +
								" --shareByEmailEnabled" +
								" --siteDesign " + template;
						
						if (!description.isEmpty()) {
							command += " --description \"" + description + "\"";
						}
					} else {
						// Create Team Site
						command = MainFrame.setting.m365Path + " spo site add" +
								" --type TeamSite" +
								" --title \"" + title + "\"" +
								" --alias \"" + alias + "\"" +
								" --owners \"" + owners + "\"" +
								" --isPublic false";
						
						if (!description.isEmpty()) {
							command += " --description \"" + description + "\"";
						}
					}
					
					System.out.println("Creating site with command: " + command);
					String result = MyLib.run(command);
					System.out.println("Site creation result: " + result);
					
					// Show success message
					javax.swing.SwingUtilities.invokeLater(() -> {
						JOptionPane.showMessageDialog(SPOPanel.this, 
							"Site '" + title + "' created successfully!", 
							"Site Created", 
							JOptionPane.INFORMATION_MESSAGE);
						
						// Refresh the site list
						loadSitesInBackground();
					});
					
				} catch (Exception ex) {
					// Show error message
					javax.swing.SwingUtilities.invokeLater(() -> {
						JOptionPane.showMessageDialog(SPOPanel.this, 
							"Error creating site: " + ex.getMessage(), 
							"Creation Error", 
							JOptionPane.ERROR_MESSAGE);
					});
				}
				return null;
			}
		};
		worker.execute();
	}

}
