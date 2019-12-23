/**
 *
 */
package meshSplit;

import processing.core.PApplet;
import processing.core.PGraphics;
import util.Tools;
import wblut.geom.*;
import wblut.hemesh.*;
import wblut.processing.WB_Render3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author amo Aug 13, 2019
 *
 */
public class MeshBlock {

    private static final WB_GeometryFactory gf = new WB_GeometryFactory();
    static double triLength = 100;
    static double faceSplit = 360;
    /**
     *
     */

    ArrayList<WB_Point> pts;
    WB_Polygon ply;
    HE_Mesh mesh;
    HE_Mesh[] meshes;
    List<HE_Mesh> resultMeshes;
    List<WB_Segment> segs;
    WB_Render3D render;
    int numberOfPoints;
    int numberOfMeshes = 1;
    double u = 0.5;

    WB_Point[] midP;

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

//		System.out.println(resultMeshes.size());
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

    static WB_Polygon getWb_polygon(WB_Polygon ply, WB_GeometryFactory gf, WB_Polygon[] holes) {
        if (holes == null || holes.length == 0)
            return ply;

        ArrayList<WB_Coord> points = new ArrayList<>(ply.getPoints().toList());
        ArrayList<WB_Coord>[] innerpts = new ArrayList[holes.length];
        boolean flag = false;
        for (int i = 0; i < holes.length; ++i) {
            WB_Polygon hole = holes[i];
            if (holes[i] == null)
                continue;
            flag = true;
            ArrayList<WB_Coord> pts = new ArrayList<>(
                    Arrays.asList(hole.getPoints().toArray()));
            // Collections.reverse(pts);
            innerpts[i] = pts;
        }
        if (flag)
            return gf.createPolygonWithHoles(points, innerpts);
        else
            return ply;
    }

    public void display(PGraphics app) {
        for (int i = 0; i < numberOfMeshes; ++i) {
            app.fill(getColor());
            app.noStroke();
            render.drawFaces(meshes[i]);
            app.noFill();
            app.stroke(0);
            render.drawEdges(meshes[i]);
        }

        app.stroke(0, 0, 255);
        // render.drawSegment(segs);

        app.fill(255, 0, 0);
        render.drawPoint(midP, 20);
    }

    public void dispalyWithHole(PGraphics app) {
        app.fill(getColor());
        app.noStroke();
        render.drawFaces(resultMeshes);

        app.noFill();
        app.stroke(0);
        render.drawEdges(resultMeshes);
    }

    public void setSegments() {
        meshes = new HE_Mesh[numberOfMeshes];
        segs = new ArrayList<WB_Segment>();
        midP = new WB_Point[4];

        WB_Point O = ply.getCenter();
        WB_Point[] innerP = new WB_Point[4];

        for (int i = 0; i < 4; ++i) {
            midP[i] = pts.get(i).add(pts.get((i + 1) % 4)).div(2);
            segs.add(new WB_Segment(midP[i], O));

            innerP[i] = segs.get(i).getPointOnCurve(0.5);
        }


        List<WB_Polygon> meshPly = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            int j = (i + 1) % 4;
            meshPly.add(new WB_Polygon(midP[i], pts.get(j), innerP[i]));
            meshPly.add(new WB_Polygon(pts.get(j), innerP[j], innerP[i]));
            meshPly.add(new WB_Polygon(pts.get(j), midP[j], innerP[j]));
            meshPly.add(new WB_Polygon(innerP[i], innerP[j], O));
        }


        HEC_FromPolygons creator = new HEC_FromPolygons(meshPly);
        meshes[0] = new HE_Mesh(creator);

    }

    public int getColor() {
        int ret = (int) (u * 255);
        return ret;
    }

    public void MeshSplit() {
        HET_MeshOp.splitFacesTri(mesh);
        HET_MeshOp.splitFacesTri(mesh);
    }

    public void setHoles() {
        // MeshSplit();
        // mesh.triangulate();

        resultMeshes = new ArrayList<>();
        for (int i = 0; i < numberOfMeshes; ++i) {
            List<HE_Face> faces = meshes[i].getFaces();
            for (HE_Face f : faces) {
                WB_Transform3D T = new WB_Transform3D(f.getFaceCenter(),
                        f.getFaceNormal(), WB_Point.ORIGIN(), WB_Vector.Z());
                WB_Transform3D rT = new WB_Transform3D(WB_Point.ORIGIN(),
                        WB_Vector.Z(), f.getFaceCenter(), f.getFaceNormal());

                WB_Polygon ply = new WB_Polygon(f.getPolygon().apply(T).getPoints());
                WB_Polygon hole;
                if (i < 4) {
                    hole = Tools.JTSOffset(ply, (1 - u) * 5 + 38,
                            10 * (1 - u) + 20);
                } else {
                    hole = Tools.JTSOffset(ply, (1 - u) * 45 + 5,
                            20 * (1 - u) + 10);
                }

                ply = createPolygonWithPolygonHoles(ply, hole);
                resultMeshes
                        .add((new HE_Mesh(new HEC_Polygon(ply, 0))).apply(rT));
            }
        }
    }

    public WB_Polygon createPolygonWithPolygonHoles(WB_Polygon ply,
                                                    WB_Polygon... holes) {
        return getWb_polygon(ply, gf, holes);
    }

    public WB_Point getCenter() {
        return ply.getCenter();

    }

}
