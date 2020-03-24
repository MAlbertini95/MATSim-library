/* *********************************************************************** *
 * project: org.matsim.*
 * OTFVis.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008, 2009 by the members listed in the COPYING,  *
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

package org.matsim.contrib.otfvis;

import org.matsim.contrib.otfvis.OTFVis;
import org.matsim.vis.otfvis.OTFClientFile;

/**
 * @author teoal
 */

public class MovieFilePlayer {
	
	public static void main(String[] args) {
		// Parameters
		String mviFile = "Path to OTFVIS file/otfvis.mvi";
		boolean createScreenshots = true; // Snapshots will be stored at run directory
		
		// Run
		if (createScreenshots == false) {
			OTFVis.playMVI(mviFile);
		} else {
//			new OTFClientFile(mviFile).run();
			new MyOTFClientFile(mviFile).run();
		}
	}
}