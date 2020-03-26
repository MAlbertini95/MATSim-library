package org.matsim.Analysis;

/**
* @author ikaddoura
*/

public class ODRelation {

	private final String odId;
	private final String origin;
	private final String destination;
	private double trips;

	public ODRelation(String odId, String origin, String destination) {
		this.odId = odId;
		this.origin = origin;
		this.destination = destination;
		this.trips = 1;
	}

	public String getOdId() {
		return odId;
	}

	public String getOrigin() {
		return origin;
	}

	public String getDestination() {
		return destination;
	}

	public Double getTrips() {
		return trips;
	}

	public void setTrips(double trips) {
		this.trips = trips;
	}

}