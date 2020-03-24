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

package org.matsim.Population;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;

/**
 * @author  MAlbertini
 *
 */
public class PrepareCarSimPlans {
public static void main(String[] args) {
	Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
	Scenario scenario2 = ScenarioUtils.createScenario(ConfigUtils.createConfig());
	new PopulationReader(scenario).readFile("scenarios/Car2AT/Trento_SelectedATaxi_plans.xml.gz");
	
	for (Person p : scenario.getPopulation().getPersons().values()){
		Plan plan = p.getSelectedPlan();
		Leg leg = (Leg) plan.getPlanElements().get(0);
		if (leg.getMode().equals("ATaxi")){
			scenario2.getPopulation().addPerson(p);
		} else if (leg.getMode().equals("car")){
			leg.setRoute(null);
			leg.setMode("ATaxi");
			Person p2 = scenario2.getPopulation().getFactory().createPerson(Id.createPersonId(p.getId().toString()));
			p2.addPlan(plan);
			scenario2.getPopulation().addPerson(p2);
		}
	}
	new PopulationWriter(scenario2.getPopulation()).write("scenarios/Car2AT/Trento_plansOnlyATaxi.xml.gz");

}
}