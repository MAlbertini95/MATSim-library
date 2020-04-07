/* *********************************************************************** *
 * project: org.matsim.                                                    * 
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

package org.matsim.Generator.PopGenSWI.bn;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BNGraph {
	final private int numberOfVariables;
	final private List<Integer> ordering;
	final private List<Set<Integer>> connections;
	
	public BNGraph(int numberOfVariables, List<Integer> ordering, List<Set<Integer>> connections) {
		this.numberOfVariables = numberOfVariables;
		this.ordering = ordering;
		this.connections = connections;
		
		validate();
	}
	
	public int getNumberOfVariables() {
		return numberOfVariables;
	}
	
	public List<Integer> getChildren(int variable) {
		List<Integer> children = new LinkedList<>(connections.get(ordering.indexOf(variable)));
		children.sort(Integer::compare);
		return children;
	}
	
	public List<Integer> getParents(int variable) {
		int index = ordering.indexOf(variable);
		List<Integer> parents = new LinkedList<>();
		
		for (int i = 0; i < index; i++) {
			if (connections.get(i).contains(index)) {
				parents.add(ordering.get(i));
			}
		}
		
		parents.sort(Integer::compare);
		
		return parents;
	}
	
	public Collection<Integer> getMarkovBlanket(int variable) {
		Set<Integer> blanket = new HashSet<>();
		blanket.addAll(getParents(variable));
		
		Collection<Integer> children = getChildren(variable);
		blanket.addAll(children);
		
		for (int child : children) {
			blanket.addAll(getParents(child));
		}
		
		return blanket;
	}
	
	public List<Integer> getOrdering() {
		return ordering;
	}
	
	private void validate() {
		if (ordering.size() != connections.size()) {
			throw new IllegalStateException("All dimensions need a connection list");
		}
		
		Set<Integer> variables = new HashSet<>(ordering);
		
		if (variables.size() < ordering.size()) {
			throw new IllegalStateException("There are duplicate variables.");
		}
		
		for (int i = 0; i < connections.size(); i++) {
			for (int j : connections.get(i)) {
				if (j <= i) {
					throw new IllegalStateException("Backward connection found");
				}
			}
		}
	}
	
	@Override
	public String toString() {
		String s = "[ " + String.join(",", ordering.stream().map(i -> String.valueOf(i)).collect(Collectors.toList())) + " ] ";
		
		List<String> nodes = new LinkedList<>();
		
		for (Set<Integer> connection : connections) {
			String connectionString = "{ " + String.join(", ", connection.stream().map(i -> String.valueOf(ordering.get(i))).collect(Collectors.toList())) + " }";
			nodes.add(connectionString);
		}
		
		s += String.join(", ", nodes);
		s += " ]";
		
		return s;
	}
}