package org.catrobat.catroid.physics.shapebuilder.polygondecomposition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.math.Vector2D;

public class Utils {

	public static boolean isLeftTurn(Coordinate a, Coordinate b, Coordinate c) {
		return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x) > 0;
	}

	public static boolean isRightTurn(Coordinate a, Coordinate b, Coordinate c) {
		return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x) < 0;
	}

	public static int mod(int number, int mod) {
		return (number + mod) % mod;
	}

	public static boolean isInRange(Polygon polygon, int a, int b) {
		List<Coordinate> coords = Arrays.asList(polygon.getCoordinates());

		Coordinate coordA = coords.get(a);
		Coordinate coordB = coords.get(b);

		Coordinate beforeA = coords.get(Utils.mod(a - 1, coords.size()));
		Coordinate nextToA = coords.get(Utils.mod(a + 1, coords.size()));

		if (!isLeftTurn(nextToA, coordA, coordB)) {
			return false;
		}
		if (!isRightTurn(beforeA, coordA, coordB)) {
			return false;
		}

		return true;
	}

	public static void splitPolygon(Polygon rootPolygon, int index, List<Polygon> splitted) {
		System.out.println();
		// winkelsymmetrale berechnen
		int size = rootPolygon.getCoordinates().length;
		Coordinate v = rootPolygon.getCoordinates()[index];
		Coordinate u = rootPolygon.getCoordinates()[mod(index - 1, size)];
		Coordinate w = rootPolygon.getCoordinates()[mod(index + 1, size)];

		Vector2D vecUV = new Vector2D(u, v).normalize();
		Vector2D vecWV = new Vector2D(w, v).normalize();
		Vector2D vecV = new Vector2D(v);

		Vector2D angularBisector = vecUV.add(vecWV).normalize();

		Vector2D helper = vecV.add(angularBisector);

		GeometryFactory fact = new GeometryFactory();
		Coordinate[] lineCoords = new Coordinate[2];
		lineCoords[0] = new Coordinate(v.x, v.y);
		lineCoords[1] = new Coordinate(helper.getX(), helper.getY());
		CoordinateArraySequence sequence = new CoordinateArraySequence(lineCoords);
		LineString line1 = new LineString(sequence, fact);

		Coordinate[] polyCoords = rootPolygon.getCoordinates();
		Coordinate resultIntersection = null;
		int cuttingIndex = 0;
		double distance = Double.MAX_VALUE;
		for (int i = 0; i < size - 1; i++) {
			Coordinate intersection = calcIntersection(lineCoords[0], lineCoords[1], polyCoords[i], polyCoords[i + 1]);
			if (intersection != null && !intersection.equals2D(v)) {
				Vector2D dir = new Vector2D(lineCoords[0], intersection).normalize();
				if (Math.toDegrees(dir.angle(angularBisector)) < 5) {
					double currentDistance = intersection.distance(v);
					if (currentDistance < distance) {
						distance = currentDistance;
						resultIntersection = intersection;
						cuttingIndex = i;
					}
				}
			}
		}

		// splitting it up
		Coordinate[] extendedPolygon = new Coordinate[polyCoords.length + 1];
		for (int i = 0; i <= cuttingIndex; i++) {
			extendedPolygon[i] = polyCoords[i];
		}
		extendedPolygon[cuttingIndex + 1] = resultIntersection;
		for (int i = cuttingIndex + 2; i < size + 1; i++) {
			extendedPolygon[i] = polyCoords[i - 1];
		}

		LinearRing extPoly = new GeometryFactory().createLinearRing(extendedPolygon);
		Polygon extPolygon = new Polygon(extPoly, null, fact);

		splitPolygon(extPolygon, index, cuttingIndex + 1, splitted);
	}

	private static Coordinate calcIntersection(Coordinate a, Coordinate b, Coordinate c, Coordinate d) {

		double x1 = a.x;
		double y1 = a.y;
		double x2 = b.x;
		double y2 = b.y;
		double x3 = c.x;
		double y3 = c.y;
		double x4 = d.x;
		double y4 = d.y;

		double zx = (x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4);
		double zy = (x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4);

		double n = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

		double x = zx / n;
		double y = zy / n;

		if (Double.isNaN(x) & Double.isNaN(y)) {
			return null;
		}
		// if ((x - x1) / (x2 - x1) > 1 || (x - x3) / (x4 - x3) > 1 || (y - y1)
		// / (y2 - y1) > 1 || (y - y3) / (y4 - y3) > 1 )
		// {
		// return null;
		// }
		return new Coordinate(x, y);

	}

	public static void splitPolygon(Polygon polygon, int x, int y, List<Polygon> result) {
		Coordinate[] origCoords = polygon.getCoordinates();
		List<Coordinate> leftPoly = new ArrayList<Coordinate>();
		List<Coordinate> rightPoly = new ArrayList<Coordinate>();

		int a = Math.min(x, y);
		int b = Math.max(x, y);

		int i = 0;
		while (i < a) {
			leftPoly.add(origCoords[i]);
			i++;
		}
		leftPoly.add(origCoords[a]);
		rightPoly.add(origCoords[a]);
		i++;
		while (i < b) {
			rightPoly.add(origCoords[i]);
			i++;
		}
		leftPoly.add(origCoords[b]);
		rightPoly.add(origCoords[b]);
		i++;
		while (i < origCoords.length) {
			leftPoly.add(origCoords[i]);
			i++;
		}
		rightPoly.add(origCoords[a]);

		GeometryFactory fact = new GeometryFactory();

		Coordinate[] leftArray = leftPoly.toArray(new Coordinate[leftPoly.size()]);
		LinearRing leftLinear = new GeometryFactory().createLinearRing(leftArray);
		Polygon leftPolygon = new Polygon(leftLinear, null, fact);

		Coordinate[] rightArray = rightPoly.toArray(new Coordinate[rightPoly.size()]);
		LinearRing rightLinear = new GeometryFactory().createLinearRing(rightArray);
		Polygon rightPolygon = new Polygon(rightLinear, null, fact);

		result.add(leftPolygon);
		result.add(rightPolygon);
	}

	public static List<Integer> findReflexVertices(Polygon polygon) {
		int size = polygon.getCoordinates().length;
		List<Integer> reflexVertices = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {
			Coordinate a = polygon.getCoordinates()[i];
			Coordinate b = polygon.getCoordinates()[Utils.mod(i + 1, size)];
			Coordinate c = polygon.getCoordinates()[Utils.mod(i + 2, size)];
			if (Utils.isLeftTurn(a, b, c)) {
				reflexVertices.add(Utils.mod(i + 1, size));
			}
		}
		return reflexVertices;
	}

	//	public static Graphics2D drawPolygon(Polygon polygon, String folder, String filename) throws IOException {
	//		BufferedImage bufImg = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
	//
	//		Graphics2D g = bufImg.createGraphics();
	//		g.setBackground(Color.black);
	//		g.setColor(Color.white);
	//		g.setStroke(new BasicStroke(1));
	//
	//		int size = polygon.getCoordinates().length;
	//		for (int i = 0; i < size - 1; i++) {
	//			Coordinate a = polygon.getCoordinates()[i];
	//			Coordinate b = polygon.getCoordinates()[i + 1];
	//			g.draw(new Line2D.Double(a.x, a.y, b.x, b.y));
	//		}
	//		Coordinate a = polygon.getCoordinates()[size - 1];
	//		Coordinate b = polygon.getCoordinates()[0];
	//		g.draw(new Line2D.Double(a.x, a.y, b.x, b.y));
	//
	//		File folderFile = new File("res/" + folder);
	//		folderFile.mkdirs();
	//		boolean boo = ImageIO.write(bufImg, "jpg", new File(folderFile, filename + ".jpg"));
	//		return g;
	//	}
	//
	//	public static Graphics2D drawPolygon(List<Polygon> polygons, String folder, String filename) throws IOException {
	//		BufferedImage bufImg = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
	//
	//		Graphics2D g = bufImg.createGraphics();
	//		g.setBackground(Color.black);
	//		g.setColor(Color.white);
	//		g.setStroke(new BasicStroke(1));
	//
	//		for (Polygon polygon : polygons) {
	//			int size = polygon.getCoordinates().length;
	//			for (int i = 0; i < size - 1; i++) {
	//				Coordinate a = polygon.getCoordinates()[i];
	//				Coordinate b = polygon.getCoordinates()[i + 1];
	//				g.draw(new Line2D.Double(a.x, a.y, b.x, b.y));
	//			}
	//			Coordinate a = polygon.getCoordinates()[size - 1];
	//			Coordinate b = polygon.getCoordinates()[0];
	//			g.draw(new Line2D.Double(a.x, a.y, b.x, b.y));
	//		}
	//
	//		File folderFile = new File(folder);
	//		folderFile.mkdirs();
	//		boolean boo = ImageIO.write(bufImg, "jpg", new File(folderFile, filename + ".jpg"));
	//		return g;
	//	}

	public static void savePolygonToXML(Polygon original, List<Polygon> decomposed, String path, String filename,
			long duration) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		File folder = new File(path);
		folder.mkdirs();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element root = doc.createElement("PolygonDecomposition");
		root.setAttribute("size", Integer.toString(original.getCoordinates().length));
		root.setAttribute("subPolygons", Integer.toString(decomposed.size()));
		root.setAttribute("duration", Long.toString(duration));
		doc.appendChild(root);

		Element origElement = doc.createElement("OriginalPolygon");
		root.appendChild(origElement);

		addPolygonTag(original, doc, origElement);

		Element decomposedElement = doc.createElement("DecomposedPolygon");
		root.appendChild(decomposedElement);

		for (int i = 0; i < decomposed.size(); i++) {
			Polygon poly = decomposed.get(i);
			Element actPoly = doc.createElement("Polygon");
			actPoly.setAttribute("id", Integer.toString(i));
			decomposedElement.appendChild(actPoly);
			addPolygonTag(poly, doc, actPoly);
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);

		StreamResult result = new StreamResult(new File(path + filename));

		transformer.transform(source, result);
	}

	private static void addPolygonTag(Polygon original, Document doc, Element origElement) {
		Coordinate[] origCoords = original.getCoordinates();
		for (int i = 0; i < origCoords.length; i++) {
			Coordinate coord = origCoords[i];
			Element vertex = doc.createElement("Vertex");
			vertex.setAttribute("id", Integer.toString(i));

			Element xcoord = doc.createElement("x");
			xcoord.setTextContent(Double.toString(coord.x));
			vertex.appendChild(xcoord);

			Element ycoord = doc.createElement("y");
			ycoord.setTextContent(Double.toString(coord.y));
			vertex.appendChild(ycoord);

			origElement.appendChild(vertex);
		}
	}

	public static Polygon loadPolygonFromXML(String filename) throws ParserConfigurationException, SAXException,
			IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(filename));

		NodeList orig = doc.getElementsByTagName("OriginalPolygon");
		Node polyNode = orig.item(0);

		NodeList vertices = polyNode.getChildNodes();
		Coordinate[] coordinates = new Coordinate[vertices.getLength()];
		for (int i = 0; i < vertices.getLength(); i++) {
			Node node = vertices.item(i);

			Node xnode = node.getFirstChild();
			Node ynode = node.getLastChild();

			double x = Double.parseDouble(xnode.getTextContent());
			double y = Double.parseDouble(ynode.getTextContent());

			coordinates[i] = new Coordinate(x, y);
		}

		GeometryFactory fact = new GeometryFactory();

		LinearRing ring = new GeometryFactory().createLinearRing(coordinates);
		Polygon loadedPolygon = new Polygon(ring, null, fact);

		return loadedPolygon;
	}
}
