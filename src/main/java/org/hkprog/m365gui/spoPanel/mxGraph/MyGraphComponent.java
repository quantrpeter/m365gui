package org.hkprog.m365gui.spoPanel.mxGraph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.view.mxGraph;

public class MyGraphComponent extends mxGraphComponent {

	public MyGraphComponent(mxGraph graph) {
		super(graph);
	}

	@Override
	public mxInteractiveCanvas createCanvas() {
		return new MyInteractiveCanvas(this);
	}

	@Override
	protected void paintBackground(Graphics g) {
		double scale = this.getGraph().getView().getScale();
		// super.paintGrid(g);

		if (scale >= 0.5) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.white);
			g2.fillRect(0, 0, getGraphControl().getWidth(), getGraphControl().getHeight());
			int tricker = 10;
			g2.setColor(Color.darkGray);
			int gridWidth = getGraphControl().getWidth();
			int gridHeight = getGraphControl().getHeight();
			for (int y = 0; y <= gridHeight; y += tricker) {
				for (int x = 0; x <= gridWidth; x += tricker) {
					g2.fillRect(x, y, 1, 1);
				}
			}
		}
	}
}
