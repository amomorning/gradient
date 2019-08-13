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
	WB_Render3D render;
	private static final WB_GeometryFactory gf = new WB_GeometryFactory();

	int numberOfPoints;

	float u = 1;

	public MeshBlock(PApplet app, WB_Coord... points) {
		numberOfPoints = points.length;
		pts = new ArrayList<>();
		for (WB_Coord p : points) {
			pts.add(new WB_Point(p));
		}
		ply = new WB_Polygon(pts);

		mesh = new HE_Mesh(new HEC_Polygon(ply, 0));

		render = new WB_Render3D(app);
	}

	public MeshBlock(PApplet app, WB_Polygon poly) {
		numberOfPoints = poly.getNumberOfPoints();
		ply = poly;
		pts = new ArrayList<>();
		for (WB_Coord p : poly.getPoints().toArray()) {
			pts.add(new WB_Point(p));
		}

		mesh = new HE_Mesh(new HEC_Polygon(ply, 0));

		render = new WB_Render3D(app);
	}

	public void display(PGraphics app) {
		app.fill(getColor());
		app.noStroke();
		render.drawFaces(resultMeshes);

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
			WB_Polygon hole = Tools.JTSOffset(ply, 0, 20);
			
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
	

}
