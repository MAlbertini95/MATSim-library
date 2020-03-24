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

package org.matsim.AVanalysis;


import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;

public class LinkFinder {
	private final Network network;

	public LinkFinder(Network network) {
		this.network = network;
	}

	public Link getLink(Id<Link> linkId) {
		Link link = network.getLinks().get(linkId);

		if (link == null) {
			throw new IllegalStateException("Cannot find link: " + linkId);
		}

		return link;
	}

	public double getDistance(Id<Link> linkId) {
		return getLink(linkId).getLength();
	}
}