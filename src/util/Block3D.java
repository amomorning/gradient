/**
 * 
 */
package util;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PGraphics;
import utils.Tools;
import wblut.geom.WB_Coord;
import wblut.geom.WB_GeometryFactory2D;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Vector;
import wblut.processing.WB_Render3D;

/**
 * @author amo Aug 11, 2019
 * 
 */
public class Block3D {

	/**
	 * 
	 */

	WB_Point pts[] = new WB_Point[5];
	WB_Point heart[] = new WB_Point[4];
	WB_Polygon ply[] = new WB_Polygon[4];
	WB_Render3D hpp;
	public Block3D(WB_Coord[] plypt, PApplet app) {
		// TODO Auto-generated constructor stub
		for (int i = 0; i < 4; ++i)
			pts[i] = new WB_Point(plypt[i]);
		pts[4] = pts[0].add(pts[2]).div(2);
		hpp = new WB_Render3D(app);

	}

	/**
	 *
	 * @param:
	 * @return:void
	 * @throws:
	 */
	public void display(PGraphics app, double u) {
		calcPolygon(u);
		app.stroke(0, 0, 255);
		app.fill(255);
		for (int i = 0; i < 4; ++i) {
			if(ply[i] != null) hpp.drawPolygonEdges(ply[i]);
		}
		// TODO Auto-generated method stub
		app.noFill();
	}

	public void calcPolygon(double u) {
		for (int i = 0; i < 4; ++i) {
			WB_Polygon p = getPolygon(pts[i], pts[(i + 1) % 4], pts[4], u);
			ply[i] = p;
		}
	}

	public WB_Polygon getPolygon(WB_Point a, WB_Point b, WB_Point c, double u) {

		WB_Point pt = calcHeart(a, b, c);
		ArrayList<WB_Point> points = new ArrayList<WB_Point>();
		
		double t = 0.01;
		points.add(movePoint(a, b, c, t));
		points.add(toPoint(a, b, pt, u));
		points.add(movePoint(b, a, c, t));
		points.add(toPoint(b, c, pt, u));
		points.add(movePoint(c, b, a, t));
		points.add(toPoint(c, a, pt, u));

		return new WB_Polygon(points);
	}
	
	private WB_Point movePoint(WB_Point a, WB_Point b, WB_Point c, double u) {
		WB_Vector v1 = b.sub(a);
		WB_Vector v2 = c.sub(a);
		WB_Vector v3 = v1.add(v2);
		return a.add(v3.mul(u));
	}

	private WB_Point toPoint(WB_Point a, WB_Point b, WB_Point c, double u) {
		WB_Vector v1 = b.sub(a);
		WB_Vector v2 = c.sub(a);
		double t = v1.dot(v2) / (v1.normalizeSelf());
		WB_Point foot = a.add(v1.mul(t));
		WB_Vector v3 = c.sub(foot);
		return foot.add(v3.mul(u));
	}

	private WB_Point calcHeart(WB_Point a, WB_Point b, WB_Point c) {
		double a_edge = (b.sub(c)).normalizeSelf();
		double b_edge = (a.sub(c)).normalizeSelf();
		double c_edge = (a.sub(b)).normalizeSelf();
		return ((a.mul(a_edge).add(b.mul(b_edge)).add(c.mul(c_edge))))
				.div(a_edge + b_edge + c_edge);
	}

}
