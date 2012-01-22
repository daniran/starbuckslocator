package com.dannyran.test.client;

import java.util.List;

import com.dannyran.test.shared.StoreLocation;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("data")
public interface DataService extends RemoteService {

	/**
	 * Creat and store a {@link StoreLocation}
	 * 
	 * @param address
	 * @param city
	 * @param lng
	 * @param lat
	 */
	void addLocationEntity(String address, String city, double lng, double lat);

	/**
	 * Get a list of {@link StoreLocation}s in the specified distance from the specififed location
	 * 
	 * @param latitude
	 * @param longitude
	 * @param distance
	 * @return
	 */
	List<StoreLocation> getStoreLocations(double latitude, double longitude, int distance);

	/**
	 * Send a Places API auto-complete resuest
	 * 
	 * @param query
	 * @return
	 */
	String locationAutoComplete(String query);
}
