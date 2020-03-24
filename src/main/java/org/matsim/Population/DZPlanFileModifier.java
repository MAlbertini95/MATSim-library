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

package org.matsim.Population;


import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.IdentityTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

/**
 * @author dziemke
 */
public class DZPlanFileModifier {
	
	public static void main(String[] args) {
		
		String inputPlansFile = "Path to input/plans.xml.gz";
		String outputPlansFile = "Path to output plans/plans-no-links-routes.xml.gz";

//		double selectionProbability = 0.2;
//		double selectionProbability = 0.01;
		double selectionProbability = 1;
//		boolean onlyTransferSelectedPlan = true;
		boolean onlyTransferSelectedPlan = false;
		boolean considerHomeStayingAgents = true;
		boolean includeNonSelectedStayHomePlans = true;
		boolean onlyConsiderPeopleAlwaysGoingByCar = false;
		int maxNumberOfAgentsConsidered = 10000000;

		boolean removeLinksAndRoutes = true;
//		String inputCRS = TransformationFactory.HARTEBEESTHOEK94_LO19;
//		String outputCRS = "EPSG:32734";
		String inputCRS = null;
		String outputCRS = null;
		
		CoordinateTransformation ct;
		if (inputCRS == null && outputCRS == null) {
			ct = new IdentityTransformation();
		} else {
			ct = TransformationFactory.getCoordinateTransformation(inputCRS, outputCRS);
		}

		PlanFileModifier planFileModifier = new PlanFileModifier(inputPlansFile, outputPlansFile, selectionProbability, onlyTransferSelectedPlan,
				considerHomeStayingAgents, includeNonSelectedStayHomePlans, onlyConsiderPeopleAlwaysGoingByCar,
				maxNumberOfAgentsConsidered, removeLinksAndRoutes, ct);
		
		planFileModifier.modifyPlans();
	}
}