package com.ihooyah.roadgis.utils.coodinate;

import java.io.Serializable;

public class HYCoodinates extends Object implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public double latitude;
	public double longitude;

	public HYCoodinates() {

	}

	public HYCoodinates(double lat, double lon) {
		this.latitude = lat;
		this.longitude = lon;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}
