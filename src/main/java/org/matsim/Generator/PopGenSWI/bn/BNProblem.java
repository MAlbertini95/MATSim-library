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

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

public class BNProblem {
	final private INDArray counts;
	final private int[] numberOfCategories;
	final private int numberOfDimensions;
	
	public BNProblem(INDArray counts) {
		this.counts = counts;
		this.numberOfCategories = counts.shape();
		this.numberOfDimensions = numberOfCategories.length;
	}
	
	public INDArray getCounts(int variable, int[] conditionalVariables) {
		// Determine shape for the probability table. First dimension is the dependent variable
		int[] shape = new int[1 + conditionalVariables.length];
		
		for (int i = 0; i < shape.length; i++) {
			if (i == 0) {
				shape[i] = numberOfCategories[variable];
			} else {
				shape[i] = numberOfCategories[conditionalVariables[i - 1]];
			}
		}
		
		INDArray probabilities = Nd4j.zeros(shape);
		
		INDArrayIndex[] index = new INDArrayIndex[numberOfDimensions];
		for (int i = 0; i < numberOfDimensions; i++) {
			index[i] = NDArrayIndex.all();
		}
		
		INDArrayIndex writeIndex[] = new INDArrayIndex[conditionalVariables.length + 1];
		
		// This is a mess and needs a lot of commenting
		// The main part is that we iterate over all categories in all the conditional
		// dimensions by constructing the category combinations "kind of" like binary
		// numbers
		
		for (int dependentCategory = 0; dependentCategory < shape[0]; dependentCategory++) {
			index[variable] = NDArrayIndex.point(dependentCategory);
			writeIndex[0] = NDArrayIndex.point(dependentCategory);
			
			int categories[] = new int[conditionalVariables.length];

			while (true) {
				for (int j = 0; j < conditionalVariables.length; j++) {
					index[conditionalVariables[j]] = NDArrayIndex.point(categories[j]);
					writeIndex[j + 1] = NDArrayIndex.point(categories[j]);
				}
				
				probabilities.put(writeIndex, counts.get(index).sumNumber());
				
				int i = 0;
				while (i < categories.length && categories[i] == numberOfCategories[conditionalVariables[i]] - 1) {
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
		
		return probabilities;
	}
	
	public int[] getNumberOfCategories() {
		return numberOfCategories;
	}
}