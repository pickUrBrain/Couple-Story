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

	Body body;
	PApplet app;

	int color;

	float centerX;
	float centerY;
	float rad; // radius for the shape
	PVector head;
	PVector spineBase;

	// float angle = 0;
	// float aVelocity = 0;
	// float aAcceleration = 0;

	// Two ArrayLists to store the vertices for two shapes
	// This example assumes that each shape will have the same
	// number of vertices, i.e. the size of each ArrayList will be the same
	ArrayList<PVector> circle = new ArrayList<PVector>();
	ArrayList<PVector> square = new ArrayList<PVector>();
	ArrayList<PVector> morph = new ArrayList<PVector>();

	public Shape(PApplet app) {
		this.app = app;

		// app.colorMode(PApplet.HSB);
		// color = app.color(app.random(255), 255, 255);
		initCircle();
		initSquare();
	}

	public void update(Body body) {
		this.body = body;
		head = body.getJoint(Body.HEAD);
		spineBase = body.getJoint(Body.SPINE_BASE);
		if (head != null && spineBase != null) {
			centerX = spineBase.x;
			centerY = spineBase.y;
			// the Euclidean distance between two points
			rad = Math.abs(head.dist(spineBase)) / 2f;
		}
	}

	public void draw(int state) {
		// draw relative to the center of this person
		app.translate(centerX, centerY);
		switch (state) {
		case -1:
			halfHeart(true); // left heart
			break;
		case 0:
			halfHeart(false); // right heart
			break;
		case 1:
			morph(circle);
			break;
		default:
			morph(square);
			break;
		}
	}

	// tested: is being called
	public void morph(ArrayList<PVector> vertices) {
		app.strokeWeight(4);
		app.noFill();
		app.stroke(255);
		app.beginShape();
		// Look at each vertex
		for (int i = 0; i < circle.size(); i++) {
			PVector v1;
			v1 = vertices.get(i);
			// Get the vertex we will draw
			PVector v2 = morph.get(i);
			// Lerp to the target
			v2.lerp(v1, (float) 0.1);
		}
		for (PVector v : morph) {
			app.vertex(v.x, v.y);
		}
		app.endShape(PApplet.CLOSE);
	}

	public void initCircle() {
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

	public void initSquare() {
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

	public void regPolygon(int npoints) {
		float angle = PConstants.TWO_PI / npoints;
		app.beginShape();
		for (float a = 0; a < PConstants.TWO_PI; a += angle) {
			float sx = centerX + PApplet.cos(a) * rad;
			float sy = centerY + PApplet.sin(a) * rad;
			app.vertex(sx, sy);
		}
		app.endShape(PConstants.CLOSE);
	}

	public void initPentagon() {

	}

	// https://www.khanacademy.org/computer-programming/beziervertexcx1-cy1-cx2-cy2-x-y-processingjs/5085481683386368
	public void halfHeart(boolean isLeft) {
		app.smooth();
		app.noStroke();

		app.fill(255, 0, 0);

		app.beginShape();
		if (isLeft) {
			app.vertex(50, 15);
			app.bezierVertex(50, -5, 100, 5, 50, 40);
		} else {
			app.vertex(50, 15);
			app.bezierVertex(50, -5, 0, 5, 50, 40);
		}
		app.endShape();
	}

}
