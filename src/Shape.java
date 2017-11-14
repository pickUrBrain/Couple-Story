import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;
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

	boolean morphing = false;
	boolean isSquare = true;

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

		// color = app.color(app.random(255), 255, 255);
		initCircle();
		initSquare();
	}

	public void update(Body body, boolean morphing, boolean isSquare) {
		this.body = body;
		this.morphing = morphing;
		head = body.getJoint(Body.HEAD);
		spineBase = body.getJoint(Body.SPINE_BASE);
		if (head != null && spineBase != null) {
			centerX = spineBase.x;
			centerY = spineBase.y;
			// the Euclidean distance between two points
			rad = Math.abs(head.dist(spineBase)) / 2f;
		}
	}

	public void draw() {
		if (morphing) {
			// Look at each vertex
			for (int i = 0; i < circle.size(); i++) {
				PVector v1 = null;
				// Are we lerping to the circle or square?
				if (!isSquare) {
					v1 = circle.get(i);
				} else {
					v1 = square.get(i);
				}
				// Get the vertex we will draw
				PVector v2 = morph.get(i);
				// Lerp to the target
				v2.lerp(v1, (float) 0.1);
			}
		}

		PShape s = app.createShape();
		s.beginShape();
		// draw relative to the center of this person
		s.translate(centerX, centerY);
		s.scale(.1f, .1f);
		for (PVector v : morph) {
			s.vertex(v.x, v.y);
		}
		s.endShape(PApplet.CLOSE);
		app.shape(s);

	}

	public void draw(int state) {
		app.fill(255);
		app.noStroke();

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
		case 2:
			morph(square);
			break;
		default:

		}
	}

	// tested: is being called
	public void morph(ArrayList<PVector> vertices) {
		// Look at each vertex
		for (int i = 0; i < circle.size(); i++) {
			PVector v1;
			v1 = vertices.get(i);
			// Get the vertex we will draw
			PVector v2 = morph.get(i);
			// Lerp to the target
			v2.lerp(v1, (float) 0.1);
		}
		PShape s = app.createShape();
		app.translate(centerX, centerY);
		s.beginShape();
		s.translate(centerX, centerY);
		// draw relative to the center of this person
		s.scale(.01f, .01f);
		// app.translate(centerX, centerY);
		for (PVector v : morph) {
			s.vertex(v.x, v.y);
		}
		s.endShape(PApplet.CLOSE);
		app.shape(s);
	}

	// https://www.khanacademy.org/computer-programming/beziervertexcx1-cy1-cx2-cy2-x-y-processingjs/5085481683386368
	public void halfHeart(boolean isLeft) {
		app.smooth();
		// app.noStroke();
		// app.fill(255);
		// app.strokeWeight(.1f);

		// tested: is drawing, but vertex might be too small
		app.beginShape();
		// draw relative to the center of this person
		app.translate(centerX, centerY);
		app.scale(.1f, .1f);
		if (isLeft) {
			System.out.println("c:" + centerX + " ; " + centerY);
			app.vertex(50, 15);
			app.bezierVertex(50, -5, 100, 5, 50, 40);
		} else {
			app.vertex(50, 15);
			app.bezierVertex(50, -5, 0, 5, 50, 40);
		}
		app.endShape(PApplet.CLOSE);
	}

	public void initCircle() {
		// Create a circle using vectors pointing from center
		for (int angle = 0; angle < 360; angle += 9) {
			// Note we are not starting from 0 in order to match the
			// path of a circle.
			PVector v = PVector.fromAngle(PApplet.radians(angle - 135));
			v.mult(15);
			circle.add(v);
			// Let's fill out morph ArrayList with blank PVectors while we are at it
			morph.add(new PVector());
		}
	}

	// MAKE THE SQUARE BIGGER THAN THE CIRCLE!!!!!
	public void initSquare() {
		// A square is a bunch of vertices along straight lines
		// Top of square
		for (int x = -5; x < 5; x += 1) {
			square.add(new PVector(x, -5));
		}
		// Right side
		for (int y = -5; y < 5; y += 1) {
			square.add(new PVector(5, y));
		}
		// Bottom
		for (int x = 5; x > -5; x -= 1) {
			square.add(new PVector(x, 5));
		}
		// Left side
		for (int y = 5; y > -5; y -= 1) {
			square.add(new PVector(-5, y));
		}
	}

	// public void draw() {
	// app.fill(255);
	// app.noStroke();
	// drawIfValid(new PVector(centerX, centerY));
	// // halfHeart(true);
	// morph(circle);
	//
	// }
	//
	// public void drawIfValid(PVector vec) {
	// if (vec != null)
	// app.ellipse(vec.x, vec.y, .1f, .1f);
	// }

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

}
