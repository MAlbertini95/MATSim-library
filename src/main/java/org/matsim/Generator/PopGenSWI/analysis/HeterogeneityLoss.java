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

package org.matsim.Generator.PopGenSWI.analysis;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

public class HeterogeneityLoss {
	final INDArray reference;
	final INDArray exists;
	
	public HeterogeneityLoss(INDArray reference) {
		this.reference = reference;
		this.exists = Nd4j.ones(reference.shape());
	}
	
	public void addSample(int[] sample) {
		INDArrayIndex[] index = new INDArrayIndex[sample.length];
		
		for (int i = 0; i < sample.length; i++) {
			index[i] = NDArrayIndex.point(sample[i]);
		}
		
		exists.put(index, 0.0);
	}
	
	public double compute() {
		INDArray referenceFrequencies = reference.div(reference.sumNumber());
		return referenceFrequencies.mul(exists).sumNumber().doubleValue();
	}
}