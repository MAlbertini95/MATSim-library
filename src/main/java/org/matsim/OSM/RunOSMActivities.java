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

package org.matsim.OSM;

import org.matsim.contrib.accessibility.osm.CombinedOsmReader;
import org.matsim.core.controler.OutputDirectoryLogging;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

public class RunOSMActivities {

    public static void main(String[] args) {
    	

//       String root = "D:/Arbeit/Berlin/ReLocation/Test/";
//    	 String root = "D:/Arbeit/Berlin/ReLocation/exception/";
       String inputOSMFile = "C:/Users/teoal/Desktop/MATSim Milano/";
//        String inputOSMFile = args[0];
//        String outputFacilityFile = root + "facilities.xml.gz";
//        String outputFacilityFile = root + "combinedFacilities-test1.xml";
//        String attributeFile = root + "attributeFile-test1.xml";      
        String outputFacilityFile = "C:/Users/teoal/Desktop/MATSim Milano/Facility/Milano_facilities.xml";
        String attributeFile = "C:/Users/teoal/Desktop/MATSim Milano/Facility/attributeFile-test1.xml";
        
        OutputDirectoryLogging.catchLogEntries();
        try {
        OutputDirectoryLogging.initLoggingWithOutputDirectory("./log");
        } catch (IOException e1) {
        e1.printStackTrace();
        }
        
/*     String newCoord = args[1]; */
        String newCoord = "EPSG:32632";
        
        CombinedOsmReader activitiesReader = new CombinedOsmReader(newCoord,
                AccessibilityFacilityUtils.buildOsmLandUseToMatsimTypeMap(),
                AccessibilityFacilityUtils.buildOsmBuildingToMatsimTypeMap(),
                AccessibilityFacilityUtils.buildOsmAmenityToMatsimTypeMapV2(),
                AccessibilityFacilityUtils.buildOsmLeisureToMatsimTypeMapV2(),
                AccessibilityFacilityUtils.buildOsmTourismToMatsimTypeMapV2(),
                AccessibilityFacilityUtils.buildUnmannedEntitiesList(),
                0);

        try {
            activitiesReader.parseFile(inputOSMFile);
            activitiesReader.writeFacilities(outputFacilityFile);
            activitiesReader.writeFacilityAttributes(attributeFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Done");

    }

}