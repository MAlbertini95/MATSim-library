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

package org.matsim.Network;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.io.OsmNetworkReader;

public class OSMNetworkCreator {

	public static void main(String[] args) {
		// Input and output
		String osmFile = "C:/Users/teoal/Desktop/MATSIM Milano/Milano_MATSim.osm"; 
		String networkFile = "C:/Users/teoal/Desktop/MATSIM Milano/Network/MilanoMATSim_NEW_3857.xml";
		
		// Parameters
		String inputCRS = "EPSG:4326"; // EPSG:4326 scaricati da OSM, fare attenzione 
		String outputCRS = "EPSG:3857"; // EPSG:6707 Shp Milano; EPSG:32632 Milano OpenData; EPSG:3857 base; EPSG:25382 Trento
//		String outputCRS = TransformationFactory.WGS84_SA_Albers;

		// Infrastructure
		Network network = (ScenarioUtils.createScenario(ConfigUtils.createConfig())).getNetwork();
		CoordinateTransformation coordinateTransformation = TransformationFactory.getCoordinateTransformation(inputCRS, outputCRS);
		
		// "useHighwayDefaults" needs to be set to false to be able to set own values below.
		OsmNetworkReader osmNetworkReader = new OsmNetworkReader(network, coordinateTransformation, false);

		// Set values
		osmNetworkReader.setHighwayDefaults(1, "motorway",      2, 110.0/3.6, 1.0, 2000, true); // 100 instead of 120
		osmNetworkReader.setHighwayDefaults(1, "motorway_link", 1,  80.0/3.6, 1.0, 1500, true);
		osmNetworkReader.setHighwayDefaults(2, "trunk",         1,  80.0/3.6, 1.0, 2000);
		osmNetworkReader.setHighwayDefaults(2, "trunk_link",    1,  50.0/3.6, 1.0, 1500);
		osmNetworkReader.setHighwayDefaults(3, "primary",       1,  80.0/3.6, 1.0, 1500);
		osmNetworkReader.setHighwayDefaults(3, "primary_link",  1,  60.0/3.6, 1.0, 1500);
		osmNetworkReader.setHighwayDefaults(4, "secondary",     1,  60.0/3.6, 1.0, 1000);
		osmNetworkReader.setHighwayDefaults(5, "tertiary",      1,  45.0/3.6, 1.0,  600);
		// minor, unclassified, residential, living_street" are left out here, whereas they are used by defaults.
			
		// additional to defaults
		osmNetworkReader.setHighwayDefaults(4, "secondary_link", 1, 60.0/3.6, 1.0, 1000); // same values as "secondary"
		osmNetworkReader.setHighwayDefaults(5, "tertiary_link", 1, 45.0/3.6, 1.0,  600); // same values as "tertiary"
		osmNetworkReader.setHighwayDefaults(6, "unclassified", 1, 40.0/3.6, 1.0,  600);  
		osmNetworkReader.setHighwayDefaults(6, "residential", 1, 30.0/3.6, 1.0,  600);  
		osmNetworkReader.setHighwayDefaults(6, "service", 1, 20.0/3.6, 1.0,  300); 
//		osmNetworkReader.setHighwayDefaults(5, "living_street", 1, 45.0/3.6, 1.0,  600);  
//		osmNetworkReader.setHighwayDefaults(5, "minor", 1, 45.0/3.6, 1.0,  600);  

		// additional to defaults
		osmNetworkReader.setHighwayDefaults(4, "secondary_link", 1, 60.0/3.6, 1.0, 1000); // same values as "secondary"
		osmNetworkReader.setHighwayDefaults(5, "tertiary_link", 1, 45.0/3.6, 1.0,  600); // same values as "tertiary"
		osmNetworkReader.setHighwayDefaults(6, "unclassified", 1, 40.0/3.6, 1.0,  600);  
		
		// Read OSM file
		osmNetworkReader.parse(osmFile); 
		new NetworkCleaner().run(network);
		
		// Write network XML file
		(new NetworkWriter(network)).write(networkFile);
	}
}