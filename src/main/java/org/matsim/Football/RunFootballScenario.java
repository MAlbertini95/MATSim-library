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

package org.matsim.Football;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.matsim.Football.Demand.FootballFanCreator;
import org.matsim.Football.Demand.SimpleFootballFanCreator;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;

/**
 * @author tthunig, based on dgrether CottbusFootballBatch
 *
 */
public class RunFootballScenario {
	private static final Logger LOG = Logger.getLogger(RunFootballScenario.class);
	
	private enum SignalControl {FIXED, FIXED_IDEAL, SYLVIA, SYLVIA_IDEAL, LAEMMER_NICO, LAEMMER_DOUBLE, LAEMMER_NICO_GROUPS_14RE, LAEMMER_FLEXIBLE, NONE};
	private static final SignalControl CONTROL_TYPE = SignalControl.SYLVIA;
	private static final boolean CHECK_DOWNSTREAM = false;
	
	private static final boolean LONG_LANES = true;	
	private static final double FLOW_CAP = .7;
	private static final int STUCK_TIME = 120;
	private static final int TBS = 900;
	private static final String SIGNALS_BASE_CASE = "MS"; // the signals that were used for the base case plans
//	private static final String SIGNALS_BASE_CASE = "MSideal"; // the signals that were used for the base case plans
	private static final String NETWORK = "V4"; //"V1-2";
	private static final double EARLIEST_ARRIVAL_TIME_AT_STADIUM = 17 * 3600; // studies by DG and JB were made with 17. in contrast 16.5 will result in more interaction with the evening peak.
	
