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

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

/**
* @author ikaddoura
*/

public class WriteNetwork2ShapeFile {

	public static void main(String[] args) {
		Config config = ConfigUtils.createConfig();
		config.network().setInputFile("C:/Users/teoal/Desktop/MATSIM Milano/Network/Milano_MATSim_3857.xml");
		config.global().setCoordinateSystem("EPSG:3857");
		Scenario scenario = ScenarioUtils.loadScenario(config);
		Network2Shape.exportNetwork2Shp(scenario, "./scenarios/", "EPSG:3857", TransformationFactory.getCoordinateTransformation("EPSG:3857", "EPSG:3857"));
	}

}