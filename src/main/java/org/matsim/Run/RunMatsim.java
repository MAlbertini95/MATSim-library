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
package org.matsim.Run;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.roadpricing.RoadPricingModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.scenario.ScenarioUtils;

import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorModule;

/**
 * @author nagel
 *
 */
public class RunMatsim{
	private static final Logger log = Logger.getLogger( RunMatsim.class ) ;

	public static void main(String[] args) {
		if ( args.length==0 ) {
			args = new String [] { "scenarios/Trento_100pct/config.xml" } ;
			// to make sure that something is run by default; better start from MATSimGUI.
		} else {
			Gbl.assertIf( args[0] != null && !args[0].equals( "" ) );
		}

		Config config = ConfigUtils.loadConfig( args ) ;
		
		// possibly modify config here
//		config.controler().setLastIteration( 10 );
		config.controler().setOverwriteFileSetting( OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists );
		config.plansCalcRoute().setInsertingAccessEgressWalk( true );

		// ---
		
		Scenario scenario = ScenarioUtils.loadScenario(config) ;

		// possibly modify scenario here
		for( Link link : scenario.getNetwork().getLinks().values() ){
//			if ( link.getFreespeed() > 999999. ) {
//				log.warn( "linkId=" + link.getId() + "; freespeed=" + link.getFreespeed() );
//				link.setFreespeed( 100. );
//			}
			if ( link.getLength()<=0. ) {
				log.warn( "linkId=" + link.getId() + "; length=" + link.getLength() );
				link.setLength( 0.1 );
			}
		}

//		new NetworkCleaner().run( scenario.getNetwork() );
		
		Controler controler = new Controler( scenario ) ;
		
		// possibly modify controler here
		
//		To use SwissRailRaptor faster and working pt router (Part 1 of 1)
		controler.addOverridingModule(new SwissRailRaptorModule());
//		controler.addOverridingModule( new OTFVisLiveModule() ) ;
//      controler.addOverridingModule(new CadytsPtModule());
//      controler.addOverridingModule(new CadytsCarModule());	
// 		use the road pricing module.
//		(loads the road pricing scheme, uses custom travel disutility including tolls, etc.)
//      controler.setModules(new ControlerDefaultsWithRoadPricingModule());
//		controler.addOverridingModule( new RoadPricingModule() );	
        
// use the (congested) car travel time for the teleported ride mode
		controler.addOverridingModule( new AbstractModule() {
			@Override
			public void install() {
				addTravelTimeBinding( TransportMode.ride ).to( networkTravelTime() );
				addTravelDisutilityFactoryBinding( TransportMode.ride ).to( carTravelDisutilityFactoryKey() );
			}
		} );        

// include cadyts into the plan scoring (this will add the cadyts corrections to the scores):
//		controler.setScoringFunctionFactory(new ScoringFunctionFactory() {
//			@Inject CadytsContext cadytsContext;
//			@Inject ScoringParametersForPerson parameters;
//			@Override
//			public ScoringFunction createNewScoringFunction(Person person) {
//				final ScoringParameters params = parameters.getScoringParameters(person);
//				
//				SumScoringFunction scoringFunctionAccumulator = new SumScoringFunction();
//				scoringFunctionAccumulator.addScoringFunction(new CharyparNagelLegScoring(params, controler.getScenario().getNetwork(), config.transit().getTransitModes()));
//				scoringFunctionAccumulator.addScoringFunction(new CharyparNagelActivityScoring(params)) ;
//				scoringFunctionAccumulator.addScoringFunction(new CharyparNagelAgentStuckScoring(params));
//
//				final CadytsScoring<Link> scoringFunction = new CadytsScoring<>(person.getSelectedPlan(), config, cadytsContext);
//				scoringFunction.setWeightOfCadytsCorrection(15. * config.planCalcScore().getBrainExpBeta()) ;
//				scoringFunctionAccumulator.addScoringFunction(scoringFunction );
//
//				return scoringFunctionAccumulator;
//			}
//		}) ;
                
		// ---
		
		controler.run();
	}
	
}
