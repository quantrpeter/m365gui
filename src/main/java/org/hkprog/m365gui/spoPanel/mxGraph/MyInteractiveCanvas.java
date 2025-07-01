package org.hkprog.m365gui.spoPanel.mxGraph;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.view.mxCellState;
import javax.swing.ImageIcon;

public class MyInteractiveCanvas extends mxInteractiveCanvas {

	protected JLabel jLabel = new JLabel();

	protected mxGraphComponent graphComponent;

	Color borderColor = new Color(0, 102, 255);
	Color backgroundcolor = new Color(195, 219, 255);
	ImageIcon siteIcon = new ImageIcon(getClass().getResource("/m365icon/site.png"));
//	Color backgroundcolor = new Color(255, 0, 0);

	public MyInteractiveCanvas(mxGraphComponent graphComponent) {
		super(graphComponent);
		this.graphComponent = graphComponent;

		jLabel.setBorder(BorderFactory.createLineBorder(borderColor));
		jLabel.setHorizontalAlignment(JLabel.CENTER);
		jLabel.setBackground(backgroundcolor);
		jLabel.setOpaque(true);
		jLabel.setFont(new Font("arial", Font.PLAIN, 20));
	}

	@Override
	public Object drawLabel(String text, mxCellState state, boolean html) {
//		return super.drawLabel(text, state, html); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
		return null;
	}

//	@Override
//	public Object drawCell(mxCellState state) {
//		Map<String, Object> style = state.getStyle();
//		mxIShape shape = getShape(style);
//
//		if (g != null && shape != null) {
//			// Creates a temporary graphics instance for drawing this shape
//			float opacity = mxUtils.getFloat(style, mxConstants.STYLE_OPACITY, 100);
//			Graphics2D previousGraphics = g;
//			g = createTemporaryGraphics(style, opacity, state);
//
//			// Paints the shape and restores the graphics object
//			shape.paintShape(this, state);
//
//			if (((mxCell) state.getCell()).isVertex()) {
//				int x = (int) (state.getCenterX() - state.getWidth() / 2);
//				int y = (int) (state.getCenterY());
//				Image img = new javax.swing.ImageIcon(getClass().getResource("/famfamfam/page_white_excel.png")).getImage();
////                Image img = Toolkit.getDefaultToolkit().getImage(<PATH TO YOUR IMAGE>);
//				previousGraphics.drawImage(img, x, y, null);
//			}
//
//			g.dispose();
//			g = previousGraphics;
//		}
//
//		return shape;
//	}
	@Override
	public Object drawCell(mxCellState state) {
		// If it is edge or null, call parent
		if (state == null || state.getCell() == null) {
			return super.drawCell(state);
		}
		// Use JGraphX API to check if cell is edge
		Object cell = state.getCell();
		if (graphComponent != null && graphComponent.getGraph() != null && graphComponent.getGraph().getModel().isEdge(cell)) {
			return super.drawCell(state);
		}
		// Get label from user object or cell value
		String label = "";
		if (graphComponent != null && graphComponent.getGraph() != null) {
			Object userObject = graphComponent.getGraph().getModel().getValue(cell);
			label = userObject != null ? userObject.toString() : cell.toString();
		}
		jLabel.setIcon(siteIcon);
		jLabel.setText(label);
		int preferredWidth = jLabel.getPreferredSize().width;
		int preferredHeight = jLabel.getPreferredSize().height;
		updateCellSizeInModel(cell, preferredWidth, preferredHeight);

		rendererPane.paintComponent(g, jLabel, graphComponent,
				(int) (state.getX() + translate.getX()),
				(int) (state.getY() + translate.getY()),
				jLabel.getPreferredSize().width, jLabel.getPreferredSize().height, true);
		return jLabel;
	}

	private void updateCellSizeInModel(Object cell, int width, int height) {
		if (graphComponent != null && graphComponent.getGraph() != null && cell != null) {
			com.mxgraph.model.mxIGraphModel model = graphComponent.getGraph().getModel();
			com.mxgraph.model.mxGeometry geometry = model.getGeometry(cell);
			if (geometry != null && (geometry.getWidth() != width || geometry.getHeight() != height)) {
				model.beginUpdate();
				try {
					geometry = (com.mxgraph.model.mxGeometry) geometry.clone();
					geometry.setWidth(width);
					geometry.setHeight(height);
					model.setGeometry(cell, geometry);
				} finally {
					model.endUpdate();
				}
			}
		}
	}

}
