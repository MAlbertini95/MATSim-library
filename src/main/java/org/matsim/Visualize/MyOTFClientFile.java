/* *********************************************************************** *
 * project: org.matsim.*
 * OTFVis.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008, 2009 by the members listed in the COPYING,  *
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

package org.matsim.Visualize;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.SwingUtilities;

import org.matsim.vis.otfvis.gui.OTFVisFrame;
import org.matsim.vis.otfvis.OTFVisConfigGroup;
import org.matsim.vis.otfvis.OTFVisConfigGroup.ColoringScheme;
import org.matsim.vis.otfvis.data.OTFClientQuadTree;
import org.matsim.vis.otfvis.data.OTFConnectionManager;
import org.matsim.vis.otfvis.data.OTFServerQuadTree;
import org.matsim.vis.otfvis.data.fileio.OTFFileReader;
import org.matsim.vis.otfvis.data.fileio.SettingsSaver;
import org.matsim.vis.otfvis.gui.OTFControlBar;
import org.matsim.vis.otfvis.gui.OTFHostControl;
import org.matsim.vis.otfvis.gui.OTFTimeLine;
import org.matsim.vis.otfvis.handler.OTFAgentsListHandler;
import org.matsim.vis.otfvis.handler.OTFLinkAgentsHandler;
import org.matsim.vis.otfvis.opengl.drawer.OTFOGLDrawer;

/**
 * @author teoal
 * This file starts OTFVis using a .mvi file.
 * 
 */

public class MyOTFClientFile implements Runnable {

	private final String url;
	
	public MyOTFClientFile(String filename) {
		super();
		this.url = filename;
		
	}

	@Override
	public final void run() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createDrawer();
			}
		});
	}

	private void createDrawer() {
		Component canvas = OTFOGLDrawer.createGLCanvas(new OTFVisConfigGroup());
		OTFFileReader otfServer = new OTFFileReader(url);
		final OTFHostControl hostControl = new OTFHostControl(otfServer, canvas);
		OTFVisConfigGroup otfVisConfig = otfServer.getOTFVisConfig();
		// new
		otfVisConfig.setRenderImages(true);
		// end new
//		otfVisConfig.setRenderImages(true);
		otfVisConfig.addParam("agentSize", "50.f"); //Initial size of the agents. Only a range of numbers is allowed, otherwise otfvis aborts or displays no agents
		otfVisConfig.addParam("linkWidth", "10.f"); //Initial) width of the links of the network. Use positive floating point values."
		otfVisConfig.addParam("drawTransitFacilities", "true"); 
		otfVisConfig.addParam("drawTransitFacilityIds", "false");
		otfVisConfig.addParam("drawNonMovingItems", "false"); //If non-moving items (e.g. agents at activities, at bus stops, etc.) should be showed
		otfVisConfig.addParam("linkwidthIsProportionalTo", "numberOfLanes"); //otherwise could be "capacity"
		otfVisConfig.addParam("coloringScheme", "taxicab");		
//		ColoringScheme coloring = ColoringScheme.taxicab; //possible values { standard, bvg, bvg2, byId, gtfs, taxicab }
//		otfVisConfig.setColoringScheme(coloring);
		OTFConnectionManager connect = new OTFConnectionManager();
		connect.connectWriterToReader(OTFLinkAgentsHandler.Writer.class, OTFLinkAgentsHandler.class);
		connect.connectWriterToReader(OTFAgentsListHandler.Writer.class, OTFAgentsListHandler.class);
		OTFServerQuadTree servQ = otfServer.getQuad(connect);
		OTFClientQuadTree clientQ = servQ.convertToClient(otfServer, connect);
		// Change pane stat opens
//		clientQ.setMinEasting(110000);
//		clientQ.setMaxEasting(0);
//		clientQ.setMinNorthing(116000);
//		clientQ.setMaxNorthing(140000);
		OTFOGLDrawer mainDrawer = new OTFOGLDrawer(clientQ, otfVisConfig, canvas, hostControl);
//		mainDrawer.setIncludeLogo(false);
//		mainDrawer.setScreenshotInterval(3600);
//		mainDrawer.setTimeOfLastScreenshot(86400);
		OTFControlBar hostControlBar = new OTFControlBar(otfServer, hostControl, mainDrawer);
		OTFVisFrame otfVisFrame = new OTFVisFrame(canvas, otfServer, hostControlBar, mainDrawer, new SettingsSaver(url));
		OTFTimeLine timeLine = new OTFTimeLine("time", hostControl);
		otfVisFrame.getContentPane().add(timeLine, BorderLayout.SOUTH);
//		otfClient.setSize(800, 800); // does not seem to take effect
//		otfClient.show();
		otfVisFrame.pack();
        otfVisFrame.setVisible(true);
	}
}