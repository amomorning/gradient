/**
 * 
 */
package meshSplit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import igeo.IG;
import processing.core.PApplet;
import util.PolyTrans;
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

/**
 * @author amo Aug 12, 2019 
 * 
 */
public class Main extends PApplet{

	private Tools tools;
	private PolyTrans polytrans;
	private int len = 10000;
	private int n;
	
	ArrayList<MeshBlock> meshblock;
	WB_Polygon ply, hole;
	
	HE_Mesh mesh;
	public void setup() {
		size(1600, 900, P3D);
		tools = new Tools(this, len);
		// cam = new CameraController(this);
		polytrans = new PolyTrans(this);

		IG.init();
		
		setMeshes();

//		MeshBlock b = meshblock.get(0);
//		b.mesh.triangulate();
//		HE_Face f = b.mesh.getFaceWithIndex(0);
//		WB_Transform T = new WB_Transform(f.getFaceCenter(), f.getFaceNormal(), WB_Point.ORIGIN(), WB_Vector.Z());
//		WB_Transform rT = new WB_Transform(WB_Point.ORIGIN(), WB_Vector.Z(), f.getFaceCenter(), f.getFaceNormal());
//		
//		ply = new WB_Polygon(f.toPolygon().apply(T).getPoints());
//		hole = Tools.JTSOffset(ply, 60, 50);
//
//		ply = createPolygonWithPolygonHoles(ply, hole);
//		
//
//		mesh = new HE_Mesh(new HEC_Polygon(ply, 0));
//		mesh.applySelf(rT);
//		ply = new WB_Polygon(ply.apply(rT).getPoints());
//		b.mesh.applySelf(rT);
		
		
	}
	
	
	public WB_Polygon createPolygonWithPolygonHoles(WB_Polygon ply, WB_Polygon... holes ) {

		ArrayList<WB_Coord> points =  new ArrayList<>(ply.getPoints().toList());
		@SuppressWarnings("unchecked")
		ArrayList<WB_Coord>[] innerpoints = new ArrayList[holes.length];
		for(int i = 0; i < holes.length; ++ i) {
			WB_Polygon hole = holes[i];
			ArrayList<WB_Coord> pts = new ArrayList<>(Arrays.asList(hole.getPoints().toArray()));
//			Collections.reverse(pts);
			innerpoints[i] = pts;
		}
		return (new WB_GeometryFactory()).createPolygonWithHoles(points, innerpoints);
	}
	

	/** 
	*
	* @param:
	* @return:void
	* @throws:
	*/
	private void setMeshes() {
		meshblock = new ArrayList<>();
		for(WB_Polygon ply: polytrans.polygons) {
			meshblock.add(new MeshBlock(this, ply));
		}
		
		for(MeshBlock b:meshblock) {
			b.u = 0.8f;
			b.setHoles(10);
		}
	}

	public void draw() {
		background(255);
		 tools.cam.drawSystem(len);
		tools.cam.openLight();
		scale(-1, 1, 1);
		polytrans.display(this.g);
		

		for(MeshBlock b:meshblock) {
			b.display(this.g);
		}

//		noFill();
//		stroke(255, 0, 0);
//		tools.render.drawPolygonEdges(ply);
//
//		fill(0, 0, 255);
//		noStroke();
//		tools.render.drawFaces(mesh);
		tools.drawCP5();
	}
	


}
