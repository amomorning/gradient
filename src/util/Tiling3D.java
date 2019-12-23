package util;

import igeo.IG;
import igeo.ISurface;
import igeo.IVec;
import meshSplit.PolyTrans;
import processing.core.PApplet;
import wblut.geom.WB_Coord;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Vector;

import java.util.ArrayList;

public class Tiling3D extends PApplet {
    public static int len = 10000;
    PolyTrans polytrans;
    Tools tools;
    float A, AA, BB, CC;
    private boolean clicked;

    public static void savePolygonWithHolesAsSurf(WB_Polygon shellPoly, ArrayList<WB_Polygon> innnerPolys, int layer) {
        int shellPtsNum = shellPoly.getNumberOfPoints();
        IVec[] shellPts = new IVec[shellPtsNum];

        for (int i = 0; i < shellPtsNum; i++) {
            WB_Point pt = shellPoly.getPoint(i);
            shellPts[i] = new IVec(pt.xf(), pt.yf(), pt.zf());
        }
        int holeNum = innnerPolys.size();
        IVec[][] innerPts = new IVec[holeNum][];

        for (int i = 0; i < holeNum; i++) {

            WB_Coord[] ptt = innnerPolys.get(i).getPoints().toArray();
            ArrayList<WB_Point> pts = new ArrayList<>();
            for (int j = 0; j < ptt.length; ++j) {
                pts.add((WB_Point) ptt[j]);
            }
            IVec[] ptsIG = new IVec[pts.size()];
            for (int j = 0; j < pts.size(); j++) {
                WB_Point pt = pts.get(j);
                ptsIG[j] = new IVec(pt.xf(), pt.yf(), pt.zf());
            }
            innerPts[i] = ptsIG;
        }

        try {
            new ISurface(shellPts, innerPts).layer("" + layer);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setup() {
        size(1600, 900, P3D);
        tools = new Tools(this, len);
        // cam = new CameraController(this);
        polytrans = new PolyTrans(this);

        IG.init();
        for (int i = 0; i < 4; ++i) {
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
        tools.cp5.addSlider("BB").setRange(0, 1).setValue(0.60f).setPosition(20,
                60);

        tools.cp5.addSlider("CC").setRange(0, 1).setValue(0.20f).setPosition(20,
                80);
    }

    private double normalDistribution(double x) {
        return 1.0 / Math.sqrt(0.3 * Math.PI) * Math.exp(-x * x / AA);
    }

    private void draw3DRender() {
        ArrayList<WB_Polygon> ply = polytrans.polygons;
        double mn = 1e15, mx = 0;
        for (int i = 0; i < ply.size(); ++i) {
            Block3D b = new Block3D(ply.get(i).getPoints().toArray(), this);
            double total = 0;
            for (int j = 0; j < 4; ++j) {
                total += normalDistribution(distance(b.pts[4], polytrans.feets[j]));
            }
//			System.out.println("total = " + total);
            mn = Math.min(mn, total);
            mx = Math.max(mx, total);
//			double u = total;
//			b.display(this.g, u);
        }
//		System.out.println(mn + " " + mx);
        for (int i = 0; i < ply.size(); ++i) {
            Block3D b = new Block3D(ply.get(i).getPoints().toArray(), this);
            double total = 0;
            for (int j = 0; j < 4; ++j) {
                total += normalDistribution(distance(b.pts[4], polytrans.feets[j]));
            }
            double u = (total - mn) / (mx - mn) * BB + CC;
            b.display(this.g, u);
        }
    }

    private double distance(WB_Point a, WB_Point b) {
        return WB_Vector.getDistance2D(a, b) * (a.zd() - b.zd()) / A / A;
    }

    private void getPrint() {
    }

    private void drawPrint() {
        ArrayList<WB_Polygon> ply = polytrans.polygons;
        ArrayList<WB_Polygon> ply_fr = polytrans.polygons_fr;
        for (int i = 0; i < ply.size(); ++i) {
            Block3D b = new Block3D(ply.get(i).getPoints().toArray(), this);
            double total = 0;
            for (int j = 0; j < polytrans.feets.length; ++j) {
                total += normalDistribution(distance(b.pts[4], polytrans.feets[j]));
            }
            double u = (total - BB) / (CC - BB);
            Block3D b_fr = new Block3D(ply_fr.get(i).getPoints().toArray(), this);
            b_fr.display(this.g, u);
        }
    }

    public void keyPressed() {
        if (key == 'a') {
            clicked = !clicked;
        }

        if (key == 's') {
            ArrayList<WB_Polygon> ply = polytrans.polygons;
            double mn = 1e15, mx = 0;
            for (int i = 0; i < ply.size(); ++i) {
                Block3D b = new Block3D(ply.get(i).getPoints().toArray(), this);
                double total = 0;
                for (int j = 0; j < 4; ++j) {
                    total += normalDistribution(distance(b.pts[4], polytrans.feets[j]));
                }
//				System.out.println("total = " + total);
                mn = Math.min(mn, total);
                mx = Math.max(mx, total);
//				double u = total;
//				b.display(this.g, u);
            }
//			System.out.println(mn + " " + mx);
            for (int i = 0; i < ply.size(); ++i) {
                Block3D b = new Block3D(ply.get(i).getPoints().toArray(), this);
                double total = 0;
                for (int j = 0; j < 4; ++j) {
                    total += normalDistribution(distance(b.pts[4], polytrans.feets[j]));
                }
                double u = (total - mn) / (mx - mn) * BB + CC;
                b.calcPolygon(u);

                WB_Polygon outP = new WB_Polygon(b.pts[0], b.pts[1], b.pts[2]);
                ArrayList<WB_Polygon> innerP = new ArrayList<>();
                for (int j = 0; j < 2; ++j) {
                    if (b.ply[j] != null) innerP.add(b.ply[j]);
                }
                for (int j = 0; j < 6; ++j) {
                    if (b.detail[j] != null) innerP.add(b.detail[j]);
                }
                savePolygonWithHolesAsSurf(outP, innerP, 0);
                System.out.println("ok");

                outP = new WB_Polygon(b.pts[0], b.pts[2], b.pts[3]);
                innerP = new ArrayList<>();
                for (int j = 0; j < 2; ++j) {
                    if (b.ply[j + 2] != null) innerP.add(b.ply[j + 2]);
                }

                for (int j = 0; j < 6; ++j) {
                    if (b.detail[j + 6] != null) innerP.add(b.detail[j + 6]);
                }
                savePolygonWithHolesAsSurf(outP, innerP, 0);
                System.out.println("ok");

//				for(int j = 0; j < 4; ++ j) {
//					List<WB_Coord> ppt = b.ply[j].getPoints().toList();
//					IVec[] MVecList = new IVec[ppt.size()];
//					for(int k = 0; k < ppt.size(); ++ k) {
//						WB_Coord p = ppt.get(k); 
//						MVecList[k] = new IVec(p.xf(), p.yf(), p.zf());
//					}
//					ICurve cur = new ICurve(MVecList, true).layer("" + i);
//				}
            }
            IG.save("./model/face.3dm");
            System.out.println("saved");
        }
    }


}
