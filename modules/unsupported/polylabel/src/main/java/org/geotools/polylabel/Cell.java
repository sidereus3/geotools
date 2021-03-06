/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2016, Open Source Geospatial Foundation (OSGeo)
 *    
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *    
 */
package org.geotools.polylabel;

import java.util.logging.Level;

import org.geotools.geometry.jts.GeometryBuilder;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.distance.DistanceOp;


/**
 * Based on Vladimir Agafonkin's Algorithm https://www.mapbox.com/blog/polygon-center/
 * 
 * Implementation of quadtree cells for "Pole of inaccessibility". 
 * 
 * @author Ian Turton
 * @author Casper Børgesen
 * 
 */
public class Cell implements Comparable<Cell> {
    static final private GeometryBuilder gb = new GeometryBuilder();

    private static final double SQRT2 = 1.4142135623730951;

    private double x;

    private double y;

    private double h;

    private double d;

    private double max;

    Cell(double x, double y, double h, MultiPolygon polygon) {

        this.setX(x); // cell center x
        this.setY(y); // cell center y
        this.setH(h); // half the cell size
        Point p = gb.point(x, y);

        // distance from cell center to polygon
        this.setD(pointToPolygonDist(p, polygon)); 
        
        // max distance to polygon within a cell
        this.setMax(this.getD() + this.getH() * SQRT2); 
    }

    @Override
    public int compareTo(Cell o) {

        return (int) (o.getMax() - getMax());
    }

    public Point getPoint() {
        return gb.point(x, y);
    }

    // signed distance from point to polygon outline (negative if point is
    // outside)
    private double pointToPolygonDist(Point point, MultiPolygon polygon) {
        boolean inside = polygon.contains(point);
        double dist = DistanceOp.distance(point, polygon.getBoundary());

        // Points outside has a negative distance and thus will be weighted down later.
        return (inside ? 1 : -1) * dist;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
