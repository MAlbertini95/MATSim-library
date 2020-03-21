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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.vehicles.Vehicle;

public class PassengerTracker {
	private final Map<Id<Vehicle>, Set<Id<Person>>> passengers = new HashMap<>();

	private void ensurePassengers(Id<Vehicle> vehicleId) {
		if (!passengers.containsKey(vehicleId)) {
			passengers.put(vehicleId, new HashSet<>());
		}
	}

	public boolean hasPassenger(Id<Vehicle> vehicleId, Id<Person> passengerId) {
		ensurePassengers(vehicleId);
		return passengers.get(vehicleId).contains(passengerId);
	}

	public void addPassenger(Id<Vehicle> vehicleId, Id<Person> passengerId) {
		ensurePassengers(vehicleId);

		if (!passengers.get(vehicleId).add(passengerId)) {
			throw new IllegalStateException(
					String.format("Passenger '%s' is already in vehicle '%s'", passengerId, vehicleId));
		}
	}

	public void removePassenger(Id<Vehicle> vehicleId, Id<Person> passengerId) {
		ensurePassengers(vehicleId);

		if (!passengers.get(vehicleId).remove(passengerId)) {
			throw new IllegalStateException(
					String.format("Passenger '%s' is not in vehicle '%s'", passengerId, vehicleId));
		}
	}

	public int getNumberOfPassengers(Id<Vehicle> vehicleId) {
		ensurePassengers(vehicleId);
		return passengers.get(vehicleId).size();
	}

	public Collection<Id<Person>> getPassengerIds(Id<Vehicle> vehicleId) {
		ensurePassengers(vehicleId);
		return passengers.get(vehicleId);
	}

	public void clear() {
		for (Set<Id<Person>> passengerList : passengers.values()) {
			passengerList.clear();
		}
	}
}