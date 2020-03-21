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
package org.matsim.trento.utils;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;

/**
 * @author nagel
 *
 */
public class PopulationFilter{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( PopulationFilter.class ) ;

	public static void main(String[] args) {
		if ( args.length==0 ) {
			args = new String [] { "scenarios/Calibration/FilteringConfig.xml" } ;
			// to make sure that something is run by default; better start from MATSimGUI.
		} else {
			Gbl.assertIf( args[0] != null && !args[0].equals( "" ) );
		}
		
		
	//----
		
	Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
	new PopulationReader(scenario).readFile("scenarios/Calibration/Tn500.output_plans.xml.gz");

	for (Person person : scenario.getPopulation().getPersons().values()) {
		Plan highestScoring = person.getPlans().stream()
			// get the plan with the highest score
			.min((plan1, plan2) -> Double.compare(plan2.getScore(), plan1.getScore()))
			.orElseThrow(() -> new RuntimeException("something went wrong"));

		person.getPlans().clear();
		person.addPlan(highestScoring);
		person.setSelectedPlan(highestScoring);
	}

	new PopulationWriter(scenario.getPopulation()).write("scenarios/Calibration/CaydtsUpScaledPopulation.xml");
	
	}
	
}
