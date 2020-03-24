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

package org.matsim.Population;


import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;

/**
 * @author teoal customized dziemke.
 * Uses a given plans file and adds a stay-home plan to the plans of each person
 * The home location of the newly created stay-home plan is taken from the first plan of the agent
 */
public class AddStayHomePlan {

	// input and output files
	static String inputPlansFile = "Path to input file/plans.xml.gz";
	static String outputPlansFile = "Path to output file/plansStayHome.xml.gz";
	
	public static void main(String[] args) {
		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);
		PopulationReader reader = new PopulationReader(scenario);
		reader.readFile(inputPlansFile);
		Population population = scenario.getPopulation();

		for (Person person : population.getPersons().values()) {			
			Plan plan = person.getPlans().get(0);
			
			Activity homeActivity = (Activity) plan.getPlanElements().get(0);
			
			Plan plan2 = population.getFactory().createPlan();
			plan2.addActivity(homeActivity);			
			
			person.addPlan(plan2);		
		}
		
		// write population file
		//new PopulationWriter(scenario.getPopulation(), null).write(outputBase + "plans.xml.gz");
		new PopulationWriter(scenario.getPopulation(), null).write(outputPlansFile);
		
		System.out.println("Analysis file " + outputPlansFile + " written.");
	}
}