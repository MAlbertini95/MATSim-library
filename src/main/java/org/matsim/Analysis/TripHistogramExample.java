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

package org.matsim.Analysis;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;

/**
 * @author  jbischoff
 *
 *Run di MATSim con il modulo aggiuntivo
 */

public class TripHistogramExample {

	public static void main(String[] args) {
		Config config = ConfigUtils.loadConfig("C:/Users/teoal/git/MATSim-library/scenarios/Trento_Calibrated/Config.xml");
		//"C:/Users/teoal/Politecnico di Milano 1863/MAGISTRALE/Tesi/Calibrazione/Trento_Calibrated/AT03.output_config.xml"
		config.vspExperimental().setAbleToOverwritePtInteractionParams(true);
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		Scenario scenario = ScenarioUtils.loadScenario(config);
		Controler controler = new Controler(scenario);
		controler.addOverridingModule(new TripHistogramModule());
		controler.run();
	}

}