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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.PersonLeavesVehicleEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.api.core.v01.events.handler.PersonLeavesVehicleEventHandler;
import org.matsim.api.core.v01.population.Person;

import org.matsim.trento.AVanalysis.LinkFinder;
import org.matsim.trento.AVanalysis.PassengerTracker;

import ch.ethz.matsim.av.data.AVOperator;

import org.matsim.trento.AVanalysis.AVUtils;

public class PassengerAnalysisListener implements PersonDepartureEventHandler, PersonArrivalEventHandler,
		LinkEnterEventHandler, PersonEntersVehicleEventHandler, PersonLeavesVehicleEventHandler {
	private final LinkFinder linkFinder;
	private final PassengerTracker passengers = new PassengerTracker();

	private final List<PassengerRideItem> rides = new LinkedList<>();
	private final Map<Id<Person>, PassengerRideItem> currentRides = new HashMap<>();

	public PassengerAnalysisListener(LinkFinder linkFinder) {
		this.linkFinder = linkFinder;
	}

	@Override
	public void handleEvent(PersonDepartureEvent event) {
		if (!event.getPersonId().toString().startsWith("AT_")) {
			if (event.getLegMode().equals("ATaxi")) {
				PassengerRideItem ride = new PassengerRideItem();
				rides.add(ride);

				ride.personId = event.getPersonId();

				ride.departureTime = event.getTime();
				ride.originLink = linkFinder.getLink(event.getLinkId());

				currentRides.put(event.getPersonId(), ride);
			}
		}
	}

	@Override
	public void handleEvent(LinkEnterEvent event) {
		if (event.getVehicleId().toString().startsWith("AT_")) {
			double distance = linkFinder.getDistance(event.getLinkId());

			for (Id<Person> passengerId : passengers.getPassengerIds(event.getVehicleId())) {
				PassengerRideItem ride = currentRides.get(passengerId);

				if (ride == null) {
					throw new IllegalStateException("Found vehicle enter link without departure");
				}

				ride.distance += distance;
			}
		}
	}

	@Override
	public void handleEvent(PersonEntersVehicleEvent event) {
		if (!event.getPersonId().toString().startsWith("AT_")) {
			if (event.getVehicleId().toString().startsWith("AT_")) {
				PassengerRideItem ride = currentRides.get(event.getPersonId());

				if (ride == null) {
					throw new IllegalStateException("Found vehicle enter event without departure");
				}

//				ride.operatorId = AVOperator.createId("default");
				ride.vehicleId = event.getVehicleId();

				ride.waitingTime = event.getTime() - ride.departureTime;

				passengers.addPassenger(event.getVehicleId(), event.getPersonId());
			}
		}
	}

	@Override
	public void handleEvent(PersonLeavesVehicleEvent event) {
		if (!event.getPersonId().toString().startsWith("AT_")) {
			if (event.getVehicleId().toString().startsWith("AT_")) {
				passengers.removePassenger(event.getVehicleId(), event.getPersonId());
			}
		}
	}

	@Override
	public void handleEvent(PersonArrivalEvent event) {
		if (!event.getPersonId().toString().startsWith("AT_")) {
			PassengerRideItem ride = currentRides.remove(event.getPersonId());

			if (ride != null) {
				ride.arrivalTime = event.getTime();
				ride.destinationLink = linkFinder.getLink(event.getLinkId());
			}
		}
	}

	@Override
	public void reset(int iteration) {
		passengers.clear();
		rides.clear();
		currentRides.clear();
	}

	public List<PassengerRideItem> getRides() {
		return rides;
	}
}