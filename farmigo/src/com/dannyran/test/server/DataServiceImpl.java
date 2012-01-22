package com.dannyran.test.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import com.dannyran.test.client.DataService;
import com.dannyran.test.shared.StoreLocation;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DataServiceImpl extends RemoteServiceServlet implements DataService {

	@Override
	public void addLocationEntity(String address, String city, double lng, double lat) {
		StoreLocationHelper.createLocationEntity(address, city, lng, lat);
	}

	@Override
	public List<StoreLocation> getStoreLocations(double latitude, double longitude, int distance) {
		return StoreLocationHelper.getStoreLocations(latitude, longitude, distance);
	}

	@Override
	public String locationAutoComplete(String query) {
		URLFetchService urlFetchService = URLFetchServiceFactory.getURLFetchService();
		URL url;
		try {
			query = URLEncoder.encode(query, "UTF-8");
			url = new URL("https://maps.googleapis.com/maps/api/place/autocomplete/json?sensor=false&input=" + query
					+ "&language=en&types=geocode&key=AIzaSyBH5jAZ9xAFEx9BYCXATH3LTEzwyUIdDtY");
			HTTPResponse response = urlFetchService.fetch(url);
			return new String(response.getContent(), "UTF-8");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

}
