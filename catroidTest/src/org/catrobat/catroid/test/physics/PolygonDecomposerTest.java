/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.physics;

import junit.framework.TestCase;

import org.catrobat.catroid.physics.shapebuilder.PolygonDecomposer;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class PolygonDecomposerTest extends TestCase {

	@Test
	public void testFindReflexPoints() {

		Polygon testPolygon = createTestPolygon();
		PolygonDecomposer decomposer = new PolygonDecomposer();
		decomposer.decompose(testPolygon);
		assertEquals(2, decomposer.getReflexVertices().size());
	}

	private Polygon createTestPolygon() {
		Coordinate[] tmp = new Coordinate[10];
		tmp[0] = new Coordinate(6, 8);
		tmp[1] = new Coordinate(8, 7);
		tmp[2] = new Coordinate(9, 5);
		tmp[3] = new Coordinate(7, 4);
		tmp[4] = new Coordinate(9, 1);
		tmp[5] = new Coordinate(6, 1);
		tmp[6] = new Coordinate(3, 2);
		tmp[7] = new Coordinate(5, 4);
		tmp[8] = new Coordinate(3, 5);
		tmp[9] = new Coordinate(4, 7);

		GeometryFactory fact = new GeometryFactory();
		LinearRing linear = new GeometryFactory().createLinearRing(tmp);
		Polygon poly = new Polygon(linear, null, fact);
		return poly;
	}
}
