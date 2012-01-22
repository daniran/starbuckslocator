package com.dannyran.test.client;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dannyran.test.shared.StoreLocation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * main Panel
 */
public class StoreLocatorPanel extends Composite {

	interface StoreLocatorPanelUiBinder extends UiBinder<Widget, StoreLocatorPanel> {
		StoreLocatorPanelUiBinder uiBinder = GWT.create(StoreLocatorPanelUiBinder.class);
	}

	private class StoreLocationsCallback implements AsyncCallback<List<StoreLocation>> {
		private final LatLng point;

		private StoreLocationsCallback(LatLng point) {
			this.point = point;
		}

		@Override
		public void onFailure(Throwable caught) {
			// silently ignore
		}

		@Override
		public void onSuccess(List<StoreLocation> result) {

			// sort locations
			Collections.sort(result, new Comparator<StoreLocation>() {
				@Override
				public int compare(StoreLocation o1, StoreLocation o2) {
					LatLng location1 = LatLng.newInstance(o1.getLatitude(), o1.getLongitude());
					LatLng location2 = LatLng.newInstance(o2.getLatitude(), o2.getLongitude());
					double dist1 = location1.distanceFrom(point);
					o1.setDistance(dist1);
					double dist2 = location2.distanceFrom(point);
					o2.setDistance(dist2);
					return o1.compareTo(o2);
				}

			});

			// update provider
			listProvider.setList(result);
			pager.firstPage();
			showRange();
			if (!result.isEmpty()) {
				selectionModel.setSelected(result.get(0), true);
			}
		}
	}

	private static final String API_KEY = "AIzaSyBH5jAZ9xAFEx9BYCXATH3LTEzwyUIdDtY";

	private final Images images = GWT.create(Images.class);

	private final DataServiceAsync dataService = GWT.create(DataService.class);

	@UiField
	PushButton button;

	@UiField
	ListBox distance;

	@UiField(provided = true)
	SuggestBox location;

	@UiField
	LayoutPanel mapPanel;

	MapWidget map;

	@UiField(provided = true)
	CellList<StoreLocation> resultList;

	@UiField(provided = true)
	SimplePager pager;

	private final ListDataProvider<StoreLocation> listProvider;

	private final SingleSelectionModel<StoreLocation> selectionModel;

	private final Map<StoreLocation, Marker> markers = new HashMap<StoreLocation, Marker>();

	private Geocoder geocoder;

	public StoreLocatorPanel() {
		// init provider and list
		listProvider = new ListDataProvider<StoreLocation>();
		resultList = new CellList<StoreLocation>(new StoreLocationCell(images.starbuckSmall()), listProvider);
		listProvider.addDataDisplay(resultList);
		resultList.addRangeChangeHandler(new RangeChangeEvent.Handler() {
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				showRange();
			}
		});
		pager = new SimplePager();
		pager.setDisplay(resultList);
		selectionModel = new SingleSelectionModel<StoreLocation>();
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				StoreLocatorPanel.this.openMarker(selectionModel.getSelectedObject());
			}
		});
		resultList.setSelectionModel(selectionModel);

		// suggest box
		location = new SuggestBox(new PlacesSuggestOracle());
		location.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				String replacementString = event.getSelectedItem().getReplacementString();
				StoreLocatorPanel.this.searchLocation(replacementString);
			}
		});

		// init ui binder
		initWidget(StoreLocatorPanelUiBinder.uiBinder.createAndBindUi(this));

		// distance dropdown
		distance.addItem("1 km", "1");
		distance.addItem("5 km", "5");
		distance.addItem("10 km", "10");
		distance.addItem("15 km", "15");

		// load maps API
		Maps.loadMapsApi(API_KEY, "2", false, new Runnable() {
			@Override
			public void run() {
				drawMap();
			}
		});

	}

	@UiHandler("button")
	void onClick(ClickEvent e) {
		searchLocation(location.getText());
	}

	private void drawMap() {
		geocoder = new Geocoder();
		map = new MapWidget(LatLng.newInstance(39.51824759907753, -105.82328455636276), 4);
		map.setSize("100%", "100%");
		map.addControl(new LargeMapControl());
		mapPanel.add(map);

	}

	private void openMarker(StoreLocation location) {
		Marker marker = markers.get(location);
		if (marker != null) {
			map.getInfoWindow().open(marker.getLatLng(), new InfoWindowContent("Store Location: " + location.getAddress()));
		}
	}

	private void searchLocation(String address) {
		// 1. query geocoder for the location
		geocoder.getLatLng(address, new LatLngCallback() {

			@Override
			public void onFailure() {
				// silently ignore
			}

			@Override
			public void onSuccess(final LatLng point) {
				// 2. zoom map to point
				map.panTo(point);
				map.setZoomLevel(12);
				String value = distance.getValue(distance.getSelectedIndex());
				// 3. query for store locations
				pager.startLoading();
				dataService.getStoreLocations(point.getLatitude(), point.getLongitude(), Integer.parseInt(value),
						new StoreLocationsCallback(point));
			};
		});

	}

	private void showRange() {
		// clear
		if (map != null) {
			map.clearOverlays();
		}
		markers.clear();

		// create a marker for each location
		for (final StoreLocation storeLocation : resultList.getVisibleItems()) {
			LatLng location = LatLng.newInstance(storeLocation.getLatitude(), storeLocation.getLongitude());
			Marker marker = new Marker(location);
			markers.put(storeLocation, marker);
			marker.addMarkerClickHandler(new MarkerClickHandler() {

				@Override
				public void onClick(MarkerClickEvent event) {
					StoreLocatorPanel.this.openMarker(storeLocation);
					selectionModel.setSelected(storeLocation, true);
				}
			});
			map.addOverlay(marker);
		}
	}

}
