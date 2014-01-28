package org.catrobat.catroid.physics.shapebuilder.polygondecomposition;

import java.util.Arrays;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.geom.prep.PreparedGeometry;
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory;

public class PriorityCoordinate extends Coordinate {

	private static final long serialVersionUID = 1L;
	private int myIndex;
	private int otherIndex;
	private Coordinate myCoordinate;
	private Coordinate otherCoordinate;
	private Polygon myPolygon;

	private boolean isReflex;
	private boolean isBothReflex;
	private boolean isVisible;
	private boolean isinRange;
	private boolean isOtherInRange;
	private boolean isBothInRange;

	public PriorityCoordinate(Polygon polygon, int myIndex, int otherIndex) {
		this.myIndex = myIndex;
		this.otherIndex = otherIndex;
		this.myCoordinate = polygon.getCoordinates()[myIndex];
		this.otherCoordinate = polygon.getCoordinates()[otherIndex];
		this.myPolygon = polygon;

		this.x = myCoordinate.x;
		this.y = myCoordinate.y;

		isReflex = computeIsReflex();
		isBothReflex = computeIsBothReflex();
		isVisible = computeIsVisibleFrom();
		isinRange = computeIsThisInRange();
		isOtherInRange = computeIsOtherInRange();
		isBothInRange = isinRange && isOtherInRange;
	}

	public boolean isReflex() {
		return isReflex;
	}

	public boolean isBothReflex() {
		return isBothReflex;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public boolean isInRange() {
		return isinRange;
	}

	public boolean isOtherInRange() {
		return isOtherInRange;
	}

	public boolean isBothInRange() {
		return isBothInRange;
	}

	public int getIndex() {
		return myIndex;
	}

	public int getOtherIndex() {
		return otherIndex;
	}

	@Override
	public int compareTo(Object o) {
		if (o == null) {
			throw new NullPointerException();
		}
		PriorityCoordinate other = (PriorityCoordinate) o;
		if (other.getIndex() != myIndex) {
			throw new IllegalArgumentException("Comparing two non comparable coordinates");
		}

		// if one is visible but the other is not return the visible one
		if (isVisible && !other.isVisible()) {
			return -1;
		}
		if (!isVisible && other.isVisible()) {
			return 1;
		}
		if (!isVisible && !other.isVisible()) {
			return 0;
		}
		// both visible!!!

		// if one is viceversa in range the other is not return the viceversa
		// one
		if (isBothInRange && !other.isBothInRange()) {
			return -1;
		}
		if (!isBothInRange && other.isBothInRange()) {
			return 1;
		}

		// if one is in range the other is not retutn the in range one
		if (isinRange && !other.isInRange()) {
			return -1;
		}
		if (!isinRange && other.isInRange()) {
			return 1;
		}

		// if one is reflex the other is not return reflex one
		if (isReflex && !other.isReflex()) {
			return -1;
		}
		if (!isReflex && other.isReflex()) {
			return 1;
		}

		// if all are equal return the one with lower distance
		if (distance() < other.distance()) {
			return -1;
		} else if (distance() > other.distance()) {
			return 1;
		}

		return 0;
	}

	public double distance() {
		return Math.sqrt(Math.pow(myCoordinate.x - otherCoordinate.x, 2)
				+ Math.pow(myCoordinate.y - otherCoordinate.y, 2));
	}

	private boolean computeIsBothReflex() {
		return computeIsReflex() && isOtherReflex();
	}

	private boolean computeIsReflex() {
		List<Coordinate> coords = Arrays.asList(myPolygon.getCoordinates());

		Coordinate coordA = coords.get(myIndex);

		Coordinate beforeA = coords.get(Utils.mod(myIndex - 1, coords.size()));
		Coordinate nextToA = coords.get(Utils.mod(myIndex + 1, coords.size()));

		if (Utils.isLeftTurn(beforeA, coordA, nextToA)) {
			return true;
		}
		return false;
	}

	private boolean isOtherReflex() {
		List<Coordinate> coords = Arrays.asList(myPolygon.getCoordinates());

		Coordinate coordB = coords.get(otherIndex);

		Coordinate beforeB = coords.get(Utils.mod(otherIndex - 1, coords.size()));
		Coordinate nextToB = coords.get(Utils.mod(otherIndex + 1, coords.size()));

		if (Utils.isLeftTurn(beforeB, coordB, nextToB)) {
			return true;
		}
		return false;
	}

	private boolean computeIsVisibleFrom() {
		GeometryFactory factory = new GeometryFactory(new PrecisionModel(100.0), 31300);
		PreparedGeometry prepGeometry = PreparedGeometryFactory.prepare(myPolygon);
		CoordinateArraySequence coordSeq = new CoordinateArraySequence(new Coordinate[] { myCoordinate,
				myPolygon.getCoordinates()[otherIndex] });

		LineString line = new LineString(coordSeq, factory);

		if (prepGeometry.contains(line)) {
			return true;
		}

		return false;
	}

	private boolean computeIsOtherInRange() {
		List<Coordinate> coords = Arrays.asList(myPolygon.getCoordinates());

		Coordinate coordA = coords.get(myIndex);
		Coordinate coordB = coords.get(otherIndex);

		Coordinate beforeA = coords.get(Utils.mod(myIndex - 1, coords.size()));
		Coordinate nextToA = coords.get(Utils.mod(myIndex + 1, coords.size()));

		if (Utils.isRightTurn(nextToA, coordA, coordB)) {
			return false;
		}
		if (Utils.isLeftTurn(beforeA, coordA, coordB)) {
			return false;
		}

		return true;
	}

	private boolean computeIsThisInRange() {
		// TODO is this correct?
		List<Coordinate> coords = Arrays.asList(myPolygon.getCoordinates());

		Coordinate coordA = coords.get(myIndex);
		Coordinate coordB = coords.get(otherIndex);

		Coordinate beforeB = coords.get(Utils.mod(otherIndex - 1, coords.size()));
		Coordinate nextToB = coords.get(Utils.mod(otherIndex + 1, coords.size()));

		if (Utils.isRightTurn(nextToB, coordB, coordA)) {
			return false;
		}
		if (Utils.isLeftTurn(beforeB, coordB, coordA)) {
			return false;
		}

		return true;
	}
}
