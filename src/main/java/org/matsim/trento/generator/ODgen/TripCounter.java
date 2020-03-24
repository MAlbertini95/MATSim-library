package org.matsim.trento.generator.ODgen;
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

public class TripCounter {
	
	private District startDistrict;
	private District endDistrict;
	
	private int counter = 0;
	
	public TripCounter(District sD, District eD) {
		
		this.startDistrict = sD;
		this.endDistrict = eD;		
		
	}

	public String toString() {
		return startDistrict.getName() + ", " + endDistrict.getName() + ", " + counter;
		
	}
	
	public void count() {
		this.counter++;
	}
	
	public District getStartDistrict() {
		return startDistrict;
	}

	public District getEndDistrict() {
		return endDistrict;
	}

	public int getCounter() {
		return counter;
	}
	
}