	public static void main(String[] args) throws FileNotFoundException, IOException {		
		Config baseConfig;
		if (args != null && args.length != 0){
			baseConfig = ConfigUtils.loadConfig(args[0]);
		} else {
			String configFileName = "Path to config/config.xml";
			baseConfig = ConfigUtils.loadConfig(configFileName);
			String scenarioDescription = "flowCap" + FLOW_CAP + "_longLanes" + LONG_LANES + "_stuckTime" + STUCK_TIME + "_tbs" + TBS + "_basePlans" + SIGNALS_BASE_CASE + NETWORK;
			if (EARLIEST_ARRIVAL_TIME_AT_STADIUM != 17 * 3600) {
				scenarioDescription += "_earliestArrivalTimeOfFans" + EARLIEST_ARRIVAL_TIME_AT_STADIUM;
			}
			baseConfig.controler().setOutputDirectory("../../runs-svn/cottbus/football/" + scenarioDescription + "/");
			baseConfig.controler().setRunId("1200");
		}
		baseConfig.controler().setLastIteration(0);	
		
		String plansFile = "cb_spn_gemeinde_nachfrage_landuse_woMines/"
				+ "commuter_population_wgs84_utm33n_car_only_100it_"+SIGNALS_BASE_CASE+"_cap"+FLOW_CAP;
		if (TBS!=10) {
			plansFile += "_tbs"+TBS;
		}
		if (NETWORK!="V1") {
			plansFile += "_net"+NETWORK;
		}
		baseConfig.plans().setInputFile(plansFile+".xml.gz");
		
		baseConfig.qsim().setFlowCapFactor(FLOW_CAP);
		baseConfig.qsim().setStorageCapFactor( FLOW_CAP / Math.pow(FLOW_CAP,1/4.) );
		
		baseConfig.qsim().setStuckTime(STUCK_TIME);
		baseConfig.qsim().setEndTime(36*3600);
		
		Scenario baseScenario = ScenarioUtils.loadScenario(baseConfig);
		
		//create the output directoy
		String baseOutputDirectory = baseConfig.controler().getOutputDirectory();
		if (! baseOutputDirectory.endsWith("/")){
			baseOutputDirectory = baseOutputDirectory.concat("/");
		}
		switch (CONTROL_TYPE){
		case FIXED_IDEAL:
			baseOutputDirectory+= "fixedTimeIdeal";
			break;
		case FIXED:
			baseOutputDirectory+= "fixedTime"; 
			break;
		case SYLVIA:
			baseOutputDirectory+= "sylviaBugFix_maxExt1.5_noFixedCycle";
			break;
		case SYLVIA_IDEAL:
			baseOutputDirectory+= "sylviaIdealBugFix_maxExt1.5_noFixedCycle";
			break;
		case LAEMMER_NICO:
			baseOutputDirectory+= "laemmer_nicoGroups";
			break;
		case LAEMMER_DOUBLE:
			baseOutputDirectory+= "laemmer_doubleGroups";
			break;
		case LAEMMER_NICO_GROUPS_14RE:
			baseOutputDirectory+= "laemmer_nicoGroups14re";
			break;
		case LAEMMER_FLEXIBLE:
			baseOutputDirectory+= "laemmer_flexible";
			break;
		case NONE:
			baseOutputDirectory+= "noSignals";
			break;
		}
		baseOutputDirectory += "_"+SIGNALS_BASE_CASE+"Plans";
		baseOutputDirectory+= "/";
		LOG.info("using base output directory: " + baseOutputDirectory);
		Population fanPop = ScenarioUtils.createScenario(ConfigUtils.createConfig()).getPopulation();
		//initialize variables needed in the loop
		String runId = baseConfig.controler().getRunId();
		Map<Integer, Double> percentageOfFans2AverageTTMap = new HashMap<>();
		Map<Integer, Double> percentageOfFans2TotalTTMap = new HashMap<>();
		Map<Integer, Double> percentageOfFans2noStuckedAgents = new HashMap<>();
		//fan creator
		String kreisShapeFile = "Path to region shape file/kreis.shp";
		FootballFanCreator fanCreator = new SimpleFootballFanCreator(kreisShapeFile);
		fanCreator.setEarliestArrivalTimeAtStadium(EARLIEST_ARRIVAL_TIME_AT_STADIUM);
		//start the runs
		int increment = 5;
		for (int numberOfFootballFans = 0; numberOfFootballFans <= 100; numberOfFootballFans = numberOfFootballFans + increment){
			if (numberOfFootballFans != 0) {
				// create additional football fans (from 0 to 2000)
				Population p = fanCreator.createAndAddFans(baseScenario, 20 * increment);
				for (Person pers : p.getPersons().values()){
					fanPop.addPerson(pers);
				}
			}
		
			baseConfig.controler().setOutputDirectory(baseOutputDirectory + numberOfFootballFans + "_football_fans/");
			baseConfig.controler().setRunId(runId + "_" + numberOfFootballFans + "_football_fans");
			Controler controler = new Controler(baseScenario);
			controler.addControlerListener(new FootballFansControlerListener(fanPop));
			//add average tt handler for football fans
			FootballAnalysisControllerListener cbfbControllerListener = new FootballAnalysisControllerListener();
			controler.addControlerListener(cbfbControllerListener);
			
			controler.run();
			if (cbfbControllerListener.getAverageTraveltime() != null){
				percentageOfFans2AverageTTMap.put(numberOfFootballFans, cbfbControllerListener.getAverageTraveltime());
				percentageOfFans2TotalTTMap.put(numberOfFootballFans, cbfbControllerListener.getTotalTraveltime());
				percentageOfFans2noStuckedAgents.put(numberOfFootballFans, cbfbControllerListener.getNumberOfStuckedPersons() + 0.);
			}
		}
		
//		try {
//			new SelectedPlans2ESRIShape(baseScenario.getPopulation(), baseScenario.getNetwork(), MGC.getCRS(TransformationFactory.WGS84_UTM33N), "/media/data/work/matsimOutput/run1219/" ).write();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		writeAnalysis(percentageOfFans2AverageTTMap, baseOutputDirectory + "average_traveltimes_last_iteration.csv", "Average travel time");
		writeAnalysis(percentageOfFans2TotalTTMap, baseOutputDirectory + "total_traveltimes_last_iteration.csv", "Total travel time");
		writeAnalysis(percentageOfFans2noStuckedAgents, baseOutputDirectory + "numberOfStuckedAgents_last_iteration.csv", "Number of stucked agents");
	}
		
	private static void writeAnalysis(Map<Integer, Double> map, String filename, String headerColumn2) throws FileNotFoundException, IOException{
		SortedMap<Integer, Double> sorted = new TreeMap<>();
		sorted.putAll(map);
		BufferedWriter writer = IOUtils.getBufferedWriter(filename);
		writer.write("Football fans %\t" + headerColumn2);
		writer.newLine();
		for (Entry<Integer, Double> e : sorted.entrySet()){
			writer.write(e.getKey().toString() + "\t" + e.getValue().toString());
			writer.newLine();
		}
		writer.close();
	}

	
}