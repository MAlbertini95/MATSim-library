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

import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

public class IPFUtils {
	static public INDArrayIndex[] getIndices(int numberOfDimensions, int[] dimensions, int categories[]) {
		INDArrayIndex[] index = new INDArrayIndex[numberOfDimensions];
		
		for (int i = 0; i < numberOfDimensions; i++) {
			index[i] = NDArrayIndex.all();;
		}
		
		for (int i = 0; i < dimensions.length; i++) {
			index[dimensions[i]] = NDArrayIndex.point(categories[i]);
		}
		
		return index;
	}
}