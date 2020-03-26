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

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

public class PersonGeoTransformatorTask implements PersonSinkSource {

	private PersonSink sink;
	
	private CoordinateTransformation transformation;
	
	public PersonGeoTransformatorTask(String from, String to) {
		this.transformation = TransformationFactory.getCoordinateTransformation(from, to);
	}

	@Override
	public void complete() {
		sink.complete();
	}

	@Override
	public void process(Person person) {
		Plan plan = person.getPlans().get(0);
		for (PlanElement planElement : plan.getPlanElements()) {
			if (planElement instanceof Activity) {
				Activity activity = (Activity) planElement;
				Coord oldCoord = activity.getCoord();
				Coord newCoord = transformation.transform(oldCoord);
				activity.setCoord(newCoord);
			} 
		}
		sink.process(person);
	}

	@Override
	public void setSink(PersonSink sink) {
		this.sink = sink;
	}

}