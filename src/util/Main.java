/**
 * 
 */
package util;

import controlP5.ControlP5;
import processing.core.*;
import utils.Tools;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.hemesh.HE_Mesh;

/**
 * @author amo Aug 6, 2019
 * 
 */
public class Main extends PApplet {

	WB_Point pts[];
	int cnt;
	ControlP5 cp5;

	float AA;
	float BB;
	
	int color;
	
	boolean clicked = false;
	public void setup() {
		size(900, 900);
		pts = new WB_Point[10];
		cp5 = new ControlP5(this);
		cnt = 0;

		cp5.addSlider("AA").setRange(0, 40000).setValue(10000).setPosition(20, 40);
		cp5.addSlider("BB").setRange(0, 500).setValue(250).setPosition(20, 60);

		for (int i = 300; i < 900; i += 300) {
			for (int j = 300; j < 900; j += 300) {
				pts[cnt++] = new WB_Point(i, j);
			}
		}


	}

	public void draw() {
		background(0);

		
		if(clicked) {
			drawBlock();
		} else {
			drawGradient();
		}
		
		fill(255, 0, 0);
		for (int i = 0; i < cnt; ++i) {
			
			rect(pts[i].xf(), pts[i].yf(), 2f, 2f);
		}
		block bb = new block(300, 300, 400);
//		bb.draw(this, 0.3);
//		
		cp5.draw();
		

	}

	private double distance(int x, int y, int i) {
		double tmp = (x - pts[i].xd()) * (x - pts[i].xd())
				+ (y - pts[i].yd()) * (y - pts[i].yd());
		return Math.sqrt(tmp);
	}
	
	
	public void mousePressed() {
		int color = get(mouseX, mouseY) & 0xFF;
		System.out.println(mouseX + " " + mouseY + " " + color);
	}
	
	public double normalDistribution(double x) {
		return 1.0/Math.sqrt(0.3*Math.PI)*Math.exp(-x*x/AA);
	}
	public void drawGradient() {
				fill(255);
		noStroke();
		textSize(30);

		for (int i = 0; i < 900; i += 2) {
			for (int j = 0; j < 900; j += 2) {
				int min = 255;
				double total = 0;
				for (int k = 0; k < cnt; ++k) {
					double dis = distance(i, j, k);
					if (dis != 0)
						total += normalDistribution(dis);
				}
				color = (int) (BB*total);
				if (color > 255)
					color = 255;
				fill(255 - color);
				rect(i, j, 2, 2);

				fill(0);
				if(i%100 == 0 && j%100 == 0) {
					text(" "+color, i, j);
				}
			}
		}
	}
	
	public void drawBlock() {
		int step = 45;
		for(int i = 0; i < 900; i += step) {
			for(int j = 0; j < 900; j += step) {
				double total = 0;
				for(int k = 0; k < cnt; ++ k) {
					double dis = distance(i+step/2, j+step/2, k);
					if(dis != 0) {
						total += normalDistribution(dis);
					}
				}
				double color = BB*total;
				if(color > 255) color = 255;
				double u = color/255;
				
				block bb = new block(i, j, step);
				bb.draw(this, u);
				
			}
		}
	}

	public void keyPressed() {
		if(key == 'a') {
			clicked = !clicked;
		}
	}
	
 }
