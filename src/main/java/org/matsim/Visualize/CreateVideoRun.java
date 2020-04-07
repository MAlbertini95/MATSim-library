/* *********************************************************************** *
 * project: org.matsim.*												   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
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

package org.matsim.Visualize;

import java.io.IOException;

/**
* @author teoal customizing ikaddoura
* 
* Utilizza MATSimVideoUtils, che crea un video a partire dai file png delle iterazioni
*/

public class CreateVideoRun {

	private static final String runDirectory = "C:/Users/teoal/Politecnico di Milano 1863/MAGISTRALE/Tesi/MAAS Trento/AT_5000_03/";
	private static final String runId = "AT03";

	public static void main(String[] args) throws IOException {
//		MATSimVideoUtils.createVideo(runDirectory, 10, "tolls");
		MATSimVideoUtils.createLegHistogramVideo(runDirectory, runId, "C:/Users/teoal/Politecnico di Milano 1863/MAGISTRALE/Tesi/MAAS Trento/AT_5000_03");
	}
	
}
