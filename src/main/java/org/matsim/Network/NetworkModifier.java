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

package org.matsim.Network;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.network.algorithms.TransportModeNetworkFilter;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NetworkModifier {

    public static void main (String[] args) {
        String networkInputFile = "Path to input file/network.xml.gz";
        String networkOutputFile = "Path to output file/network-v2.xml.gz";

        Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        MatsimNetworkReader reader = new MatsimNetworkReader(scenario.getNetwork());
        reader.readFile(networkInputFile);

        // Get pt subnetwork
        Scenario ptScenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        TransportModeNetworkFilter transportModeNetworkFilterPt = new TransportModeNetworkFilter(scenario.getNetwork());
        Set ptMode = new HashSet<String>();
        ptMode.add(TransportMode.pt);
        transportModeNetworkFilterPt.filter(ptScenario.getNetwork(), ptMode);

        for (Link link : new ArrayList<>(scenario.getNetwork().getLinks().values())) {
            if (link.getAllowedModes().contains(TransportMode.car)) {
                String type = link.getAttributes().getAttribute("type").toString();
                if (type.equals("tertiary") || type.equals("tertiary_link") || type.equals("unclassified")
                        || type.equals("residential") || type.equals("living_street")) {
                    scenario.getNetwork().removeLink(link.getId());
                }
            }
        }

        NetworkCleaner networkCleaner = new NetworkCleaner();
        networkCleaner.run(scenario.getNetwork());

        // Add pt back into the other network
        // Note: Customized attribute are not considered here
        NetworkFactory factory = scenario.getNetwork().getFactory();
        for (Node node : ptScenario.getNetwork().getNodes().values()) {
            Node node2 = factory.createNode(node.getId(), node.getCoord());
            scenario.getNetwork().addNode(node2);
        }
        for (Link link : ptScenario.getNetwork().getLinks().values()) {
            Node fromNode = scenario.getNetwork().getNodes().get(link.getFromNode().getId());
            Node toNode = scenario.getNetwork().getNodes().get(link.getToNode().getId());
            Link link2 = factory.createLink(link.getId(), fromNode, toNode);
            link2.setAllowedModes(link.getAllowedModes());
            link2.setCapacity(link.getCapacity());
            link2.setFreespeed(link.getFreespeed());
            link2.setLength(link.getLength());
            link2.setNumberOfLanes(link.getNumberOfLanes());
            scenario.getNetwork().addLink(link2);
        }

        NetworkWriter writer = new NetworkWriter(scenario.getNetwork());
        writer.write(networkOutputFile);
    }
}