/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.physics.shapebuilder;

import java.util.ArrayList;
import java.util.List;

import org.catrobat.catroid.physics.PhysicsWorldConverter;
import org.catrobat.catroid.physics.shapebuilder.polygondecomposition.PolygonDecomposer2;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class ImageProcessor {

	public static List<Pixel> points;
	private static List<Vector2> resultingPoints = new ArrayList<Vector2>();
	private static Pixmap pixmap;

	public static Shape[] getShape(Pixmap pixmap) {
		points = new ArrayList<Pixel>();
		ImageProcessor.pixmap = pixmap;
		proceed();
		resultingPoints.clear();
		for (Pixel p : points) {
			Vector2 toVec2 = new Vector2(p.x, p.y);
			resultingPoints.add(toVec2);
		}
		resultingPoints = simplify(0.2f, 5);
		toCatroidCoordinates();
		return divideShape();
	}

	private static void toCatroidCoordinates() {
		List<Vector2> result = new ArrayList<Vector2>();
		for (Vector2 point : resultingPoints) {
			point.x -= pixmap.getWidth() / 2;
			point.y = pixmap.getHeight() - point.y - pixmap.getHeight() / 2;
			point = PhysicsWorldConverter.vecCatToBox2d(point);
			result.add(point);
		}
		resultingPoints = new ArrayList<Vector2>(result);
	}

	private static Shape[] divideShape() {
		Coordinate[] coords = new Coordinate[resultingPoints.size()];
		for (int i = 0; i < resultingPoints.size(); i++) {
			Vector2 vec = resultingPoints.get(i);
			coords[i] = new Coordinate(vec.x, vec.y);
		}

		GeometryFactory fact = new GeometryFactory();

		LinearRing ring = new GeometryFactory().createLinearRing(coords);
		Polygon loadedPolygon = new Polygon(ring, null, fact);

		List<Polygon> resultPolys = new ArrayList<Polygon>();

		PolygonDecomposer2 decomposer = new PolygonDecomposer2();
		decomposer.decompose(loadedPolygon, resultPolys);

		//shovel that to vectors
		List<Shape> shapes = new ArrayList<Shape>();
		for (Polygon poly : resultPolys) {
			PolygonShape currentShape = new PolygonShape();
			Coordinate[] actualCoords = poly.getCoordinates();

			Vector2[] vec = new Vector2[actualCoords.length];
			for (int i = 0; i < actualCoords.length; i++) {
				vec[i] = new Vector2((float) actualCoords[i].x, (float) actualCoords[i].y);
			}

			currentShape.set(shapes.toArray(new Vector2[shapes.size()]));
			shapes.add(currentShape);
		}

		return shapes.toArray(new Shape[shapes.size()]);
	}

	public static int proceed() {
		if (pixmap != null) {
			for (int cx = 0; cx < pixmap.getWidth(); cx++) {
				for (int cy = 0; cy < pixmap.getHeight(); cy += 2) {
					if (isBoundary(cx, cy)) {
						traverseBoundary(cx, cy);
						return 0;
					}
				}
			}
		} else {
			System.out.println("IMAGEPROCESSOR FAILD - no Image to proceed");
		}
		return 0;
	}

	public static boolean isBoundary(int x, int y) {
		// If this point doesn't have alpha we're not interested
		if (!pointHasAlpha(x, y)) {
			return false;
		}
		// Look at the 8 adjoining points, if at least one has alpha then 
		// it's a boundary
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				if (!pointHasAlpha(x + i, y + j)) {
					return true;
				}
			}
		}

		// If this point has alpha and all of the adjoining points
		// have alpha then it's not a boundary
		return false;
	}

	public static boolean pointHasAlpha(int x, int y) {
		if (x < 0 || x > (pixmap.getWidth() - 1) || y < 0 || y > (pixmap.getHeight() - 1)) {
			return false;
		} else {
			if (hasAlpha(x, y)) {
				return true;
			}
			return false;
		}
	}

	public static boolean hasAlpha(int x, int y) {
		int pixel = pixmap.getPixel(x, y);
		int alpha = pixel & 0xff;

		if (alpha > 0) {
			return true;
		}
		return false;
	}

	public static void traverseBoundary(int x, int y) {
		Pixel start = new Pixel(x, y);
		Pixel next = start;
		Pixel old = new Pixel(-1, -1);
		points.add(start);
		// Set arbitary limit to length of circumference
		int scope = (pixmap.getWidth() + pixmap.getHeight()) * 3;
		// Abbruchbedingung Winkel -----   ????
		for (int i = 0; i < 100000; i++) {
			// Save off the last position
			old = next;

			// Find the next clockwise boundary pixel
			next = findNextBounearyPoint(next);

			// Set the search start point as the previous pixel
			// to avoid loops and continue traversing the shape
			// in the same direction i.e.
			// edge:  x  first point     second point  34x
			//       x            23x                  2x
			//      x             1x                   1
			// In this diagram the numbers represent the order
			// in which the neighboring points are tested. We
			// Always start with the previous point found.
			next.setNSP(old.sub(next));
			// When we get back to the start break the loop 
			if (next.equals(start)) {
				break;
			}
			points.add(next);
		}
	}

	public static Pixel findNextBounearyPoint(Pixel p) {
		// Move clockwise around from previous point until we
		// hit another boundary cell
		for (int i = 0; i < 9; i++) {
			// Find the next pixel clockwise - the pixel remembers the last
			// position tested and will increment to the next position
			Pixel next = p.clockwise();
			if (isBoundary(next.x, next.y)) {
				return next;
			}
		}
		System.out.println("findNextBoundaryPoint :" + "Error next point not found");
		return null;

	}

	//	private ArrayList<Vertex> points = new ArrayList<Vertex>();

	//	public ArrayList<Vector2> getPoints() {
	//		return points;
	//	}

	//	private boolean isBoundary(Pixel point) {
	//		return isBoundary(point.x, point.y);
	//	}

	private static ArrayList<Vector2> simplify(float lim, int average) {
		ArrayList<Vector2> smoothedLine = new ArrayList<Vector2>();
		ArrayList<Vector2> simplifiedLine = new ArrayList<Vector2>();

		// Add the first point
		smoothedLine.add(resultingPoints.get(0));

		ArrayList<Vector2> averageVertices = new ArrayList<Vector2>();

		// Loop over the next [average] vertices and 
		// add the result to the array of smoothed points
		for (int i = 0; i < resultingPoints.size() - average; i++) {

			averageVertices.clear();
			for (int j = 0; j < average; j++) {
				averageVertices.add(resultingPoints.get(i + j));
			}
			smoothedLine.add(average(averageVertices));
		}

		float curvatureTotal = 0;
		float curvature = 0;

		for (int i = 0; i < smoothedLine.size() - 3; i++) {
			// Calculate the curvature
			curvature = curvature(smoothedLine.get(i), smoothedLine.get(i + 1), smoothedLine.get(i + 2));

			// Use a curvature accumulator to prevent cases
			// where a line curves gradually - this would be 
			// be picked up if we just used the curvature because
			// each individual curvature may be less than our limit
			curvatureTotal += curvature;

			// If the total curvature is greater than our set
			// limit then add the point to our simplified line
			if (curvatureTotal > lim) {
				curvatureTotal = 0;
				simplifiedLine.add(smoothedLine.get(i));
			}
		}

		return simplifiedLine;
	}

	private static Vector2 average(ArrayList<Vector2> vertices) {
		Vector2 average = new Vector2(0, 0);
		for (Vector2 v : vertices) {
			average = average.add(v);
		}
		average.set((int) (average.x / vertices.size()), (int) (average.y / vertices.size()));
		return average;
	}

	private static Vector2 midPoint(Vector2 v1, Vector2 v2) {
		return new Vector2((v1.x + v2.x) / 2, (v1.y + v2.y) / 2);
	}

	private static float curvature(Vector2 v1, Vector2 v2, Vector2 v3) {
		Vector2 midPoint = midPoint(v1, v3);
		return sq(midPoint.x - v2.x) + sq(midPoint.y - v2.y);
	}

	private static float sq(float f1) {
		return f1 * f1;
	}

}
