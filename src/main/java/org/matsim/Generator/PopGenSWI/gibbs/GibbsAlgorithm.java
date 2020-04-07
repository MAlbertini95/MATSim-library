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

package org.matsim.Generator.PopGenSWI.gibbs;

import java.util.Random;

import org.nd4j.linalg.api.ndarray.INDArray;

public class GibbsAlgorithm {
	final private GibbsProblem problem;
	final private Random random;
	
	private int[] currentSample;
	private int currentDimension;
	
	public GibbsAlgorithm(GibbsProblem problem, Random random) {
		this.problem = problem;
		this.random = random;
		
		this.currentSample = problem.getInitialSample();
		this.currentDimension = 0;
	}
	
	private int select(INDArray probabilities) {
		INDArray cumulative = probabilities.cumsum(0);
		double r = random.nextDouble();
		
		int i = 0;
		while (r > cumulative.getDouble(i)) {
			i++;
		}
		
		return i;
	}
	
	public int[] next() {
		INDArray probabilities = problem.getProbabilities(currentDimension, currentSample);

		currentSample[currentDimension] = select(probabilities);
		
		currentDimension++;
		if (currentDimension == currentSample.length) {
			currentDimension = 0;
		}
		
		return currentSample;
	}
}