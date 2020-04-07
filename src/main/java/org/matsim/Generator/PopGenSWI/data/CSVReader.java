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

package org.matsim.Generator.PopGenSWI.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CSVReader {
	final private String separator;
	
	public CSVReader(String separator) {
		this.separator = separator;
	}
	
	public List<List<Integer>> load(File path, List<String> columns) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
		
		String line = null;
		List<String> header = null;
		List<String> row = null;
		
		List<List<Integer>> data = new LinkedList<>();
		
		while ((line = reader.readLine()) != null) {
			row = Arrays.asList(line.split(separator));
			
			if (header == null) {
				header = row;
			} else {
				List<Integer> dataRow = new ArrayList<>(columns.size());
				
				for (String column : columns) {
					dataRow.add(Integer.parseInt(row.get(header.indexOf(column))));
				}
				
				data.add(dataRow);
			}
		}		
		
		reader.close();
		
		return data;
	}
}