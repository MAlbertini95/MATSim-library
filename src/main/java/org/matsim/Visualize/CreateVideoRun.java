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
*/

public class CreateVideoRun {

	private static final String runDirectory = "/Users/ihab/Desktop/ils4a/ziemke/open_berlin_scenario/output/be400mt_58_v6/";
	private static final String runId = "be400mt_58_v6";

	public static void main(String[] args) throws IOException {
//		MATSimVideoUtils.createVideo(runDirectory, 10, "tolls");
		MATSimVideoUtils.createLegHistogramVideo(runDirectory, runId, "/Users/ihab/Desktop/video");
	}
	
}
