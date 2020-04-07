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

import org.matsim.Generator.PopGenSWI.analysis.HeterogeneityLoss;
import org.matsim.Generator.PopGenSWI.analysis.SRMSE;
import org.matsim.Generator.PopGenSWI.bn.BNAlgorithm;
import org.matsim.Generator.PopGenSWI.bn.BNGraph;
import org.matsim.Generator.PopGenSWI.bn.BNGraphFinder;
import org.matsim.Generator.PopGenSWI.bn.BNGraphGenerator;
import org.matsim.Generator.PopGenSWI.bn.BNProblem;
import org.matsim.Generator.PopGenSWI.data.CSVReader;
import org.matsim.Generator.PopGenSWI.data.CountsGenerator;
import org.matsim.Generator.PopGenSWI.gibbs.GibbsAlgorithm;
import org.matsim.Generator.PopGenSWI.gibbs.GibbsProblemFromCounts;
import org.matsim.Generator.PopGenSWI.gibbs.GibbsSampler;
import org.matsim.Generator.PopGenSWI.ipf.IPFAlgorithm;
import org.matsim.Generator.PopGenSWI.ipf.IPFProblemFromCounts;
import org.matsim.Generator.PopGenSWI.ipf.IPFSampler;
import org.nd4j.linalg.api.ndarray.INDArray;

public class RunData {
	static private Sampler createIPFSampler(INDArray counts, INDArray reducedCounts, Random random) {
		IPFProblemFromCounts problem = new IPFProblemFromCounts(counts, new int[] { 1 });
		IPFAlgorithm algorithm = new IPFAlgorithm(problem);
		algorithm.setWeights(reducedCounts.add(0.001));

		for (int i = 0; i < 100; i++)
			algorithm.runOneIteration();
		
		IPFSampler sampler = new IPFSampler(algorithm.getWeights(), random);

		return sampler;
	}

	static private Sampler createGibbsSampler(INDArray reducedCounts, Random random) {
		GibbsProblemFromCounts problem = new GibbsProblemFromCounts(reducedCounts);
		problem.chooseRandomInitialSample(random);

		GibbsAlgorithm algorithm = new GibbsAlgorithm(problem, random);
		GibbsSampler sampler = new GibbsSampler(algorithm, 10, 10000);

		return sampler;
	}

	static private Sampler createBNSampler(List<List<Integer>> reducedData, INDArray reducedCounts, Random random) {
		BNGraphGenerator graphGenerator = new BNGraphGenerator(random);

		BNGraphFinder graphFinder = new BNGraphFinder(graphGenerator, reducedCounts, reducedData, random);
		BNGraph graph = graphFinder.findGraph(1000);

		BNProblem problem = new BNProblem(reducedCounts);
		BNAlgorithm algorithm = new BNAlgorithm(graph, problem, random);
		BNAlgorithm sampler = algorithm;

		return sampler;
	}

	public static void main(String[] args) throws IOException {
		File inputPath = new File(args[0]);
		List<String> columns = Arrays.asList(args[1].split(","));
		double fraction = Double.parseDouble(args[2]);
		double relativeSampleSize = Double.parseDouble(args[3]);
		String samplerName = args[4];
		
		Random random = new Random(0);

		List<List<Integer>> data = new CSVReader(";").load(inputPath, columns);
		Collections.shuffle(data, random);

		int reducedDataSize = (int) (fraction * data.size());
		List<List<Integer>> reducedData = data.subList(0, reducedDataSize);

		INDArray counts = new CountsGenerator().getCounts(data);
		INDArray reducedCounts = new CountsGenerator().getCounts(reducedData, data);
		
		Sampler sampler = null;
		int sampleSize = (int) (data.size() * relativeSampleSize);
		
		switch (samplerName) {
		case "ipf":
			sampler = createIPFSampler(counts, reducedCounts, random);
			break;
		case "gibbs":
			sampler = createGibbsSampler(reducedCounts, random);
			break;
		case "bn":
			sampler = createBNSampler(reducedData, reducedCounts, random);
			break;
		default:
			throw new IllegalArgumentException();
		}

		SRMSE srmse = new SRMSE(counts);
		HeterogeneityLoss heterogeneityLoss = new HeterogeneityLoss(counts);
		
		long lastTime = System.currentTimeMillis();

		for (int i = 0; i < sampleSize; i++) {
			int[] sample = sampler.sample();
			srmse.addSample(sample);
			heterogeneityLoss.addSample(sample);

			if (System.currentTimeMillis() - lastTime > 1000) {
				lastTime = System.currentTimeMillis();
				System.out.println(i + "/" + sampleSize + " # SRMSE: " + srmse.compute() + "   L: " + heterogeneityLoss.compute());
			}
		}
		
		System.out.println("Final # SRMSE: " + srmse.compute() + "   L: " + heterogeneityLoss.compute());
	}
}