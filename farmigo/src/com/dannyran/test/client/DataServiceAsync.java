package com.dannyran.test.client;

import java.util.List;

import com.dannyran.test.shared.StoreLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DataServiceAsync {
	void addLocationEntity(String address, String city, double lng, double lat, AsyncCallback<Void> callback);

	void getStoreLocations(double latitude, double longitude, int distance, AsyncCallback<List<StoreLocation>> callback);

	void locationAutoComplete(String query, AsyncCallback<String> callback);
}
