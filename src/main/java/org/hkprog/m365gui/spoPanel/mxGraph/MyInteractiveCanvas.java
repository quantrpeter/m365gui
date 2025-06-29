package org.hkprog.m365gui.spoPanel.mxGraph;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.view.mxCellState;

public class MyInteractiveCanvas extends mxInteractiveCanvas {

	protected JLabel jLabel = new JLabel();

	protected mxGraphComponent graphComponent;

	Color borderColor = new Color(0, 102, 255);
	Color backgroundcolor = new Color(195, 219, 255);

	public MyInteractiveCanvas(mxGraphComponent graphComponent) {
		this.graphComponent = graphComponent;

		jLabel.setBorder(BorderFactory.createLineBorder(borderColor));
		jLabel.setHorizontalAlignment(JLabel.CENTER);
		jLabel.setBackground(backgroundcolor);
		jLabel.setOpaque(true);
		jLabel.setFont(new Font("arial", Font.PLAIN, 10));
	}

	@Override
	public Object drawCell(mxCellState state) {
		Object value = state.getCell();
		String label = "";
		if (value != null && graphComponent != null && graphComponent.getGraph() != null) {
			Object userObject = graphComponent.getGraph().getModel().getValue(value);
			label = userObject != null ? userObject.toString() : value.toString();
		}
		jLabel.setText(label);
		rendererPane.paintComponent(g, jLabel, graphComponent,
			(int) (state.getX() + translate.getX()),
			(int) (state.getY() + translate.getY()),
			(int) state.getWidth(), (int) state.getHeight(), true);
		return jLabel;
	}

}
