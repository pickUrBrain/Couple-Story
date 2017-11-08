import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import processing.data.*;

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
	PVector head;
	PVector spineBase;
	// Two ArrayLists to store the vertices for two shapes
	// This example assumes that each shape will have the same
	// number of vertices, i.e. the size of each ArrayList will be the same
	ArrayList<PVector> circle = new ArrayList<PVector>();
	ArrayList<PVector> square = new ArrayList<PVector>();
	ArrayList<PVector> morph = new ArrayList<PVector>();

	public Shape(PApplet app) {
		this.app = app;

		app.colorMode(PApplet.HSB);
		color = app.color(app.random(255), 255, 255);
	}

	public void update(Body body) {
		this.body = body;
		head = body.getJoint(Body.HEAD);
		spineBase = body.getJoint(Body.SPINE_BASE);
		centerX = spineBase.x;
		centerY = spineBase.y;
		rad = Math.abs(head.dist(spineBase)) / 2f; // the Euclidean distance between two points
	}

	public void draw() {
		app.translate(centerX, centerY);
		if (isSquare)
			drawPolygon(4);
		// else
		// drawCircle();
	}

	public void circleVertices() {
		// Create a circle using vectors pointing from center
		for (int angle = 0; angle < 360; angle += 9) {
			// Note we are not starting from 0 in order to match the
			// path of a circle.
			PVector v = PVector.fromAngle(PApplet.radians(angle - 135));
			v.mult(100);
			circle.add(v);
			// Let's fill out morph ArrayList with blank PVectors while we are at it
			morph.add(new PVector());
		}
	}

	public void sqaureVertices() {
		// A square is a bunch of vertices along straight lines
		// Top of square
		for (int x = -50; x < 50; x += 10) {
			square.add(new PVector(x, -50));
		}
		// Right side
		for (int y = -50; y < 50; y += 10) {
			square.add(new PVector(50, y));
		}
		// Bottom
		for (int x = 50; x > -50; x -= 10) {
			square.add(new PVector(x, 50));
		}
		// Left side
		for (int y = 50; y > -50; y -= 10) {
			square.add(new PVector(-50, y));
		}
	}

	public void morph() {
		// Look at each vertex
		for (int i = 0; i < circle.size(); i++) {
			PVector v1;
			if (isSquare)
				v1 = square.get(i);
			else
				v1 = circle.get(i);
			// Get the vertex we will draw
			PVector v2 = morph.get(i);
			// Lerp to the target
			v2.lerp(v1, (float) 0.1);
		}
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
