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

package org.matsim.Generator.PopGenSWI.bn;

import java.util.List;
import java.util.Random;

import org.nd4j.linalg.api.ndarray.INDArray;

public class BNGraphFinder {
	final private BNGraphGenerator generator;
	final private INDArray counts;
	final private List<List<Integer>> data;
	final private Random random;
	
	public BNGraphFinder(BNGraphGenerator generator, INDArray counts, List<List<Integer>> data, Random random) {
		this.generator = generator;
		this.counts = counts;
		this.data = data;
		this.random = random;
	}
	
	public BNGraph findGraph(int numberOfIterations) {
		BNGraph bestGraph = null;
		double bestScore = Double.NEGATIVE_INFINITY;
		
		for (int i = 0; i < numberOfIterations; i++) {
			BNGraph graph = generator.generate(data.get(0).size());

			BNProblem problem = new BNProblem(counts);
			BNAlgorithm algorithm = new BNAlgorithm(graph, problem, random);

			double logLikelihood = algorithm.computeLogLikelihood(data);
			double score = logLikelihood;// - algorithm.getNumberOfParameters();
			
			if (score > bestScore) {
				bestGraph = graph;
				bestScore = score;
				
				System.out.println("AIC: " + score + ", Graph: " + bestGraph);
			}
		}
		
		return bestGraph;	
	}
}