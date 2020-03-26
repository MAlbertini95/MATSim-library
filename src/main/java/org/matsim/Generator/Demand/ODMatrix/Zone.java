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

package org.matsim.Generator.Demand.ODMatrix;

import org.matsim.api.core.v01.Coord;

public class Zone {

	public final int id;

	public final int workplaces;

	public final int workingPopulation;

	public final Coord coord;

	public Zone(int id, int workplaces, int workingPopulation, Coord coord) {
		super();
		this.id = id;
		this.workplaces = workplaces;
		this.workingPopulation = workingPopulation;
		this.coord = coord;
	}

}