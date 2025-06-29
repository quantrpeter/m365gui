package org.hkprog.m365gui.spoPanel.mxGraph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.view.mxCellState;
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
			g2.fillRect(0, 0, getWidth(), getHeight());
			int tricker = 10;
			g2.setColor(Color.darkGray);
			for (int y = 0; y <= getWidth(); y += tricker) {
				for (int x = 0; x <= getWidth(); x += tricker) {
					g2.fillRect(x, y, 1, 1);
				}
			}
		}
	}
}
