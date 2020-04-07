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
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import org.matsim.Generator.PopGenSWI.Sampler;
import org.nd4j.linalg.api.ndarray.INDArray;


public class IPFSampler implements Sampler {
	final private INDArray weights;
	final private Random random;

	final private int numberOfDimensions;
	final private int numberOfFlatIndices;

	final private double[] flatWeights;
	final private Integer[] flatIndices;

	public IPFSampler(INDArray weights, Random random) {
		this.numberOfDimensions = weights.shape().length;
		this.weights = weights;
		this.random = random;

		int numberOfFlatIndices = 1;

		for (int i = 0; i < weights.shape().length; i++) {
			numberOfFlatIndices *= weights.shape()[i];
		}

		this.numberOfFlatIndices = numberOfFlatIndices;

		flatWeights = new double[numberOfFlatIndices];
		flatIndices = new Integer[numberOfFlatIndices];

		for (int i = 0; i < numberOfFlatIndices; i++) {
			int[] m = buildMultidimensionalIndexFromFlatIndex(i);
			flatWeights[i] = this.weights.getDouble(m);
			flatIndices[i] = i;
		}

		Arrays.sort(flatIndices, (i, j) -> Double.compare(flatWeights[i], flatWeights[j]));

		for (int i = 1; i < numberOfFlatIndices; i++) {
			flatWeights[flatIndices[i]] += flatWeights[flatIndices[i - 1]];
		}
	}

	private int[] buildMultidimensionalIndexFromFlatIndex(int flatIndex) {
		int[] multiIndex = new int[numberOfDimensions];

		for (int i = 0; i < numberOfDimensions; i++) {
			int base = 1;

			for (int j = i + 1; j < numberOfDimensions; j++) {
				base *= weights.shape()[j];
			}

			multiIndex[i] = flatIndex / base;
			flatIndex -= multiIndex[i] * base;
		}

		return multiIndex;
	}

	private int[] directSample() {
		double target = random.nextDouble() * weights.sumNumber().doubleValue();
		int index = 0;

		while (flatWeights[flatIndices[index]] < target) {
			index++;
		}
		
		return buildMultidimensionalIndexFromFlatIndex(flatIndices[index]);
	}

	public int[] sample() {
		return directSample();
		// return rejectionSample();
	}

	private int[] rejectionSample() {
		// This was an alternative, but much slower.

		int[] index = new int[weights.shape().length];

		while (true) {
			for (int i = 0; i < weights.shape().length; i++) {
				index[i] = random.nextInt(weights.shape()[i]);
			}

			if (random.nextDouble() <= weights.getDouble(index)) {
				return index;
			}
		}
	}
}