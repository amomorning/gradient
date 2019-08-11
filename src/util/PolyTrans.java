package util;

import java.util.ArrayList;
import java.util.List;

import igeo.ICurve;
import igeo.IG;
import igeo.IPoint;
import igeo.IVecI;
import processing.core.PApplet;
import processing.core.PGraphics;
import wblut.geom.WB_Coord;
import wblut.geom.WB_CoordCollection;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Segment;
import wblut.geom.WB_Transform;
import wblut.geom.WB_Vector;
import wblut.processing.WB_Render3D;

public class PolyTrans {
	static final String dataAddress = "./model/transpoly8_10.3dm";
	static final int i1 = 0, i2 = 2;
	static final int linenum = 20, offset = 1800;

	WB_Render3D hpp;
	int size;
	public WB_Point[] feets;
	public ArrayList<WB_Polygon> polygons = new ArrayList<>();// 原始多边形
	public ArrayList<WB_Segment> segments = new ArrayList<>();//
	public ArrayList<WB_Point> centers = new ArrayList<>();
	//
	public ArrayList<WB_Polygon> polygons_f = new ArrayList<>();// 转到平面的多边形
	public ArrayList<WB_Segment> segments_f = new ArrayList<>();//
	public ArrayList<WB_Point> centers_f = new ArrayList<>();
	//
	public ArrayList<WB_Polygon> polygons_fr = new ArrayList<>();// 拍平的多边形

	public PolyTrans(PApplet app) {
		hpp = new WB_Render3D(app);
		init();
		transtoPlane();
	}

	private void init() {
		IG.init();
		IG.open(dataAddress);
		polygons = IcurvestoWB_Polys3D(IG.layer("offset").curves());
		size = polygons.size();
		//
		feets = IPointstoWB_Points(IG.layer("feetpoint").points());
		for (WB_Polygon poly : polygons) {

			centers.add(poly.getPoint(i1).add(poly.getPoint(i2)).mul(0.5f));
			segments.add(new WB_Segment(poly.getPoint(i1), poly.getPoint(i2)));
		}
	}

	private void transtoPlane() {
		for (int i = 0; i < size; i++) {
			WB_Vector vec = new WB_Polygon(polygons.get(i).getPoint(0), polygons.get(i).getPoint(1),
					polygons.get(i).getPoint(2)).getNormal();
			WB_Point center_f = new WB_Point(i % linenum * offset, i / linenum * offset, 0);
			WB_Transform T = new WB_Transform(centers.get(i), vec, center_f, new WB_Vector(0, 0, 1));
			//
			WB_Polygon poly = new WB_Polygon(polygons.get(i).apply(T).getPoints());
			centers_f.add(center_f);
			polygons_f.add(poly);
			segments_f.add(new WB_Segment(poly.getPoint(i1), poly.getPoint(i2)));
			//
			polygons_fr.add(rotatePoly(poly));
		}
	}

	private WB_Polygon rotatePoly(WB_Polygon poly) {
		
		WB_Coord[] pts = poly.getPoints().toArray();
		WB_Plane oplane = new WB_Plane(pts[0], pts[2], pts[3]);
		WB_Plane tplane = new WB_Plane(pts[0], pts[1], pts[2]);
		double ang = tplane.getNormal().getAngle(oplane.getNormal());
		if (pts[3].zf() > 0)
			ang = -ang;
		WB_Point p = ((WB_Point)pts[3]).rotateAboutAxis2P(ang, pts[0], pts[2]);
		return new WB_Polygon(pts[0], pts[1], pts[2], p);
	}

	public void display(PGraphics app) {
		app.noFill();
		app.stroke(0);
		hpp.drawPolygonEdges(polygons);
		app.stroke(255, 0, 0);
		hpp.drawPoint(centers, 10);
		app.stroke(255, 0, 0);
		hpp.drawSegment(segments);
		app.fill(0, 0, 255);
		for (int i = 0; i < size; i++) {
			textDisplay(app, i + "", centers.get(i), 30);
		}
		hpp.drawPoint(feets,40);

		//
		app.noFill();
		app.stroke(0);
		hpp.drawPolygonEdges(polygons_f);
		app.stroke(255, 0, 0);
		hpp.drawSegment(segments_f);
		app.stroke(0, 0, 255);
		hpp.drawPolygonEdges(polygons_fr);
	}

	//
	public static WB_Polygon IcurvetoWB_Poly3D(ICurve curve) {
		ArrayList<WB_Point> points = new ArrayList<>();
		IVecI[] ps = curve.cps();
		for (int i = 0; i < ps.length; i++) {
			points.add(new WB_Point(ps[i].x(), -ps[i].y(), ps[i].z()));
		}
		return new WB_Polygon(points);
	}

	public static ArrayList<WB_Polygon> IcurvestoWB_Polys3D(ICurve[] curves) {
		ArrayList<WB_Polygon> polys = new ArrayList<>();
		for (int i = 0; i < curves.length; i++) {
			polys.add(IcurvetoWB_Poly3D(curves[i]));
		}
		return polys;
	}

	public static void textDisplay(PGraphics app, String str, WB_Coord pos, float size) {
		app.textSize(size);
		app.translate(pos.xf(), pos.yf(), pos.zf());
		app.text(str, 0, 0);
		app.translate(-pos.xf(), -pos.yf(), -pos.zf());
	}

	public static WB_Point[] IPointstoWB_Points(IPoint[] pts) {
		WB_Point[] points = new WB_Point[pts.length];
		for (int i = 0; i < pts.length; i++) {
			points[i] = IPointtoWB_Point(pts[i]);
		}
		return points;
	}

	public static WB_Point IPointtoWB_Point(IPoint p) {
		return new WB_Point(p.x(), -p.y(), p.z());
	}
	//
}
