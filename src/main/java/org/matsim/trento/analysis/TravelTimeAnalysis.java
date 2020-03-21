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

package org.matsim.trento.analysis;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.utils.io.IOUtils;

import org.matsim.trento.utils.JbUtils;

/**
 * @author  MAlbertini
 *
 */

/* creato da JBischoff, av/evaluation, usato per flowpaper
 * 
 */

public class TravelTimeAnalysis implements PersonDepartureEventHandler, PersonArrivalEventHandler, PersonEntersVehicleEventHandler {
	
	Map<Id<Person>,Double> lastCarDepartureTime = new HashMap<>();
	Map<Id<Person>,Double> lastTaxiDepartureTime = new HashMap<>();
	Map<Id<Person>,Double> lastTaxiVehicleEnterTime = new HashMap<>();
	final int timebins = 24; 
	double[] departuresPerHourCar = new double[timebins];
	double[] departuresPerHourTaxi= new double[timebins];
	double[] traveltimePerHourCar = new double[timebins];
	double[] inVehicleTraveltimePerHourTaxi= new double[timebins];
	double[] waitingTimePerHourTaxi= new double[timebins];
	
	@Override
	public void reset(int iteration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(PersonArrivalEvent event) {
		if (!event.getPersonId().toString().startsWith("AT_")) {
			if (event.getLegMode().equals(TransportMode.car)){
				double departureTime = lastCarDepartureTime.remove(event.getPersonId());
				double travelTime = event.getTime()-departureTime;
				int hour = JbUtils.getHour(departureTime);
				//car trip inside Trento
				this.traveltimePerHourCar[hour]+=travelTime;
				this.departuresPerHourCar[hour]++;
			}
			else if (event.getLegMode().equals("ATaxi")){
				//taxi trip
				double departureTime = lastTaxiDepartureTime.remove(event.getPersonId());
				int hour = JbUtils.getHour(departureTime);
			
				double vehEnterTime = lastTaxiVehicleEnterTime.remove(event.getPersonId());
				double travelTime = event.getTime() - vehEnterTime;   //includes pickup and dropoff (=180 s)
				this.inVehicleTraveltimePerHourTaxi[hour]+=travelTime;
				this.departuresPerHourTaxi[hour]++;
			}
			else {
				System.out.println("Untracked LegMode " + event.getLegMode());
			}
		}
	}
	

	@Override
	public void handleEvent(PersonDepartureEvent event) {
		if (event.getPersonId().toString().startsWith("AT_")) return;
		if (event.getLegMode().equals(TransportMode.car)){
			this.lastCarDepartureTime.put(event.getPersonId(), event.getTime());
		} else if (event.getLegMode().equals("ATaxi")){
			this.lastTaxiDepartureTime.put(event.getPersonId(), event.getTime());
		} else {
			System.out.println("Untracked LegMode " + event.getLegMode());
		}
		
	}


	@Override
	public void handleEvent(PersonEntersVehicleEvent event) {
		if (this.lastTaxiDepartureTime.containsKey(event.getPersonId())){
			double departureTime = lastTaxiDepartureTime.get(event.getPersonId());
			double waitingTime = event.getTime() - departureTime;
			int hour = JbUtils.getHour(departureTime);
			this.lastTaxiVehicleEnterTime.put(event.getPersonId(), event.getTime());
			this.waitingTimePerHourTaxi[hour]+=waitingTime;
		}
	}
	
	public void writeStats(String outputFolder){
		try {
			BufferedWriter bw = IOUtils.getBufferedWriter(outputFolder+"/travelTimeStats.csv");
			bw.write("hour;carTT;carRides;taxiWait;taxiIVTT;taxiTT;taxiRides");
			Locale.setDefault(Locale.US);
			DecimalFormat df = new DecimalFormat( "####0.00" );
			for (int i = 0;i<timebins;i++){
				double carTT = this.traveltimePerHourCar[i] / this.departuresPerHourCar[i];
				double taxiWait = this.waitingTimePerHourTaxi[i] / this.departuresPerHourTaxi[i];
				double taxiIVTT = this.inVehicleTraveltimePerHourTaxi[i] / this.departuresPerHourTaxi[i];
				double taxiTT = taxiWait + taxiIVTT;
				bw.newLine();
				bw.write(i+";"+df.format(carTT)+";"+this.departuresPerHourCar[i]+";"+df.format(taxiWait)+";"+df.format(taxiIVTT)+";"+df.format(taxiTT)+";"+departuresPerHourTaxi[i]);
				
			}
			bw.newLine();
			
			double carrides = new Sum().evaluate(departuresPerHourCar);
			double taxirides = new Sum().evaluate(departuresPerHourTaxi);
			double avgCarTT = new Sum().evaluate(traveltimePerHourCar) / carrides;
			
			double totTaxiWait = new Sum().evaluate(waitingTimePerHourTaxi);
			double totTaxiIVTT = new Sum().evaluate(inVehicleTraveltimePerHourTaxi);
			double totTaxiTT = totTaxiWait + totTaxiIVTT;
			double avgTaxiWat = totTaxiWait / taxirides;
			double avgTaxIVTT = totTaxiIVTT / taxirides;
			double avgTaxiTT = totTaxiTT / taxirides;
			bw.write("average;"+df.format(avgCarTT)+";"+carrides+";"+df.format(avgTaxiWat)+";"+df.format(avgTaxIVTT)+";"+df.format(avgTaxiTT)+";"+taxirides);
			
			bw.flush();
			bw.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}