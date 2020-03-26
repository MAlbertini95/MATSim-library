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

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.algorithms.PersonAlgorithm;
import org.matsim.core.population.io.StreamingPopulationReader;
import org.matsim.core.population.io.StreamingPopulationWriter;
import org.matsim.core.population.io.StreamingDeprecated;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;

public class PopulationWriterTask implements PersonSink {
	
	private final String filename;

    private Population reader;

	private StreamingPopulationWriter populationWriter;

	private final Network network;
	
	public PopulationWriterTask(String filename, Network network) {
		super();
		this.filename = filename;
		this.network = network;
		init();
	}
	
	private void init() {
        MutableScenario scenario = (MutableScenario) ScenarioUtils.createScenario(ConfigUtils.createConfig());
//		reader = (Population) scenario.getPopulation();
		StreamingPopulationReader reader = new StreamingPopulationReader( scenario ) ;
		StreamingDeprecated.setIsStreaming(reader, true);
		populationWriter = new StreamingPopulationWriter();
		final PersonAlgorithm algo = populationWriter;
		reader.addAlgorithm(algo);
		populationWriter.startStreaming(filename);
		Logger.getLogger(this.getClass()).info("Will write to: " + filename ) ;
	}

	@Override
	public void process(Person person) {
		reader.addPerson(person);
	}
	
	@Override
	public void complete() {
		populationWriter.closeStreaming();
		Logger.getLogger(this.getClass()).info("... writing to " + filename + " completed.") ;
	}

}