import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * @author Crystal, Zhiling
 *         {@link https://processing.org/examples/regularpolygon.html}
 *         {@link https://processing.org/examples/morph.html}
 */
public class Shape {

	// This boolean variable will control if we are morphing to a circle or square
	boolean isSquare = false;

	Body body;
	PApplet app;
	int color;
	float centerX;
	float centerY;
	float rad; // radius for the shape
	float angle = 0;
	float aVelocity = 0;
	float aAcceleration = 0;
	ArrayList<PVector> vertices = new ArrayList<PVector>();

	public Shape(PApplet app) {
		this.app = app;

		app.colorMode(PApplet.HSB);
		color = app.color(app.random(255), 255, 255);
	}

	public void update(Body body) {
		this.body = body;
		PVector head = body.getJoint(Body.HEAD);
		PVector spineBase = body.getJoint(Body.SPINE_BASE);
		centerX = spineBase.x;
		centerY = spineBase.y;
		rad = Math.abs(head.dist(spineBase)); // the Euclidean distance between two points
	}

	public void draw() {
		if (isSquare)
			drawPolygon(4);
		else
			drawCircle();
	}

	public void drawCircle() {
	}

	public void drawPolygon(int npoints) {
		float angle = PConstants.TWO_PI / npoints;
		app.beginShape();
		for (float a = 0; a < PConstants.TWO_PI; a += angle) {
			float sx = centerX + PApplet.cos(a) * rad;
			float sy = centerY + PApplet.sin(a) * rad;
			app.vertex(sx, sy);
		}
		app.endShape(PConstants.CLOSE);
	}

}
