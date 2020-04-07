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

package org.matsim.Generator.PopGenSWI.data;


import java.util.List;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

public class CountsGenerator {
	public INDArray getCounts(List<List<Integer>> data) {
		return getCounts(data, data);
	}
	
	public INDArray getCounts(List<List<Integer>> data, List<List<Integer>> full) {
		int numberOfDimensions = data.get(0).size();
		int[] numberOfCategories = new int[numberOfDimensions];
		
		for (List<Integer> row : full) {
			for (int i = 0; i < numberOfDimensions; i++) {
				numberOfCategories[i] = Math.max(numberOfCategories[i], row.get(i) + 1);
			}
		}
		
		INDArray counts = Nd4j.zeros(numberOfCategories);
		
		for (List<Integer> row : data) {
			INDArrayIndex[] writeIndex = new INDArrayIndex[numberOfDimensions];
			int[] readIndex = new int[numberOfDimensions];
			
			for (int i = 0; i < numberOfDimensions; i++) {
				writeIndex[i] = NDArrayIndex.point(row.get(i));
				readIndex[i] = row.get(i);
			}
			
			counts.put(writeIndex, counts.getDouble(readIndex) + 1.0);
		}
		
		return counts;
	}
}