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

package org.matsim.trento.savPricing;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.matsim.core.utils.io.UncheckedIOException;
import org.opengis.feature.simple.SimpleFeature;

/**
* @author ikaddoura
*/

public final class BerlinShpUtils {

	private Map<Integer, Geometry> carRestrictedAreaGeometries;
	private Map<Integer, Geometry> serviceAreaGeometries;

	public BerlinShpUtils(String carRestrictedAreaShpFile, String drtServiceAreaShapeFile) {
		
		if (carRestrictedAreaShpFile != null && carRestrictedAreaShpFile != "" && carRestrictedAreaShpFile != "null" ) {
			this.carRestrictedAreaGeometries = loadShapeFile(carRestrictedAreaShpFile);
		}
		
		if (drtServiceAreaShapeFile != null && drtServiceAreaShapeFile != "" && drtServiceAreaShapeFile != "null" ) {
			this.serviceAreaGeometries = loadShapeFile(drtServiceAreaShapeFile);
		}
	}

	public BerlinShpUtils(String serviceAreaShapeFile) {
		this(null, serviceAreaShapeFile);
	}

	private Map<Integer, Geometry> loadShapeFile(String shapeFile) {
		Map<Integer, Geometry> geometries = new HashMap<>();

		Collection<SimpleFeature> features = null;
		if (new File(shapeFile).exists()) {
			features = ShapeFileReader.getAllFeatures(shapeFile);	
		} else {
			try {
				features = getAllFeatures(new URL(shapeFile));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		if (features == null) throw new RuntimeException("Aborting...");
		int featureCounter = 0;
		for (SimpleFeature feature : features) {
			geometries.put(featureCounter, (Geometry) feature.getDefaultGeometry());
			featureCounter++;
		}
		return geometries;
	}

	public boolean isCoordInCarRestrictedArea(Coord coord) {
		return isCoordInArea(coord, carRestrictedAreaGeometries);
	}

	public boolean isCoordInDrtServiceArea(Coord coord) {
		return isCoordInArea(coord, serviceAreaGeometries);
	}

	private boolean isCoordInArea(Coord coord, Map<Integer, Geometry> areaGeometries) {
		boolean coordInArea = false;
		for (Geometry geometry : areaGeometries.values()) {
			Point p = MGC.coord2Point(coord);

			if (p.within(geometry)) {
				coordInArea = true;
			}
		}
		return coordInArea;
	}
	
	public static Collection<SimpleFeature> getAllFeatures(final URL url) {
		try {
			FileDataStore store = FileDataStoreFinder.getDataStore(url);
			SimpleFeatureSource featureSource = store.getFeatureSource();

			SimpleFeatureIterator it = featureSource.getFeatures().features();
			List<SimpleFeature> featureSet = new ArrayList<SimpleFeature>();
			while (it.hasNext()) {
				SimpleFeature ft = it.next();
				featureSet.add(ft);
			}
			it.close();
			store.dispose();
			return featureSet;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public Point getRandomPointInServiceArea(Random random) {
		return getRandomPointInFeature(random, serviceAreaGeometries.get(random.nextInt(serviceAreaGeometries.size())));
	}
	
	private static Point getRandomPointInFeature(Random rnd, Geometry g)
    {
        Point p = null;
        double x, y;
        do {
            x = g.getEnvelopeInternal().getMinX() + rnd.nextDouble()
                    * (g.getEnvelopeInternal().getMaxX() - g.getEnvelopeInternal().getMinX());
            y = g.getEnvelopeInternal().getMinY() + rnd.nextDouble()
                    * (g.getEnvelopeInternal().getMaxY() - g.getEnvelopeInternal().getMinY());
            p = MGC.xy2Point(x, y);
        }
        while (!g.contains(p));
        return p;
    }
	
}