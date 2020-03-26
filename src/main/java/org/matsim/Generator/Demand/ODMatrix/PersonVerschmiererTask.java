/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2016 by the members listed in the COPYING,        *
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

package org.matsim.Generator.Demand.ODMatrix;

import java.util.List;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;

public class PersonVerschmiererTask implements PersonSinkSource {

	private Verschmierer verschmierer;
	
	private PersonSink sink;
	
	int verschmiert = 0;
	int nichtVerschmiert = 0;

	public PersonVerschmiererTask(String shapeFilename) {
		this.verschmierer = new Verschmierer(shapeFilename);
	}

	@Override
	public void complete() {
		sink.complete();
	}

	@Override
	public void process(Person person) {
		Plan plan = person.getPlans().get(0);
		
		List<PlanElement> planElements = plan.getPlanElements();
		Activity home1 = (Activity) planElements.get(0);
		Activity work = (Activity) planElements.get(2);
		Activity home2 = (Activity) planElements.get(4);
		
		Coord oldCoordHome = home1.getCoord();
		Coord oldCoordWork = work.getCoord();
		
		Coord newCoordHome = verschmierer.shootIntoSameZoneOrLeaveInPlace(oldCoordHome);
		Coord newCoordWork = verschmierer.shootIntoSameZoneOrLeaveInPlace(oldCoordWork);

		home1.setCoord(newCoordHome);
		work.setCoord(newCoordWork);
		home2.setCoord(newCoordHome);
		
		sink.process(person);
	}

	@Override
	public void setSink(PersonSink sink) {
		this.sink = sink;
	}

}