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

import org.nd4j.linalg.api.ndarray.INDArray;


public class IPFProblemFromCounts implements IPFProblem {
	final private INDArray referenceCounts;
	final private int numberOfDimensions;
	final private int[] levels;

	public IPFProblemFromCounts(INDArray referenceCounts, int[] levels) {
		this.referenceCounts = referenceCounts;
		this.numberOfDimensions = referenceCounts.shape().length;
		this.levels = levels;
	}

	@Override
	public Number getMarginalCounts(int[] dimensions, int[] categories) {
		return referenceCounts.get(IPFUtils.getIndices(numberOfDimensions, dimensions, categories)).sumNumber();
	}

	@Override
	public int[] getShape() {
		return referenceCounts.shape();
	}

	@Override
	public int[] getLevels() {
		return levels;
	}
}