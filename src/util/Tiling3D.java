package util;

import java.util.List;

import igeo.ISurface;
import igeo.IVec;
import processing.core.PApplet;
import utils.Tools;
import wblut.geom.WB_Coord;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Vector;

public class Tiling3D extends PApplet {
	PolyTrans polytrans;
	Tools tools;
	public static int len = 10000;
	float A, AA, BB, CC;
	private boolean clicked;

	public void setup() {
		size(1600, 900, P3D);
		tools = new Tools(this, len);
		// cam = new CameraController(this);
		polytrans = new PolyTrans(this);

		for(int i = 0; i < 4; ++ i) {
//			System.out.println(polytrans.feets[i]);
			System.out.println(polytrans.polygons.get(0).getPoint(i));
		}
		setSliders();
	}

	public void draw() {
		background(0, 0, 255);
		// tools.cam.drawSystem(len);
		scale(-1, 1, 1);
		polytrans.display(this.g);

		draw3DRender();
		tools.drawCP5();
	}

	private void setSliders() {
		tools.cp5.addSlider("AA").setRange(0, 40000).setValue(25000)
				.setPosition(20, 40);
tools.cp5.addSlider("A").setRange(0, 40000).setValue(25000)
				.setPosition(20, 100);
		tools.cp5.addSlider("BB").setRange(0, 1).setValue(0.82f).setPosition(20,
				60);

		tools.cp5.addSlider("CC").setRange(0, 1).setValue(0.22f).setPosition(20,
				80);
	}
	
	private double normalDistribution(double x) {
		return 1.0 / Math.sqrt(0.3 * Math.PI) * Math.exp(-x * x / AA);
	}
	
	private void draw3DRender() {
		List<WB_Polygon> ply = polytrans.polygons;
		double mn = 1e15, mx = 0;
		for(int i = 0; i < ply.size(); ++ i) {
			Block3D b = new Block3D(ply.get(i).getPoints().toArray(), this);
			double total = 0;
			for(int j = 0; j < 4; ++ j) {
				total += normalDistribution(distance(b.pts[4], polytrans.feets[j]));
			}
			System.out.println("total = " + total);
			mn = Math.min(mn, total);
			mx = Math.max(mx, total);
//			double u = total;
//			b.display(this.g, u);
		}
		System.out.println(mn + " " + mx);
		for(int i = 0; i < ply.size(); ++ i) {
			Block3D b = new Block3D(ply.get(i).getPoints().toArray(), this);
			double total = 0;
			for(int j = 0; j < 4; ++ j) {
				total += normalDistribution(distance(b.pts[4], polytrans.feets[j]));
			}
			double u = (total - mn)/(mx - mn)*BB+CC; 
			b.display(this.g, u);
		}
	}
	
	public static void savePolygonWithHolesAsSurf(WB_Polygon shellPoly, List<WB_Polygon> innnerPolys, int layer) {
		int shellPtsNum = shellPoly.getNumberOfPoints();
		IVec[] shellPts = new IVec[shellPtsNum];

		for (int i = 0; i < shellPtsNum; i++) {
			WB_Point pt = shellPoly.getPoint(i);
			shellPts[i] = new IVec(pt.xf(), pt.yf(), pt.zf());
		}
		int holeNum = innnerPolys.size();
		IVec[][] innerPts = new IVec[holeNum][];

		for (int i = 0; i < holeNum; i++) {

			List<WB_Coord> pts = innnerPolys.get(i).getPoints().toList();
			IVec[] ptsIG = new IVec[pts.size()];
			for (int j = 0; j < pts.size(); j++) {
				WB_Point pt = (WB_Point) pts.get(j);
				ptsIG[j] = new IVec(pt.xf(), pt.yf(), pt.zf());
			}
			innerPts[i] = ptsIG;
		}

		try {
			new ISurface(shellPts, innerPts).layer(""+layer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private double distance(WB_Point a, WB_Point b) {
		return WB_Vector.getDistance2D(a,  b) * (a.zd() - b.zd())/A/A;
	}

	private void getPrint() {
	}
	
	private void drawPrint() {
		List<WB_Polygon> ply = polytrans.polygons;
		List<WB_Polygon> ply_fr = polytrans.polygons_fr;
		for(int i = 0; i < ply.size(); ++ i) {
			Block3D b = new Block3D(ply.get(i).getPoints().toArray(), this);
			double total = 0;
			for(int j = 0; j < polytrans.feets.length; ++ j) {
				total += normalDistribution(distance(b.pts[4], polytrans.feets[j]));
			}
			double u = (total-BB)/(CC-BB);
			Block3D b_fr = new Block3D(ply_fr.get(i).getPoints().toArray(), this);
			b_fr.display(this.g, u);
		}	
	}
	public void keyPressed() {
		if (key == 'a') {
			clicked = !clicked;
		}

		if (key == 's') {
			List<WB_Polygon> ply = polytrans.polygons;
			for(int i = 0; i < ply.size(); ++ i) {
				Block3D b = new Block3D(ply.get(i).getPoints().toArray(), this);
				
			}
			//IG.save("testPolygonOutput.3dm");
		}
	}
}
