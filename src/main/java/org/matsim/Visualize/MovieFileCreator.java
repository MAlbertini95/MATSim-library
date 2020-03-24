/* *********************************************************************** *
 * project: org.matsim.*
 * OTFVis.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008, 2009 by the members listed in the COPYING,  *
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

package org.matsim.Visualize;


import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vis.otfvis.OTFEvent2MVI;

/**
 * @author teoal
 * 
 */

public class MovieFileCreator {
	private final static Logger log = Logger.getLogger(MovieFileCreator.class);

	public static void main(String[] args) {
		// Parameter
		String runOutputRoot = "Path to Output/Out Folder";
		double snapshotPeriod = 60;
		
		// Prende il percorso e cerca ivi i Files
		String eventFile = runOutputRoot + "/output_events.xml.gz";
		String networkFile = runOutputRoot + "/output_network.xml.gz";
		String mviFile = runOutputRoot + "/otfvis.mvi";

		// Add network to scenario
		Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);
		
		// File conversion
		OTFEvent2MVI.convert(scenario, eventFile, mviFile, snapshotPeriod);
		log.info("Movie file " + mviFile + " created.");
	}
}