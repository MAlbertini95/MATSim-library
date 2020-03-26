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

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;

public class CreateCommuterDemand {
	private CreateCommuterDemand(){}

	private static final String NETWORK_FILENAME = "../../shared-svn/studies/countries/de/prognose_2025/osm_zellen/motorway_germany.xml";

	private static final String LANDKREISE = "../../shared-svn/studies/countries/de/prognose_2025/osm_zellen/landkreise.shp";

	private static final String OUTPUT_POPULATION_FILENAME = "../../detailedEval/pop/pendlerVerkehr/pendlermatrizen/inAndOut/pendlerverkehr_10pct_scaledAndMode_workStartingTimePeak0800Var2h_dhdn_gk4.xml.gz";

	public static void main(String[] args) {
		Scenario osmNetwork = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		new MatsimNetworkReader(osmNetwork.getNetwork()).readFile(NETWORK_FILENAME);
		PersonVerschmiererTask personVerschmiererTask = new PersonVerschmiererTask(LANDKREISE);	
		PopulationWriterTask populationWriter = new PopulationWriterTask(OUTPUT_POPULATION_FILENAME, osmNetwork.getNetwork());
		PopulationGenerator populationBuilder = new PopulationGenerator();		
		TravelTimeToWorkCalculator routerFilter = new TravelTimeToWorkCalculator(osmNetwork.getNetwork());
		CommuterMatrixReader odMatrixReader = new CommuterMatrixReader(LANDKREISE);
		odMatrixReader.setFlowSink(routerFilter);
		routerFilter.setSink(populationBuilder);
		populationBuilder.setSink(personVerschmiererTask);
		personVerschmiererTask.setSink(populationWriter);		
		odMatrixReader.run();
		System.err.println("some zones do not work because of territorial change; check!") ;
	}

}