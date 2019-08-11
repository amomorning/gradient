package util;

import Guo_Cam.CameraController;
import processing.core.PApplet;

public class TransTest extends PApplet {
	CameraController cam;
	PolyTrans polytrans;

	public void setup() {
		size(1600, 900, P3D);
		cam = new CameraController(this);
		polytrans = new PolyTrans(this);
		
	}

	public void draw() {
		background(255);
		scale(-1, 1, 1);
		polytrans.display(this.g);
	}
}
