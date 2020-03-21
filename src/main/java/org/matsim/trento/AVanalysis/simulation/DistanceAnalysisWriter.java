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

package org.matsim.trento.AVanalysis.simulation;


import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.core.controler.OutputDirectoryHierarchy;

import org.matsim.trento.AVanalysis.FleetDistanceListener;
import org.matsim.trento.AVanalysis.FleetDistanceListener.OperatorData;
import ch.ethz.matsim.av.data.AVOperator;

public class DistanceAnalysisWriter implements Closeable {
	private final Collection<Id<AVOperator>> operatorIds;

	private Map<Id<AVOperator>, BufferedWriter> writers;
	private Map<Id<AVOperator>, File> paths = new HashMap<>();

	public DistanceAnalysisWriter(OutputDirectoryHierarchy outputDirectory, Collection<Id<AVOperator>> operatorIds) {
		this.operatorIds = operatorIds;

		for (Id<AVOperator> operatorId : operatorIds) {
			paths.put(operatorId, new File(outputDirectory.getOutputFilename("distance_" + operatorId + ".csv")));
		}
	}

	public void write(FleetDistanceListener listener) throws IOException {
		if (writers == null) {
			writers = new HashMap<>();

			for (Id<AVOperator> operatorId : operatorIds) {
				@SuppressWarnings("resource")
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(paths.get(operatorId))));

				writer.write(String.join(";", new String[] { "occupied_distance", //
						"empty_distance", //
						"passenger_distance" //
				}) + "\n");
				writer.flush();

				writers.put(operatorId, writer);
			}
		}

		for (Id<AVOperator> operatorId : operatorIds) {
			BufferedWriter writer = writers.get(operatorId);
			OperatorData data = listener.getData(operatorId);

			writer.write(String.join(";", new String[] { String.valueOf(data.occupiedDistance_m), //
					String.valueOf(data.emptyDistance_m), //
					String.valueOf(data.passengerDistance_m) //
			}) + "\n");
			writer.flush();
		}
	}

	@Override
	public void close() throws IOException {
		if (writers != null) {
			for (Id<AVOperator> operatorId : operatorIds) {
				writers.get(operatorId).close();
			}
		}
	}
}