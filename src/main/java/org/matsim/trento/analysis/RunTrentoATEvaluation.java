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

package org.matsim.trento.analysis;

import java.util.*;

import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.*;
import org.matsim.core.network.*;
import org.matsim.core.network.io.MatsimNetworkReader;

import org.matsim.trento.analysis.TravelTimeAnalysis;
import org.matsim.trento.analysis.TravelDistanceTimeEvaluator;
import org.matsim.trento.utils.GeomUtils;

/**
 * @author  MAlbertini, based on JBischoff
 *
 */

public class RunTrentoATEvaluation {

	public static void main(String[] args) {

		String networkFile = "C:/Users/teoal/Politecnico di Milano 1863/MAGISTRALE/Tesi/MAAS Trento/AT_5000_03/AT03.output_network.xml.gz";
		
//		Wolfsburg:
//		String shapeFile = "../../../shared-svn/projects/vw_rufbus/av_simulation/demand/zones/zones.shp";
//		Map<String,Geometry> geo = JbUtils.readShapeFileAndExtractGeometry(shapeFile, "plz");

//		Trento
		String shapeFile = "C:/Users/teoal/Politecnico di Milano 1863/MAGISTRALE/Tesi/MAAS Trento/Sez2011/TrentoSez.shp";
		Map<String,Geometry> geo = GeomUtils.readShapeFileAndExtractGeometry(shapeFile, "sez"); //Key o sez?
		
		Network network = NetworkUtils.createNetwork() ;
		new MatsimNetworkReader(network).readFile(networkFile);
		
//		List<String> list = Arrays.asList(new String[]{"00.0k_AV1.0", "02.2k_AV1.0", "02.2k_AV1.5","02.2k_AV2.0","04.4k_AV1.0","04.4k_AV1.5","04.4k_AV2.0","06.6k_AV1.0"});
		List<String> list = Arrays.asList(new String[]{"ATOnly_5000","ATOnly_7500","ATOnly_15000"}); //"AT_5000_03","AT_6000_03","AT_7000_03","AT_8000_03","AT_8000increased","ATOnly_10000","ATOnly_12500","AT_DRTOnly_8000"
		
//		List<String> list = Collections.singletonList("00.0k_AV1.0_flowCap100");
		
		for (String run : list){
		System.out.println("run "+ run);
		ZoneBasedTaxiCustomerWaitHandler zoneBasedTaxiCustomerWaitHandler = new ZoneBasedTaxiCustomerWaitHandler(network, geo);
		ZoneBasedTaxiStatusAnalysis zoneBasedTaxiStatusAnalysis = new ZoneBasedTaxiStatusAnalysis(network, geo);
//		TravelDistanceTimeEvaluator travelDistanceTimeEvaluator = new TravelDistanceTimeEvaluator(network, 0);
		TravelTimeAnalysis timeAnalysis = new TravelTimeAnalysis();
		EventsManager events = EventsUtils.createEventsManager();
		events.addHandler(zoneBasedTaxiCustomerWaitHandler);
		events.addHandler(zoneBasedTaxiStatusAnalysis);
//		events.addHandler(travelDistanceTimeEvaluator);
		events.addHandler(timeAnalysis);
		
			String outputFolder = "C:/Users/teoal/Politecnico di Milano 1863/MAGISTRALE/Tesi/MAAS Trento/"+run+"/Analysis/";
			String eventsFile = "C:/Users/teoal/Politecnico di Milano 1863/MAGISTRALE/Tesi/MAAS Trento/"+run+"/AT03.output_events.xml.gz";
			
			new MatsimEventsReader(events).readFile(eventsFile);
			zoneBasedTaxiCustomerWaitHandler.writeCustomerStats(outputFolder);
			zoneBasedTaxiStatusAnalysis.evaluateAndWriteOutput(outputFolder);
//			travelDistanceTimeEvaluator.writeTravelDistanceStatsToFiles(outputFolder);
			timeAnalysis.writeStats(outputFolder);
		}
		

		
	}

}