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

package org.matsim.Analysis;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.matsim.Analysis.AgentAnalysisFilter;
import org.matsim.Analysis.AnalysisAgentFilter;
import org.matsim.Analysis.MatsimAnalysis;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

public class MyAnalysisRun {
	private static final Logger log = Logger.getLogger(MyAnalysisRun.class);
			
	public static void main(String[] args) throws IOException {
			
		final String runDirectory = "C:/Users/teoal/Politecnico di Milano 1863/MAGISTRALE/Tesi/Calibrazione/Trento_Calibrated/";
		final String runId = "ModalShare";	
		
		final String modesString = "car,pt,ride,bike,walk"; //"car,pt,ride,bike,walk"
		final String shapeFileZones = "C:/Users/teoal/Politecnico di Milano 1863/MAGISTRALE/Tesi/MAAS Trento/Sez2011/TrentoSezCirc.shp";
		final String zonesCRS = TransformationFactory.WGS84;
		final String zoneId = "sez";	
		final int scalingFactor = 100;
		final String homeActivityPrefix = "home";

		final String analysisOutputDirectory = "C:/Users/teoal/Politecnico di Milano 1863/MAGISTRALE/Tesi/Calibrazione/Trento_Calibrated/analysis-" + runId;
//		final String visualizationScriptInputDirectory = "./visualization-scripts/";
		
		Scenario scenario1 = loadScenario(runDirectory, runId);
		
		List<AnalysisAgentFilter> filters1 = new ArrayList<>();
		AgentAnalysisFilter filter1a = new AgentAnalysisFilter("");
		filter1a.preProcess(scenario1);
		filters1.add(filter1a);
				
		List<String> modes = new ArrayList<>();
		for (String mode : modesString.split(",")) {
			modes.add(mode);
		}
		
		MatsimAnalysis analysis = new MatsimAnalysis();
		analysis.setScenario1(scenario1);
		analysis.setAgentFilters(filters1);
		analysis.setModes(modes);
//		analysis.setScenarioCRS(scenarioCRS);
		analysis.setZoneInformation(shapeFileZones, zonesCRS, zoneId);
//		analysis.setVisualizationScriptInputDirectory(visualizationScriptInputDirectory);
		analysis.setHomeActivityPrefix(homeActivityPrefix);
		analysis.setScalingFactor(scalingFactor);		
		analysis.setAnalysisOutputDirectory(analysisOutputDirectory);
		analysis.run();
	}
	
	private static Scenario loadScenario(String runDirectory, String runId) {
		log.info("Loading scenario...");
		
		if (runDirectory == null || runDirectory.equals("") || runDirectory.equals("null")) {
			return null;	
		}
		
		if (!runDirectory.endsWith("/")) runDirectory = runDirectory + "/";

		String networkFile;
		String populationFile;
		String configFile;
		
		configFile = runDirectory + runId + ".output_config.xml";	
		networkFile = runId + ".output_network.xml.gz";
		populationFile = runId + ".output_plans.xml.gz";

		Config config = ConfigUtils.loadConfig(configFile);

		if (config.controler().getRunId() != null) {
			if (!runId.equals(config.controler().getRunId())) throw new RuntimeException("Given run ID " + runId + " doesn't match the run ID given in the config file. Aborting...");
		} else {
			config.controler().setRunId(runId);
		}

		config.controler().setOutputDirectory(runDirectory);
		config.plans().setInputFile(populationFile);
		config.network().setInputFile(networkFile);
		config.vehicles().setVehiclesFile(null);
		config.transit().setTransitScheduleFile(null);
		config.transit().setVehiclesFile(null);
		
		return ScenarioUtils.loadScenario(config);
	}

}