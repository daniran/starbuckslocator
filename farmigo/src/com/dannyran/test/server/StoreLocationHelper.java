package com.dannyran.test.server;

import java.util.ArrayList;
import java.util.List;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.BoundingBox;
import com.beoui.geocell.model.Point;
import com.dannyran.test.shared.StoreLocation;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class StoreLocationHelper {
	public static final String LAT_PROP = "Lat";
	public static final String LNG_PROP = "Lng";
	public static final String GEOCELL_PROP = "geoCell";
	public static final String CITY_PROP = "city";
	public static final String ADDRESS_PROP = "address";
	public static final String STORE_LOCATION_ENTITY = "StoreLocation";

	private static final int R = 6371;

	public static Entity createLocationEntity(String address, String city, double lng, double lat) {
		Entity location = new Entity(STORE_LOCATION_ENTITY);
		location.setProperty(ADDRESS_PROP, address);
		location.setProperty(CITY_PROP, city);
		location.setProperty(LNG_PROP, lng);
		location.setProperty(LAT_PROP, lat);
		location.setProperty(GEOCELL_PROP, generateGeoCell(lng, lat));
		return location;
	}

	public static List<StoreLocation> getStoreLocations(double latitude, double longitude, int distance) {
		BoundingBox bBox = calcBBox(latitude, longitude, distance);
		List<String> cells = GeocellManager.bestBboxSearchCells(bBox, null);
		List<StoreLocation> stores = new ArrayList<StoreLocation>();
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(STORE_LOCATION_ENTITY);
		q.addFilter(GEOCELL_PROP, FilterOperator.IN, cells);
		PreparedQuery pq = ds.prepare(q);
		for (Entity result : pq.asIterable()) {
			String address = (String) result.getProperty(ADDRESS_PROP);
			String city = (String) result.getProperty(CITY_PROP);
			Double lng = (Double) result.getProperty(LNG_PROP);
			Double lat = (Double) result.getProperty(LAT_PROP);
			stores.add(new StoreLocation(city, address, lat, lng));
		}
		return stores;

	}

	/**
	 * Calculate the bounding box (NE + SW) of the given point, with the given distance
	 * 
	 * @param lat
	 * @param lng
	 * @param distance
	 *            in km
	 * @return
	 */
	private static BoundingBox calcBBox(double lat, double lng, int distance) {
		Point nePoint = calcDest(lat, lng, 45, distance);
		Point swPoint = calcDest(lat, lng, 225, distance);
		return new BoundingBox(nePoint.getLat(), nePoint.getLon(), swPoint.getLat(), swPoint.getLon());
	}

	/**
	 * Destination point given distance and bearing from start point
	 * 
	 * Given a start point, initial bearing, and distance, this will calculate the destination point and final bearing
	 * travelling along a (shortest distance) great circle arc.
	 * 
	 * taken from {@link http://www.movable-type.co.uk/scripts/latlong.html}
	 * 
	 * @param lat1
	 *            start lat
	 * @param lon1
	 *            start lon
	 * @param baring
	 * @param distance
	 *            in km
	 * @return
	 */
	private static Point calcDest(double lat1, double lon1, int baring, int distance) {
		double dist = (double) distance / R; // convert dist to angular distance in radians
		double brng = baring * Math.PI / 180; // to radians
		lat1 = lat1 * Math.PI / 180; // to radians
		lon1 = lon1 * Math.PI / 180; // to radians

		double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dist) + Math.cos(lat1) * Math.sin(dist) * Math.cos(brng));
		double lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(dist) * Math.cos(lat1), Math.cos(dist) - Math.sin(lat1) * Math.sin(lat2));
		lon2 = (lon2 + 3 * Math.PI) % (2 * Math.PI) - Math.PI; // normalise to -180..+180¼

		return new Point(lat2 * 180 / Math.PI, lon2 * 180 / Math.PI);
	}

	private static List<String> generateGeoCell(double lng, double lat) {
		return GeocellManager.generateGeoCell(new Point(lat, lng));
	}

}
