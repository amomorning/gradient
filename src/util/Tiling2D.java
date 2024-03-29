/**
 *
 */
package util;

import controlP5.ControlP5;
import processing.core.PApplet;
import wblut.geom.WB_Point;

import java.util.Random;

/**
 * @author amo Aug 6, 2019
 *
 */
public class Tiling2D extends PApplet {

    WB_Point[] pts;
    int cnt;
    ControlP5 cp5;

    float AA;
    float BB;

    int color;

    boolean clicked = false;

    public void setup() {
        size(1600, 900);
        pts = new WB_Point[22];
        cp5 = new ControlP5(this);
        cnt = 4;

        cp5.addSlider("AA").setRange(0, 40000).setValue(25000).setPosition(20,
                40);
        cp5.addSlider("BB").setRange(0, 500).setValue(150).setPosition(20, 60);

        Random rand = new Random(21239);
        for (int i = 0; i < cnt; ++i) {
            int x = (rand.nextInt() % 900 + 900) % 900;
            int y = (rand.nextInt() % 900 + 700) % 900;
            System.out.println("x = " + x + " " + "y = " + y);
            pts[i] = new WB_Point(x, y);
        }
    }

    public void draw() {
        background(0);

        if (clicked) {
            drawBlock();
        } else {
            drawGradient();
        }

        fill(255, 0, 0);
        // for (int i = 0; i < cnt; ++i) {
        //
        // rect(pts[i].xf(), pts[i].yf(), 2f, 2f);
        // }
//		block bb = new block(300, 300, 400);
        // bb.draw(this, 0.3);
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
        return 1.0 / Math.sqrt(0.3 * Math.PI) * Math.exp(-x * x / AA);
    }

    public void drawGradient() {
        fill(255);
        noStroke();
        textSize(30);

        for (int i = 0; i < 900; i += 2) {
            for (int j = 0; j < 900; j += 2) {
                double total = 0;
                for (int k = 0; k < cnt; ++k) {
                    double dis = distance(i, j, k);
                    if (dis != 0)
                        total += normalDistribution(dis);
                }
                color = (int) (BB * total);
                if (color > 255)
                    color = 255;
                fill(255 - color);
                rect(i, j, 2, 2);

                fill(0);
                if (i % 100 == 0 && j % 100 == 0) {
                    text(" " + color, i, j);
                }
            }
        }
    }

    public void drawBlock() {
        int step = 60;
        for (int i = 0; i < 900; i += step) {
            for (int j = 0; j < 900; j += step) {
                double total = 0;
                for (int k = 0; k < cnt; ++k) {
                    double dis = distance(i + step / 2, j + step / 2, k);
                    if (dis != 0) {
                        total += normalDistribution(dis);
                    }
                }
                double color = BB * total;
                if (color > 255)
                    color = 255;
                double u = color / 255;

                Block bb = new Block(i, j, step);
                bb.draw(this, u);

            }
        }
    }

    public void keyPressed() {
        if (key == 'a') {
            clicked = !clicked;
        }
    }

}
