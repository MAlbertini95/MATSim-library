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

package org.matsim.Generator.PopGenSWI;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.matsim.Generator.PopGenSWI.bn.BNGraph;
import org.matsim.Generator.PopGenSWI.bn.BNGraphFinder;
import org.matsim.Generator.PopGenSWI.bn.BNGraphGenerator;
import org.matsim.Generator.PopGenSWI.data.CSVReader;
import org.matsim.Generator.PopGenSWI.data.CountsGenerator;
import org.nd4j.linalg.api.ndarray.INDArray;

public class TestNetworkChoice {
	static public void main(String[] args) throws IOException {
		List<List<Integer>> data = new CSVReader(";").load(new File("../data/alter_gesl.csv"),
				Arrays.asList("alter", "gesl"));
		Collections.shuffle(data);
		
		List<List<Integer>> reducedData = data.subList(0, 1000);

		INDArray counts = new CountsGenerator().getCounts(data);
		INDArray reducedCounts = new CountsGenerator().getCounts(reducedData, data);

		Random random = new Random(0);
		BNGraphGenerator graphGenerator = new BNGraphGenerator(random);
		
		BNGraphFinder graphFinder = new BNGraphFinder(graphGenerator, reducedCounts, reducedData, random);
		BNGraph graph = graphFinder.findGraph(100);
	}
}