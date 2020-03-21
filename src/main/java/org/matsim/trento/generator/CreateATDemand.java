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

package org.matsim.trento.generator;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PersonUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.population.io.PopulationWriter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.router.TransitActsRemover;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

/* creato da JBischoff, av/accessibility/pseudodemand, usato per flowpaper
 * 
 */

public class CreateATDemand {


    /**
     * This class can be used to modify the modes of a certain share of current 
     * users of a population and replace it by a different mode.
     * Keep other modes in the plans file.
     */


    public static void main(String[] args) {

        String inputPopulation = "scenarios/Car2AT/Trento_plans.xml.gz";
        String outputPopulation = "scenarios/Car2AT/Car2ATaxiConvertedplans";
        new CreateATDemand().run(inputPopulation, outputPopulation, 1.0);

    }

    public void run(String inputPopulationFile, String outputPopulationFile, Double robotaxiShare) {
        for (int i = 5; i < 10; i++) {
            Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
            new PopulationReader(scenario).readFile(inputPopulationFile);
            replaceMode(TransportMode.car, TransportMode.ride, "ATaxi", robotaxiShare, scenario, new Random(42 - i));  //InputData
            new PopulationWriter(scenario.getPopulation()).write(outputPopulationFile + "_" + i + ".xml.gz");

        }
    }


    /**
     * Parses through the population to get all users of a certain fromMode (car), then
     * randomly selects a share of them and replaces all trips of the fromMode with
     * a new one (ATaxi). All unselected plans are removed in this process.
     * It also takes away the car availability of people who have switched from car to ATaxi.
     * It doesn't touch freight or external, but does switch airport car users to ATaxi.
     *
     * @param fromMode
     * @param toMode
     * @param share
     */
    public void replaceMode(String fromMode1,String fromMode2, String toMode, double share, Scenario scenario, Random random) {
        List<Person> modeUsers = new ArrayList<>();
        for (Person p : scenario.getPopulation().getPersons().values()) {
            PersonUtils.removeUnselectedPlans(p);
            Plan plan = p.getSelectedPlan();
            for (PlanElement pe : plan.getPlanElements()) {
                 if (pe instanceof Leg) {
                    if (((Leg) pe).getMode().equals(fromMode1)) {
                       modeUsers.add(p);
                       break;
                      }
                   }
                }
            }
        
        for (Person p : scenario.getPopulation().getPersons().values()) {
            PersonUtils.removeUnselectedPlans(p);
            Plan plan = p.getSelectedPlan();
            for (PlanElement pe : plan.getPlanElements()) {
                 if (pe instanceof Leg) {
                    if (((Leg) pe).getMode().equals(fromMode2)) {
                       modeUsers.add(p);
                       break;
                      }
                   }
                }
            }
        
        Collections.shuffle(modeUsers, random);
        int numberOfPersons = (int) Math.ceil(modeUsers.size() * share);
        if (numberOfPersons > modeUsers.size())
            numberOfPersons = modeUsers.size();

        for (int i = 0; i < numberOfPersons; i++) {
            Person p = modeUsers.get(i);
            new TransitActsRemover().run(p.getSelectedPlan(), true);

            p.getSelectedPlan().getPlanElements().stream().filter(Leg.class::isInstance).filter(isMode(fromMode1))
                    .forEach(l -> {
                        Leg leg = (Leg) l;
                        leg.setMode(toMode);
                        leg.setRoute(null);
                    });

            p.getSelectedPlan().getPlanElements().stream().filter(Leg.class::isInstance).filter(isMode(fromMode2))
            .forEach(l -> {
                Leg leg = (Leg) l;
                leg.setMode(toMode);
                leg.setRoute(null);
            });
            PersonUtils.setCarAvail(p, "never");
        }

    }

    Predicate<PlanElement> isMode(String mode) {
        return (Predicate<PlanElement>) l -> ((Leg) l).getMode().equals(mode);

    }
}