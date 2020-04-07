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

public class IPFProblemFromUnivariateMarginals implements IPFProblem {
	final private INDArray[] marginals;
	final private int[] shape;
	
	public IPFProblemFromUnivariateMarginals(INDArray[] marginals) {
		this.marginals = marginals;
		this.shape = new int[marginals.length];
		
		int i = 0;
		for (INDArray marginal : marginals) {
			if (marginal.shape().length > 2) {
				throw new IllegalStateException();
			}
			
			this.shape[i] = marginal.shape()[1];
			i++;
		}
	}
	
	@Override
	public Number getMarginalCounts(int[] dimensions, int[] categories) {
		if (dimensions.length > 1 || categories.length > 1) {
			throw new IllegalStateException();
		}
		
		return marginals[dimensions[0]].getDouble(categories[0]);
	}

	@Override
	public int[] getShape() {
		return shape;
	}

	@Override
	public int[] getLevels() {
		return new int[] { 1 };
	}

}