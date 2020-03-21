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
package org.matsim.trento.run;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkChangeEvent.ChangeType;
import org.matsim.core.network.NetworkChangeEvent.ChangeValue;
import org.matsim.core.network.NetworkUtils;
import org.matsim.contrib.av.flow.AvIncreasedCapacityModule;
import org.matsim.contrib.av.robotaxi.fares.taxi.TaxiFareConfigGroup;
import org.matsim.contrib.av.robotaxi.fares.taxi.TaxiFareModule;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.dvrp.run.DvrpModule;
import org.matsim.contrib.dvrp.run.DvrpQSimComponents;
import org.matsim.contrib.dvrp.trafficmonitoring.DvrpTravelTimeModule;
import org.matsim.contrib.dvrp.trafficmonitoring.TravelTimeUtils;
import org.matsim.contrib.taxi.run.MultiModeTaxiConfigGroup;
import org.matsim.contrib.taxi.run.MultiModeTaxiModule;
import org.matsim.contrib.taxi.run.TaxiConfigConsistencyChecker;
import org.matsim.contrib.taxi.run.TaxiConfigGroup;
import org.matsim.contrib.taxi.run.TaxiModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.roadpricing.RoadPricingConfigGroup;
import org.matsim.roadpricing.RoadPricingModule;

import com.google.inject.name.Names;

import ch.sbb.matsim.config.SwissRailRaptorConfigGroup;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorModule;

/**
 * @author MAlbertini 
 *
 */
public class RunTrentoATonly{
	public static void main(String[] args) {
		if ( args.length==0 ) {
			args = new String [] { "scenarios/Car2AT/AT_Only_Config.xml" } ;
		} else {
			Gbl.assertIf( args[0] != null && !args[0].equals( "" ) );
		}

		Config config = ConfigUtils.loadConfig( args, new DvrpConfigGroup(), new TaxiConfigGroup(), new TaxiFareConfigGroup(), 
				new RoadPricingConfigGroup(), new SwissRailRaptorConfigGroup() ) ;
		
		// possibly modify config here----------------------------------------------------------------------------------------------
		
//		config.controler().setLastIteration( 1 );	

		config.controler().setOverwriteFileSetting( OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists );
		config.plansCalcRoute().setInsertingAccessEgressWalk( false );
		config.addConfigConsistencyChecker(new TaxiConfigConsistencyChecker());
		config.checkConsistency();
		
		// ------------------------------------------------------------------------------------------------------------------------
		
		Scenario scenario = ScenarioUtils.loadScenario(config) ;

		// possibly modify scenario here-------------------------------------------------------------------------------------------

//		new NetworkCleaner().run( scenario.getNetwork() );
		
		Controler controler = new Controler( scenario ) ;
		
		String mode = TaxiConfigGroup.get(config).getMode();	
		
//		double flowEfficiencyFactor= 2.0;
//		String inputEvents="scenarios/Car2AT/Calibrated.output_events.xml.gz";
		
		// possibly modify controler here------------------------------------------------------------------------------------------

		// to speed up computations
//		final TravelTime initialTT = TravelTimeUtils.createTravelTimesFromEvents(controler.getScenario(), inputEvents);
//		controler.addOverridingModule(new AbstractModule() {
//			@Override
//			public void install() {
//				bind(TravelTime.class).annotatedWith(Names.named(DvrpTravelTimeModule.DVRP_INITIAL))
//						.toInstance(initialTT);
//			}
//		});		
		
		controler.addOverridingModule(new SwissRailRaptorModule());
//		controler.addOverridingModule( new OTFVisLiveModule() ) ;
		controler.addOverridingModule(new TaxiFareModule());
		controler.addOverridingModule(new DvrpModule());
		controler.addOverridingModule(new TaxiModule());
//		controler.addOverridingModule(new AvIncreasedCapacityModule(flowEfficiencyFactor));
		controler.addOverridingModule( new RoadPricingModule() );
		controler.configureQSimComponents(DvrpQSimComponents.activateModes(mode));
             
		// ------------------------------------------------------------------------------------------------------------------------
		
		controler.run();
	}
	
}
