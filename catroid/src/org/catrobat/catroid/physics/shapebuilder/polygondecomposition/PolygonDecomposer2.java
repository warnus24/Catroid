package org.catrobat.catroid.physics.shapebuilder.polygondecomposition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;

public class PolygonDecomposer2 {

	public void decompose(Polygon rootPolygon, List<Polygon> decomposedPolygon) {
		List<List<PriorityCoordinate>> priorityCoordinates = new ArrayList<List<PriorityCoordinate>>();

		List<Integer> reflexIndices = Utils.findReflexVertices(rootPolygon);
		if (reflexIndices.size() == 0) { // polygon is
			// convex //split up to 8 vertex maximum!!! TODO
			//			decomposedPolygon.add(rootPolygon);
			splitToBox2DMax(rootPolygon, decomposedPolygon);
			return;
		}

		Coordinate[] coords = rootPolygon.getCoordinates();
		for (int i = 0; i < coords.length; i++) {
			List<PriorityCoordinate> currVertexCoordinates = new ArrayList<PriorityCoordinate>();
			for (int j = 0; j < coords.length; j++) {
				PriorityCoordinate pCoord = new PriorityCoordinate(rootPolygon, i, j);
				if (!pCoord.isReflex()) {
					break;
				}
				if (pCoord.isVisible()) {
					if (pCoord.isBothInRange()) {
						List<Polygon> splitted = new ArrayList<Polygon>();
						Utils.splitPolygon(rootPolygon, pCoord.getIndex(), pCoord.getOtherIndex(), splitted);

						for (Polygon poly : splitted) {
							decompose(poly, decomposedPolygon);
						}
						return;
					}
					if (pCoord.isOtherInRange()) {
						currVertexCoordinates.add(pCoord);
					}
				}
			}
			Collections.sort(currVertexCoordinates);
			if (currVertexCoordinates.size() > 0) {
				priorityCoordinates.add(currVertexCoordinates);
			}
		}

		// TODO split when only one reflex vertex and no other vertex in range
		List<Polygon> splitted = new ArrayList<Polygon>();
		int reflexIndex = Utils.findReflexVertices(rootPolygon).get(0);
		Utils.splitPolygon(rootPolygon, reflexIndex, splitted);
		for (Polygon poly : splitted) {
			decompose(poly, decomposedPolygon);
		}
	}

	private void splitToBox2DMax(Polygon rootPolygon, List<Polygon> splitted) {
		//split to maximum 8 vertices
	}

}
