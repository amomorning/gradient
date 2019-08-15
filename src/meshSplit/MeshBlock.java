/**
 * 
 */
package meshSplit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import processing.core.PApplet;
import processing.core.PGraphics;
import utils.Tools;
import wblut.geom.WB_Coord;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Segment;
import wblut.geom.WB_Transform;
import wblut.geom.WB_Vector;
import wblut.hemesh.HEC_Polygon;
import wblut.hemesh.HET_MeshOp;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Mesh;
import wblut.processing.WB_Render3D;

/**
 * @author amo Aug 13, 2019
 * 
 */
public class MeshBlock {

	/**
	 * 
	 */

	ArrayList<WB_Point> pts;
	WB_Polygon ply;
	HE_Mesh mesh;
	List<HE_Mesh> resultMeshes;
	List<WB_Segment> segs;
	WB_Render3D render;
	private static final WB_GeometryFactory gf = new WB_GeometryFactory();

	int numberOfPoints;
	
	static double triLength = 60;

	double u = 1;
	
	WB_Point[] innerP;

	public MeshBlock(PApplet app, WB_Coord... points) {
		resultMeshes = new ArrayList<>();

		numberOfPoints = points.length;
		pts = new ArrayList<>();
		for (WB_Coord p : points) {
			pts.add(new WB_Point(p));
		}
		ply = new WB_Polygon(pts);

		mesh = new HE_Mesh(new HEC_Polygon(ply, 0));
		resultMeshes.add(mesh);

		System.out.println(resultMeshes.size());
		render = new WB_Render3D(app);
	}

	public MeshBlock(PApplet app, WB_Polygon poly) {
		numberOfPoints = poly.getNumberOfPoints();
		resultMeshes = new ArrayList<>();

		ply = poly;
		pts = new ArrayList<>();
		for (WB_Coord p : poly.getPoints().toArray()) {
			pts.add(new WB_Point(p));
		}

		mesh = new HE_Mesh(new HEC_Polygon(ply, 0));
		resultMeshes.add(mesh);

		render = new WB_Render3D(app);
	}

	public void display(PGraphics app) {
		app.fill(getColor());
		app.noStroke();
//		render.drawFaces(resultMeshes);

		app.stroke(0, 0, 255);
		render.drawSegment(segs);
		
		app.fill(255, 0, 0);
		render.drawPoint(innerP, 20);

	}
	public void setSegments() {
		segs = new ArrayList<WB_Segment>();
		innerP = new WB_Point[4];
		
		innerP[0] = new WB_Point();
		double temp = 1.0;
		for(int i = 0; i < 4; ++ i) {
			innerP[0].addSelf(pts.get((i+1)%4).mul(temp));
			temp /= u;
		}
		innerP[0].mulSelf((1.0-1.0/u)/(1-temp));
		System.out.println(innerP[0]);

		
		for(int i = 1; i < 4; ++ i) {
			innerP[i] = innerP[i-1].sub(pts.get(i)).mul(u);
			innerP[i].addSelf(pts.get(i));
//			innerP[i] = new WB_Point();
		}
		
		for(int i = 0; i < 4; ++ i) {
			segs.add(new WB_Segment(innerP[i], innerP[(i+1)%4]));
			segs.add(new WB_Segment(innerP[i], pts.get(i)));
		}
		
//		innerP =  
		
//		for(int i = 0; i < 4; ++ i) {
//			WB_Segment line = new WB_Segment(pts.get((i+1)%4), pts.get((i+2)%4));
//			WB_Point A = pts.get(i);
//			
//			double len = (u*0.5+0.1)*line.getLength(); 
//			System.out.println("edge length = " + len);
//			
//			for(int j = 1; j*triLength + triLength < len; ++ j) {
//				
//				WB_Point B = line.curvePoint(j*triLength/len);
//				System.out.println(A + "\n" + B);
//				segs.add(new WB_Segment(A, B));
//			}
//			
//		}
	}

	public int getColor() {
		int ret = (int) (u * 255);
		return ret;
	}
	public void MeshSplit() {
		HET_MeshOp.splitFacesTri(mesh);
		HET_MeshOp.splitFacesTri(mesh);
	}
	
	public void setHoles(double b) {
		MeshSplit();
		mesh.triangulate();
		List<HE_Face> faces = mesh.getFaces();
		resultMeshes = new ArrayList<>();
		for(HE_Face f:faces) {
			WB_Transform T = new WB_Transform(f.getFaceCenter(), f.getFaceNormal(), WB_Point.ORIGIN(), WB_Vector.Z());
			WB_Transform rT = new WB_Transform(WB_Point.ORIGIN(), WB_Vector.Z(), f.getFaceCenter(), f.getFaceNormal());

			WB_Polygon ply = new WB_Polygon(f.toPolygon().apply(T).getPoints());
			WB_Polygon hole = Tools.JTSOffset(ply, (1-u)*40 + 20, 40*(1-u) + 10);
			
			ply = createPolygonWithPolygonHoles(ply, hole);
			resultMeshes.add((new HE_Mesh(new HEC_Polygon(ply, 0))).apply(rT));
		}
	}
	
	public WB_Polygon createPolygonWithPolygonHoles(WB_Polygon ply, WB_Polygon... holes ) {
		if(holes == null || holes.length == 0) return ply;

		ArrayList<WB_Coord> points =  new ArrayList<>(ply.getPoints().toList());
		@SuppressWarnings("unchecked")
		ArrayList<WB_Coord>[] innerpoints = new ArrayList[holes.length];
		boolean flag = false;
		for(int i = 0; i < holes.length; ++ i) {
			WB_Polygon hole = holes[i];
			if(holes[i] == null) continue;
			flag = true;
			ArrayList<WB_Coord> pts = new ArrayList<>(Arrays.asList(hole.getPoints().toArray()));
//			Collections.reverse(pts);
			innerpoints[i] = pts;
		}
		if(flag) return gf.createPolygonWithHoles(points, innerpoints);
		else return ply;
	}
	
	public WB_Point getCenter() {
		return ply.getCenter();
		
	}

}
