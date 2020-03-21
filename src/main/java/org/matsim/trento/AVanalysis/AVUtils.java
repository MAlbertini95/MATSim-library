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

package org.matsim.trento.AVanalysis;

import org.matsim.api.core.v01.Id;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;

import ch.ethz.matsim.av.data.AVOperator;

public class AVUtils {
	private AVUtils() {
	}

	public static Id<DvrpVehicle> createId(Id<AVOperator> operatorId, String suffix) {
		return Id.create(String.format("av:%s:%s", operatorId.toString(), suffix), DvrpVehicle.class);
	}

	public static Id<DvrpVehicle> createId(Id<AVOperator> operatorId, int suffix) {
		return createId(operatorId, String.valueOf(suffix));
	}

	public static Id<DvrpVehicle> createId(Id<AVOperator> operatorId, long suffix) {
		return createId(operatorId, String.valueOf(suffix));
	}

	public static Id<AVOperator> getOperatorId(String vehicleId) {
		if (!vehicleId.startsWith("AT_")) {
			throw new IllegalStateException("Not a valid AV vehicle: " + vehicleId);
		}

//		String[] segments = vehicleId.split("_");
//
//		if (segments.length != 3) {
//			throw new IllegalStateException("Not a valid AV vehicle: " + vehicleId);
//		}
//
		return AVOperator.createId("default");
	}

	public static Id<AVOperator> getOperatorId(Id<?> vehicleId) {
		return getOperatorId(vehicleId.toString());
	}
}