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

import org.matsim.Football.Demand.FanFootballStrings;
import org.matsim.core.controler.MatsimServices;
import org.matsim.core.controler.events.IterationEndsEvent;
import org.matsim.core.controler.events.IterationStartsEvent;
import org.matsim.core.controler.events.StartupEvent;
import org.matsim.core.controler.listener.IterationEndsListener;
import org.matsim.core.controler.listener.IterationStartsListener;
import org.matsim.core.controler.listener.StartupListener;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.core.utils.io.IOUtils;

/**
 * @author dgrether
 *
 */
public class FootballAnalysisControllerListener implements StartupListener, IterationStartsListener, IterationEndsListener {

	
	private FootballTraveltimeHandler traveltimeHandler;

	private Double averageTravelTime = null;
	private Double totalTravelTime = null;
	private int numberOfStuckedPersons = 0;

	@Override
	public void notifyStartup(StartupEvent e) {
	}

	@Override
	public void notifyIterationStarts(IterationStartsEvent e) {
		MatsimServices controler = e.getServices();
		if ((controler.getConfig().controler().getWriteEventsInterval() > 0) && (e.getIteration() % controler.getConfig().controler().getWriteEventsInterval() == 0)){
			this.traveltimeHandler = new FootballTraveltimeHandler();
			e.getServices().getEvents().addHandler(this.traveltimeHandler);
		}
	}

	
	@Override
	public void notifyIterationEnds(IterationEndsEvent e) {
		MatsimServices controler = e.getServices();
 		if ((controler.getConfig().controler().getWriteEventsInterval() > 0) && (e.getIteration() % controler.getConfig().controler().getWriteEventsInterval() == 0)){
 			FootballTraveltimeWriter traveltimeWriter = new FootballTraveltimeWriter();
 			
 			String filename = e.getServices().getControlerIO().getIterationFilename(e.getIteration(), "arrival_times_" + FanFootballStrings.CB2FB +  ".csv");
 			traveltimeWriter.writeMapToCsv(traveltimeHandler.getArrivalTimesCB2FB(), filename);
 			
 			filename = e.getServices().getControlerIO().getIterationFilename(e.getIteration(), "arrival_times_" + FanFootballStrings.FB2CB +  ".csv");
 			traveltimeWriter.writeMapToCsv(traveltimeHandler.getArrivalTimesFB2CB(), filename);
 			
 			filename = e.getServices().getControlerIO().getIterationFilename(e.getIteration(), "arrival_times_" + FanFootballStrings.SPN2FB +  ".csv");
 			traveltimeWriter.writeMapToCsv(traveltimeHandler.getArrivalTimesSPN2FB(), filename);
 			
 			filename = e.getServices().getControlerIO().getIterationFilename(e.getIteration(), "arrival_times_" + FanFootballStrings.FB2SPN + ".csv");
 			traveltimeWriter.writeMapToCsv(traveltimeHandler.getArrivalTimesFB2SPN(), filename);
 			
 			filename = e.getServices().getControlerIO().getIterationFilename(e.getIteration(), "latest_arrival_times.csv");
 			traveltimeWriter.exportLatestArrivals(traveltimeHandler, filename);
 			
 			filename = e.getServices().getControlerIO().getOutputFilename("average_travel_time.csv");
 			try {
 				BufferedWriter writer = IOUtils.getAppendingBufferedWriter(filename);
 				writer.append(e.getIteration() + FanFootballStrings.SEPARATOR + this.traveltimeHandler.getTotalAndAverageTravelTime());
 				writer.newLine();
 				writer.close();
 			} catch (FileNotFoundException e1) {
 				e1.printStackTrace();
 			} catch (IOException e1) {
 				e1.printStackTrace();
 			}
 			Tuple<Double, Double> totalAndAverageTT = this.traveltimeHandler.getTotalAndAverageTravelTime();
 			this.averageTravelTime = totalAndAverageTT.getSecond();
 			this.totalTravelTime = totalAndAverageTT.getFirst();
 			this.numberOfStuckedPersons = this.traveltimeHandler.getNumberOfStuckedPersons();
 			controler.getEvents().removeHandler(this.traveltimeHandler);
 			this.traveltimeHandler = null;
 		}
	}

	public Double getAverageTraveltime() {
		return this.averageTravelTime;
	}

	public Double getTotalTraveltime() {
		return this.totalTravelTime;
	}
	
	public int getNumberOfStuckedPersons() {
		return this.numberOfStuckedPersons;
	}
}