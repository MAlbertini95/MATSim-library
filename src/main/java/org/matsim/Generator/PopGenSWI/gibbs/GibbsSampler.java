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

import org.matsim.Generator.PopGenSWI.Sampler;

public class GibbsSampler implements Sampler {
	final private GibbsAlgorithm algorithm;
	final private int samplingRate;
	
	private int remainingBurnInSamples;
	
	public GibbsSampler(GibbsAlgorithm algorithm, int samplingRate, int burnInSamples) {
		this.algorithm = algorithm;
		this.samplingRate = samplingRate;
		this.remainingBurnInSamples = burnInSamples;
	}
	
	public int[] sample() {
		while (remainingBurnInSamples > 0) {
			algorithm.next();
			remainingBurnInSamples--;
		}
		
		for (int i = 0; i < samplingRate - 1; i++) {
			algorithm.next();
		}
		
		return algorithm.next();		
	}
}