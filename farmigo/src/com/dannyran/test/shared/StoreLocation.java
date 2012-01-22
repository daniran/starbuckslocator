package com.dannyran.test.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class StoreLocation implements IsSerializable, Comparable<StoreLocation> {
	private final String city;
	private final String address;
	private final double latitude;
	private final double longitude;
	private double distance = 0;

	public StoreLocation(String city, String address, double latitude, double longitude) {
		this.city = city;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public int compareTo(StoreLocation o) {
		return Double.compare(this.distance, o.distance);
	}

	public String getAddress() {
		return address;
	}

	public String getCity() {
		return city;
	}

	public double getDistance() {
		return distance;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

}
