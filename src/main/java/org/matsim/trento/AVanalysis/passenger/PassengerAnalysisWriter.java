/* *********************************************************************** *
 * project: org.matsim.*												   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.trento.AVanalysis.passenger;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class PassengerAnalysisWriter {
	private final PassengerAnalysisListener listener;

	public PassengerAnalysisWriter(PassengerAnalysisListener listener) {
		this.listener = listener;
	}

	public void writeRides(File path) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));

		writer.write(String.join(";", new String[] { //
				"person_id", //
//				"operator_id", //
				"vehicle_id", //

				"origin_link_id", //
				"origin_x", //
				"origin_y", //

				"destination_link_id", //
				"destination_x", //
				"destination_y", //

				"departure_time", //
				"arrival_time", //
				"waiting_time", //

				"distance" //
		}) + "\n");

		for (PassengerRideItem ride : listener.getRides()) {
			writer.write(String.join(";", new String[] { //
					String.valueOf(ride.personId), //
//					ride.operatorId == null ? "NaN" : String.valueOf(ride.operatorId), //
					ride.vehicleId == null ? "NaN" : String.valueOf(ride.vehicleId), //

					ride.originLink == null ? "null" : String.valueOf(ride.originLink.getId()), //
					ride.originLink == null ? "NaN" : String.valueOf(ride.originLink.getCoord().getX()), //
					ride.originLink == null ? "NaN" : String.valueOf(ride.originLink.getCoord().getY()), //

					ride.destinationLink == null ? "null" : String.valueOf(ride.destinationLink.getId()), //
					ride.destinationLink == null ? "NaN" : String.valueOf(ride.destinationLink.getCoord().getX()), //
					ride.destinationLink == null ? "NaN" : String.valueOf(ride.destinationLink.getCoord().getY()), //

					String.valueOf(ride.departureTime), //
					String.valueOf(ride.arrivalTime), //
					String.valueOf(ride.waitingTime), //

					String.valueOf(ride.distance) //
			}) + "\n");
		}

		writer.close();
	}
}