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
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.matsim.Football.Demand.FanFootballStrings;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.utils.io.IOUtils;


/**
 * @author dgrether
 *
 */
public class FootballTraveltimeWriter {

	
	private static final Logger log = Logger.getLogger(FootballTraveltimeWriter.class);
	

	public void exportLatestArrivals(FootballTraveltimeHandler traveltimeHandler, String filename) {
		try {
			BufferedWriter writer = IOUtils.getBufferedWriter(filename);
			writer.append(FanFootballStrings.CB2FB);
			writer.append(FanFootballStrings.SEPARATOR);
			writer.append(FanFootballStrings.SPN2FB);
			writer.append(FanFootballStrings.SEPARATOR);
			writer.append(FanFootballStrings.FB2CB);
			writer.append(FanFootballStrings.SEPARATOR);
			writer.append(FanFootballStrings.FB2SPN);
			writer.append(FanFootballStrings.SEPARATOR);
			writer.newLine();
			if (! traveltimeHandler.getArrivalTimesCB2FB().isEmpty() && ! traveltimeHandler.getArrivalTimesSPN2FB().isEmpty()){
				writer.append(max(traveltimeHandler.getArrivalTimesCB2FB().values()) + FanFootballStrings.SEPARATOR
						+ max(traveltimeHandler.getArrivalTimesSPN2FB().values()) + FanFootballStrings.SEPARATOR
						+ max(traveltimeHandler.getArrivalTimesFB2CB().values()) + FanFootballStrings.SEPARATOR
						+ max(traveltimeHandler.getArrivalTimesFB2SPN().values()) + FanFootballStrings.SEPARATOR);
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeMapToCsv(Map<Id<Person>, Double> map, String filename) {
		try {
			BufferedWriter writer = IOUtils.getBufferedWriter(filename);

			for (Entry<Id<Person>, Double> e : map.entrySet()) {
				writer.append(e.getKey().toString() + FanFootballStrings.SEPARATOR + e.getValue());
				writer.newLine();
			}
			writer.flush();
			writer.close();
			log.info("Wrote " + filename);
		} catch (IOException e1) {
			log.error("cannot write to file: " + filename);
			e1.printStackTrace();
		}

	}
	
	private static double max(Collection<Double> collection) {
		try {
			return Collections.max(collection);
		} catch (NoSuchElementException e) {
			// the collection is empty (i.e. no arrivals in this category). return 0
			return 0;
		}
	}

}
