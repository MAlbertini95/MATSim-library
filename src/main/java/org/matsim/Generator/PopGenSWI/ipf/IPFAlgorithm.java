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

package org.matsim.Generator.PopGenSWI.ipf;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.math3.util.Combinations;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

public class IPFAlgorithm {
	final private INDArray weights;
	final private IPFProblem problem;

	final int[] numberOfCategories;
	final int numberOfDimensions;

	public IPFAlgorithm(IPFProblem problem) {
		this.problem = problem;
		this.weights = Nd4j.ones(problem.getShape()).muli(0.01);

		this.numberOfCategories = weights.shape();
		this.numberOfDimensions = numberOfCategories.length;
	}
	
	public void setWeights(INDArray weights) {
		Nd4j.copy(weights, this.weights);
	}

	public INDArray getWeights() {
		return weights;
	}

	private void adjust(int[] dimensions, int[] categories) {
		INDArrayIndex[] index = IPFUtils.getIndices(numberOfDimensions, dimensions, categories);

		Number referenceCount = problem.getMarginalCounts(dimensions, categories);
		Number weightsCount = weights.get(index).sumNumber();
		
		//System.out.println(dimensions[0] + " " + categories[0] + " " + (referenceCount.doubleValue() / weightsCount.doubleValue()));

		weights.get(index).muli(referenceCount.doubleValue() / weightsCount.doubleValue());
	}

	public void runOneIteration() {
		int[] numberOfCategories = weights.shape();
		int numberOfDimensions = numberOfCategories.length;

		for (int level : problem.getLevels()) {
			for (int[] dimensions : new Combinations(numberOfDimensions, level)) {
				int categories[] = new int[dimensions.length];
				
				while (true) {
					adjust(dimensions, categories);

					int i = 0;
					while (i < categories.length && categories[i] == numberOfCategories[dimensions[i]] - 1) {
						i++;
					}
					
					if (i == categories.length) {
						break;
					}

					categories[i]++;
					for (int j = 0; j < i; j++) {
						categories[j] = 0;
					}
				}
			}
		}

		/*System.exit(1);
		for (int dimension = 0; dimension < numberOfDimensions; dimension++) {
			for (int category = 0; category < numberOfCategories[dimension]; category++) {
				adjust(new int[] { dimension }, new int[] { category });
			}
		}
		for (int dimension1 = 0; dimension1 < numberOfDimensions; dimension1++) {
			for (int dimension2 = 0; dimension2 < numberOfDimensions; dimension2++) {
				for (int category1 = 0; category1 < numberOfCategories[dimension1]; category1++) {
					for (int category2 = 0; category2 < numberOfCategories[dimension2]; category2++) {
						adjust(new int[] { dimension1, dimension2 }, new int[] { category1, category2 });
					}
				}
			}
		}*/
	}
}