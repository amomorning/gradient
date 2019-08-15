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
	
	Gradient grad;
	
	boolean isHole = false;
	
	public void setup() {

		size(1600, 900, P3D);
		tools = new Tools(this, len);
		// cam = new CameraController(this);
		polytrans = new PolyTrans(this);
		grad = new Gradient(polytrans.feets);

		IG.init();
		
		setMeshes();
	
		if(isHole) setHoles();
		
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
	
		List<Double> out = new ArrayList<>();
		for(MeshBlock b:meshblock) {
			b.u = grad.calcPosition(b.getCenter());
			out.add(b.u);
			
			b.setSegments();
		}
		Collections.sort(out);
		System.out.println(Collections.min(out));
		System.out.println(Collections.max(out));
		
	}
	
	private void setHoles() {
		for(MeshBlock b:meshblock) {
			b.setHoles(10);
		}
	}

	public void draw() {
		background(255);
		tools.cam.drawSystem(len);
		if(isHole) tools.cam.openLight();
		scale(-1, 1, 1);
		if(!isHole) polytrans.display(this.g);
		

		for(MeshBlock b:meshblock) {
			b.display(this.g);
//			break;
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
	
	public void keyPressed() {
		if(key == '1') {
			grad.setFunctionType(1);
		} else if(key == '2') {
			grad.setFunctionType(2);
		} else if(key == '3') {
			grad.setFunctionType(3);
		} else if(key == '0') {
			grad.isMin = !grad.isMin;
		}
		setMeshes();
		if(isHole) setHoles();
	}


}
