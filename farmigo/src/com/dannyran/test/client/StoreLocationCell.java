package com.dannyran.test.client;

import com.dannyran.test.shared.StoreLocation;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * A Cell showing a Store Location
 */
class StoreLocationCell extends AbstractCell<StoreLocation> {

	private final String imageHtml;

	public StoreLocationCell(ImageResource image) {
		this.imageHtml = AbstractImagePrototype.create(image).getHTML();
	}

	@Override
	public void render(Context context, StoreLocation value, SafeHtmlBuilder sb) {
		if (value == null) {
			return;
		}

		sb.appendHtmlConstant("<table class='locationCell' width='100%'>");
		sb.appendHtmlConstant("<tr><td rowspan='3'>");
		sb.appendHtmlConstant(imageHtml);
		sb.appendHtmlConstant("</td>");
		sb.appendHtmlConstant("<td style='font-size:95%;'>");
		sb.appendEscaped("Address: " + value.getAddress());
		sb.appendHtmlConstant("</td></tr><tr><td>");
		sb.appendEscaped("City: " + value.getCity());
		sb.appendHtmlConstant("</td></tr><tr><td>");
		sb.appendEscaped("Distance: " + formatDistance(value.getDistance()));
		sb.appendHtmlConstant("</td></tr></table>");
	}

	private String formatDistance(double dist) {
		int d = (int) dist;
		return d > 1000 ? d / 1000 + " km" : d + " m";
	}

}