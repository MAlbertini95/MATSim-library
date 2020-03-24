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

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.av.robotaxi.fares.drt.DrtFareConfigGroup;
import org.matsim.contrib.av.robotaxi.fares.taxi.TaxiFareConfigGroup;
import org.matsim.contrib.av.robotaxi.fares.taxi.TaxiFareModule;
import org.matsim.contrib.drt.run.DrtConfigConsistencyChecker;
import org.matsim.contrib.drt.run.DrtConfigGroup;
import org.matsim.contrib.drt.run.DrtModule;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.dvrp.run.DvrpModule;
import org.matsim.contrib.dvrp.run.DvrpQSimComponents;
import org.matsim.contrib.taxi.run.TaxiConfigConsistencyChecker;
import org.matsim.contrib.taxi.run.TaxiConfigGroup;
import org.matsim.contrib.taxi.run.TaxiModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.roadpricing.RoadPricingConfigGroup;
import org.matsim.roadpricing.RoadPricingModule;

import ch.sbb.matsim.config.SwissRailRaptorConfigGroup;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorModule;

/**
 * @author MAlbertini 
 *
 */
public class RunTrentoDRTATaxi{
	public static void main(String[] args) {
		if ( args.length==0 ) {
			args = new String [] { "scenarios/AT_drt introduction/AT_DRTConfig.xml" } ;
		} else {
			Gbl.assertIf( args[0] != null && !args[0].equals( "" ) );
		}

		Config config = ConfigUtils.loadConfig( args, new DvrpConfigGroup(),new DrtConfigGroup(),
				new DrtFareConfigGroup(), new RoadPricingConfigGroup(), new SwissRailRaptorConfigGroup() ) ;
		
		// possibly modify config here----------------------------------------------------------------------------------------------
		
//		config.controler().setLastIteration( 10 );
		config.controler().setOverwriteFileSetting( OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists );
		config.plansCalcRoute().setInsertingAccessEgressWalk( true );
		config.addConfigConsistencyChecker(new DrtConfigConsistencyChecker());
		config.checkConsistency();

		String mode = DrtConfigGroup.get(config).getMode();

		// ------------------------------------------------------------------------------------------------------------------------
		
		Scenario scenario = ScenarioUtils.loadScenario(config) ;

		// possibly modify scenario here-------------------------------------------------------------------------------------------

//		new NetworkCleaner().run( scenario.getNetwork() );
		
		Controler controler = new Controler( scenario ) ;
		
		// possibly modify controler here------------------------------------------------------------------------------------------
		
		controler.addOverridingModule(new SwissRailRaptorModule());
//		controler.addOverridingModule( new OTFVisLiveModule() ) ;
		controler.addOverridingModule(new DvrpModule());
		controler.addOverridingModule(new DrtModule());
		controler.addOverridingModule( new RoadPricingModule() );   
		controler.configureQSimComponents(DvrpQSimComponents.activateModes(mode));    
		
		controler.addOverridingModule( new AbstractModule() {
			@Override
			public void install() {
				addTravelTimeBinding( TransportMode.ride ).to( networkTravelTime() );
				addTravelDisutilityFactoryBinding( TransportMode.ride ).to( carTravelDisutilityFactoryKey() );
			}
		} ); 
                
		// ------------------------------------------------------------------------------------------------------------------------
		
		controler.run();
	}
	
}