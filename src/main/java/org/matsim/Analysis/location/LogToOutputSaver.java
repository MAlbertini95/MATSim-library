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

package org.matsim.Analysis.location;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.matsim.core.controler.OutputDirectoryLogging;

public class LogToOutputSaver {
	final private static Logger log = Logger.getLogger(LogToOutputSaver.class);
	
	public static void setOutputDirectory(String outputBase) {
		try	{
			OutputDirectoryLogging.initLoggingWithOutputDirectory(outputBase);
			log.info("Logging will be stored at " + outputBase);
		} catch (IOException e)	{
			log.error("Cannot create logfiles: " + e.getMessage());
			e.printStackTrace();
		}
	}
}