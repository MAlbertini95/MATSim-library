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

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bytedeco.javacpp.FloatPointer;
import org.matsim.Generator.PopGenSWI.Sampler;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

public class BNAlgorithm implements Sampler {
	final private Random random;
	final int numberOfVariables;
	final int[] numberOfCategories;
	final List<INDArray> allCounts = new LinkedList<>();

	final private List<List<Integer>> allChildren = new LinkedList<>();
	final private List<List<Integer>> allParents = new LinkedList<>();
	final private List<Integer> ordering;

	public BNAlgorithm(BNGraph graph, BNProblem problem, Random random) {
		this.random = random;
		this.numberOfVariables = graph.getNumberOfVariables();
		this.numberOfCategories = problem.getNumberOfCategories();
		this.ordering = graph.getOrdering();
		double prior = 0.001;

		for (int i = 0; i < numberOfVariables; i++) {
			List<Integer> parents = new LinkedList<>(graph.getParents(i));
			allParents.add(parents);

			List<Integer> children = new LinkedList<>(graph.getChildren(i));
			allChildren.add(children);

			int[] parents_ = new int[parents.size()];
			for (int j = 0; j < parents.size(); j++) {
				parents_[j] = parents.get(j);
			}

			INDArray counts = problem.getCounts(i, parents_);
			counts.addi(prior);

			allCounts.add(counts);
		}
	}
	
	private String valueOf(int[] a) {
		String s = "";
		for (int i = 0; i < a.length; i++) {
			s += a[i] + " ";
		}
		return s;
	}
	
	private int select(INDArray probabilities) {
		INDArray cumulative = probabilities.cumsum(0);
		double r = random.nextDouble();
		
		//System.out.println(valueOf(probabilities.shape()));
		//System.out.println(cumulative);
		//System.out.println(r);
		
		int i = 0;
		while (r > cumulative.getDouble(i)) {
			i++;
		}
		
		//System.out.println("OUT " + i);
		
		return i;
	}
	
	public int[] sample() {
		return next();
	}
	
	public int[] next() {
		int[] sample = new int[numberOfVariables];
		
		for (int i : ordering) {
			List<Integer> parents = allParents.get(i);
			INDArray flatProbabilities = null;
			
			if (parents.isEmpty()) {
				flatProbabilities = allCounts.get(i);
				flatProbabilities.divi(flatProbabilities.sumNumber());
			} else {
				INDArrayIndex[] index = new INDArrayIndex[parents.size() + 1];
				index[0] = NDArrayIndex.all();
				
				for (int j = 0; j < parents.size(); j++) {
					index[j + 1] = NDArrayIndex.point(sample[parents.get(j)]);
				}
				
				flatProbabilities = allCounts.get(i).get(index);
			}
			
			flatProbabilities.divi(flatProbabilities.sumNumber());
			sample[i] = select(flatProbabilities);	
		}
		
		return sample;
	}
	
	public double getNumberOfParameters() {
		int numberOfParameters = 0;
		
		for (INDArray counts : allCounts) {
			int[] shape = counts.shape();
			int cells = 1;
			
			for (int j = 0; j < shape.length; j++) {
				cells *= shape[j];
			}
			
			numberOfParameters += cells;
		}
		
		return numberOfParameters;
	}
	
	public double computeLogLikelihood(List<List<Integer>> data) {
		double logLikelihood = 0.0;
		
		for (List<Integer> dataSample : data) {
			int[] sample = new int[dataSample.size()];
			for (int i = 0; i < sample.length; i++) {
				sample[i] = dataSample.get(i);
			}
			
			for (int i : ordering) {
				List<Integer> parents = allParents.get(i);
				INDArray flatProbabilities = null;
				
				if (parents.isEmpty()) {
					flatProbabilities = allCounts.get(i);
					flatProbabilities.divi(flatProbabilities.sumNumber());
				} else {
					INDArrayIndex[] index = new INDArrayIndex[parents.size() + 1];
					index[0] = NDArrayIndex.all();
					
					for (int j = 0; j < parents.size(); j++) {
						index[j + 1] = NDArrayIndex.point(sample[parents.get(j)]);
					}
					
					flatProbabilities = allCounts.get(i).get(index);
				}
				
				flatProbabilities.divi(flatProbabilities.sumNumber());
				
				logLikelihood += Math.log(flatProbabilities.getDouble(sample[i]));
			}
		}
		
		return logLikelihood;
	}
}