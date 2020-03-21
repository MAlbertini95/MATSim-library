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

package org.matsim.trento.AVanalysis;

import java.io.File;
import java.io.IOException;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.trento.AVanalysis.passenger.PassengerAnalysisListener;
import org.matsim.trento.AVanalysis.passenger.PassengerAnalysisWriter;
import org.matsim.trento.AVanalysis.LinkFinder;

public class RunPassengerAnalysis {
	static public void main(String[] args) throws ConfigurationException, IOException {
//		CommandLine cmd = new CommandLine.Builder(args) //
//				.requireOptions("events-path", "network-path", "output-path") //
//				.build();

		String eventsPath = "C:/Users/teoal/Politecnico di Milano 1863/MAGISTRALE/Tesi/MAAS Trento/AT_5000_03/AT_5000_03.output_events.xml.gz"; //cmd.getOptionStrict("events-path");
		String networkPath = "C:/Users/teoal/Politecnico di Milano 1863/MAGISTRALE/Tesi/MAAS Trento/AT_5000_03/AT03.output_network.xml.gz"; //cmd.getOptionStrict("network-path");
		String outputPath = "C:/Users/teoal/Politecnico di Milano 1863/MAGISTRALE/Tesi/MAAS Trento/AT_5000_03/Analysis/PassengerAnalysis.csv"; //cmd.getOptionStrict("output-path");

		Network network = NetworkUtils.createNetwork();
		new MatsimNetworkReader(network).readFile(networkPath);

		LinkFinder linkFinder = new LinkFinder(network);
		PassengerAnalysisListener listener = new PassengerAnalysisListener(linkFinder);

		EventsManager eventsManager = EventsUtils.createEventsManager();
		eventsManager.addHandler(listener);
		new MatsimEventsReader(eventsManager).readFile(eventsPath);

		new PassengerAnalysisWriter(listener).writeRides(new File(outputPath));
	}
}