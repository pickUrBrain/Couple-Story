import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * @author Crystal, Zhiling
 *         {@link https://processing.org/examples/regularpolygon.html}
 *         {@link https://processing.org/examples/morph.html}
 */
public class Shape extends PApplet {
	// store the vertices for different shapes
	ArrayList<PVector> hexagon = new ArrayList<PVector>();
	ArrayList<PVector> pentagon = new ArrayList<PVector>();
	ArrayList<PVector> square = new ArrayList<PVector>();
	ArrayList<PVector> triangle = new ArrayList<PVector>();
	ArrayList<PVector> circle = new ArrayList<PVector>();

	void polygon(float x, float y, float radius, int npoints) {
		float angle = TWO_PI / npoints;
		beginShape();
		for (float a = 0; a < TWO_PI; a += angle) {
			float sx = x + cos(a) * radius;
			float sy = y + sin(a) * radius;
			vertex(sx, sy);
		}
		endShape(CLOSE);
	}
}
