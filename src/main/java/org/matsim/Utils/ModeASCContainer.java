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

package org.matsim.Utils;


import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

/**
 *
 * @author Gunnar Flötteröd
 *
 */
@Singleton
public class ModeASCContainer {

	private final Map<String, Double> mode2asc = new LinkedHashMap<>();

	public ModeASCContainer() {
	}

	public synchronized double getASC(final String mode) {
		return this.mode2asc.getOrDefault(mode, 0.0);
	}

	public synchronized void setASC(final String mode, final double asc) {
		this.mode2asc.put(mode, asc);
		Logger.getLogger(this.getClass()).info("Set ASC for mode " + mode + " to " + this.getASC(mode));
	}

}