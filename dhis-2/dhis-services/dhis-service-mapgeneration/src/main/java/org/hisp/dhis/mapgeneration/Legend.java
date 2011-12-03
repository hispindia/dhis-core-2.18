package org.hisp.dhis.mapgeneration;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

/**
 * A legend is a graphical presentation of data contained in a map layer. This
 * class works as helper for LegendSet when it comes to drawing the actual
 * legend using java graphics. A legend has a height, but the actual width is
 * not defined.
 * 
 * @author Kristin Simonsen <krissimo@ifi.uio.no>
 * @author Kjetil Andresen <kjetil.andrese@gmail.com>
 */
public class Legend {

	public static final Font TITLE_FONT = new Font("title", Font.BOLD, 15);
	public static final Font PLAIN_FONT = new Font("plain", Font.PLAIN, 13);

	private InternalMapLayer mapLayer;
	private List<LegendItem> legendItems;
	
	private static final int HEADER_HEIGHT = 50;
	
	public Legend(InternalMapLayer mapLayer) {
		this.mapLayer = mapLayer;
		this.legendItems = new LinkedList<LegendItem>();

		for (Interval interval : mapLayer.getIntervalSet().getAllIntervals()) {
			addLegendItem(new LegendItem(interval));
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.setFont(TITLE_FONT);
		g.drawString(mapLayer.getName(), 0, 15);
		g.setFont(PLAIN_FONT);
		g.drawString(mapLayer.getPeriod().getStartDateString() + "", 0, 35);

		g.translate(0, HEADER_HEIGHT);

		for (LegendItem legendItem : legendItems) {
			legendItem.draw(g);
			g.translate(0, legendItem.getHeight());
		}
	}

	public int getHeight() {
		int height = 0;

		for (LegendItem legendItem : legendItems) {
			height += legendItem.getHeight();
		}

		return HEADER_HEIGHT + height;
	}

	public List<LegendItem> getLegendItems() {
		return legendItems;
	}

	public void addLegendItem(LegendItem legendItem) {
		legendItems.add(legendItem);
	}
}
