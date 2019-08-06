/**
 * 
 */
package util;

import java.util.ArrayList;

import processing.core.PApplet;
import wblut.geom.WB_Line;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Vector;

/**
 * @author amo Aug 6, 2019 
 * 
 */
public class block {

	
	WB_Point pts[] = new WB_Point[5];
	WB_Point center;
	
	WB_Point heart[] = new WB_Point[4];
	
	/**
	 * 
	 */
	public block() {
		// TODO Auto-generated constructor stub
		pts[0] = new WB_Point(200, 200);
		pts[1] = new WB_Point(400, 200);
		pts[2] = new WB_Point(400, 400);
		pts[3] = new WB_Point(200, 400);

		double xx = 0, yy = 0;
		for(int i = 0; i < 4; ++ i) {
			xx += pts[i].xd();
			yy += pts[i].yd();
		}
		pts[4] = new WB_Point(xx/4, yy/4);
	}
	
	public block(int x, int y, int a) {
		pts[0] = new WB_Point(x, y);
		pts[1] = new WB_Point(x+a, y);
		pts[2] = new WB_Point(x+a, y+a);
		pts[3] = new WB_Point(x, y+a);
		
		double xx = 0, yy = 0;
		for(int i = 0; i < 4; ++ i) {
			xx += pts[i].xd();
			yy += pts[i].yd();
		}
		pts[4] = new WB_Point(xx/4, yy/4);
	}
	
	public double distance() {
		return 0;
	}
	
	
	public void draw(PApplet app, double u) {
		app.fill(0);
		app.noStroke();
		for(int i = 0; i < 5; ++ i) {
			app.beginShape();
			app.vertex(pts[i].xf(), pts[i].yf());
			app.vertex(pts[(i+1)%4].xf(), pts[(i+1)%4].yf());
			app.vertex(pts[4].xf(), pts[4].yf());
			app.endShape();
			
			WB_Point pt = calcHeart(pts[i], pts[(i+1)%4], pts[4]);
			app.fill(255);
			app.noStroke();
			app.rect(pt.xf(), pt.yf(), 2f, 2f);
			

			WB_Polygon ply = getPolygon(pts[i], pts[(i+1)%4], pts[4], u);
			app.beginShape();
			for(int j = 0; j < ply.getNumberOfPoints(); ++ j) {
				WB_Point p = ply.getPoint(j);
				app.vertex(p.xf(), p.yf());
			}
			app.endShape();
			WB_Point foot = toPoint(pts[i], pts[(i+1)%4], pt, 0.5);
			
			app.rect(foot.xf(), foot.yf(), 2f, 2f);
			
			app.fill(0);
		}
		
		
	}
	
	public WB_Polygon getPolygon(WB_Point a, WB_Point b, WB_Point c, double u) {
		
		WB_Point pt = calcHeart(a, b, c);
		ArrayList<WB_Point> points = new ArrayList<WB_Point>();
		points.add(a);
		points.add(toPoint(a, b, pt, u));
		points.add(b);
		points.add(toPoint(b, c, pt, u));
		points.add(c);
		points.add(toPoint(c, a, pt, u));
		
		return new WB_Polygon(points);
	}
	
	private WB_Point toPoint(WB_Point a, WB_Point b, WB_Point c, double u) {
			WB_Vector v1 = b.sub(a);
			WB_Vector v2 = c.sub(a);
			double t = v1.dot(v2)/(v1.normalizeSelf());
			WB_Point foot =  a.add(v1.mul(t));
			WB_Vector v3 = c.sub(foot);
			return foot.add(v3.mul(u));
	}


	private WB_Point calcHeart(WB_Point a, WB_Point b, WB_Point c) {
		double a_edge = (b.sub(c)).normalizeSelf();
		double b_edge = (a.sub(c)).normalizeSelf();
		double c_edge = (a.sub(b)).normalizeSelf();
		return ((a.mul(a_edge).add(b.mul(b_edge)).add(c.mul(c_edge)))).div(
				a_edge + b_edge + c_edge);
	}
}
