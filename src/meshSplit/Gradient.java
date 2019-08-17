/**
 * 
 */
package meshSplit;

import java.util.ArrayList;
import java.util.List;

import wblut.geom.WB_Coord;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_Point;
import wblut.math.WB_Epsilon;

/**
 * @author amo Aug 15, 2019
 * 
 */
public class Gradient {

	/**
	 * 
	 */

	int numberOfPoints;
	double u;
	int functionType = 2;
	static int functionNum = 3;

	private List<WB_Point> pts;
	
	double minDist = 2437.8389106352893;
	double maxDist = 17059.758565740645;
	boolean isMin = false;

	public Gradient(WB_Coord... pt) {
		pts = new ArrayList<>();
		for (WB_Coord p : pt) {
			pts.add(new WB_Point(p));
		}
	}
	


	public List<WB_Point> getPoints() {
		return pts;
	}

	public void setFunctionType(int t) {
		if (t > functionNum) {
			System.err.println("Function type overflow.");
			functionType = 1;
		}
		else functionType = t;
	}

	public int getNumberOfPoints() {
		return numberOfPoints;
	}

	public double calcPosition(WB_Coord pos) {
		double total = 0;
		double min = 1;

//		double lmin = 1;
		for (WB_Point p : pts) {
			double dist = normalization(WB_GeometryOp.getDistance3D(pos, p), minDist, maxDist);
			if (WB_Epsilon.isZero(dist))
				continue;
			double tmp = function(dist);
			total += tmp;
			if(min > tmp) {
				min = tmp;
			}
		}
		if(isMin) {
			return min;
		}
		return total / pts.size();
	}


	public double function(double x) {
		double ret = 0;
		switch (functionType) {

			case 1 :
				ret = x;
				if(isMin) ret = normalization(ret, 0, 0.45);
				else ret = normalization(ret, 0.2, 0.71);

				break;
			case 2:
				ret =  1-Math.exp(-x*x/0.2) / Math.sqrt(2*Math.PI*0.1);
				if(!isMin) ret = normalization(ret, 0, 0.81);
				else ret = normalization(ret, -0.3, 0.50);
				break;
			case 3:
				ret = Math.sin(Math.PI*x/2);
				if(!isMin) ret = normalization(ret, 0.35, 0.85);
				else ret = normalization(ret, 0, 0.62);
				break;
			default :
				break;
		}
		return ret;
	}
	
	public double normalization(double x, double min, double max) {
		return (x-min)/(max-min);
	}

//	public double calcPosition(double x, double y, double z) {
//		return calcPosition(new WB_Point(x, y, z));
//	}

}
