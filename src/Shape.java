import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;
import processing.core.PVector;

/**
 * @author Crystal, Zhiling
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
	PVector shoulderL;
	PVector shoulderR;

	boolean morphing = false;
	boolean isSquare = true;
	boolean isMarried = false;
	boolean isDivorced = false;

	long timeBorn = System.currentTimeMillis();

	// future featurs I want to implement
	// float angle = 0;
	// float aVelocity = 0;
	// float aAcceleration = 0;

	// keep tracking of exes
	HashSet<Long> marriedTo = new HashSet<Long>();

	// store the vertices for shapes
	ArrayList<PVector> crclSet = new ArrayList<PVector>();
	ArrayList<PVector> sqrSet = new ArrayList<PVector>();
	// store the vertices that been lerped to
	ArrayList<PVector> morphSet = new ArrayList<PVector>();

	// to store all the locations past by until meeting that married person
	ArrayList<PVector> exTraces = new ArrayList<PVector>();
	ArrayList<PVector> newTraces = new ArrayList<PVector>();

	public Shape(PApplet app) {
		this.app = app;
		// initialization for vertices set
		initCircle();
		initSquare();
	}

	public void update(Body body, boolean isMarried, long partnerId, boolean isDivorced) {
		this.body = body;
		head = body.getJoint(Body.HEAD);
		spineBase = body.getJoint(Body.SPINE_BASE);
		// shoulderR = body.getJoint(Body.SHOULDER_RIGHT);
		// shoulderL = body.getJoint(Body.SHOULDER_LEFT);
		if (head != null && spineBase != null) {
			centerX = spineBase.x;
			centerY = spineBase.y;
			// the Euclidean distance between two points
			// rad = Math.abs(head.dist(spineBase)) / 2f;
		}
		if (isDivorced) {
			exTraces = newTraces;
			newTraces = new ArrayList<PVector>(); // record new traces until meeting the next partner
			this.isDivorced = isDivorced;
		} else {
			if (isMarried) {
				// if never married to this person
				if (!marriedTo.contains(partnerId)) {
					marriedTo.add(partnerId);
					this.isMarried = isMarried;
					this.isDivorced = false;
				}
			}
		}
	}

	public void draw(int state, Color color) {
		app.fill(color.getRGB());
		switch (state) {
		case -1:
			halfHeart(true); // left heart
			break;
		case 0:
			halfHeart(false); // right heart
			break;
		case 1:
			morph(crclSet);
			break;
		case 2:
			morph(sqrSet);
			break;
		case 3:
			morph(crclSet);
			break;
		case 4:
			breakUp();
			break;
		}
	}

	public void breakUp() {
		// if not getting a break up or is married, should not display any traces
		if (!isDivorced || isMarried)
			return;
		app.strokeWeight((float) 0.02);
		for (int i = 1; i < exTraces.size(); i++) {
			float val = (float) (i / exTraces.size() * 204.0 + 51);
			app.stroke(val);
			app.line(exTraces.get(i - 1).x, exTraces.get(i - 1).y, exTraces.get(i).x, exTraces.get(i).y);
		}
	}

	// https://processing.org/examples/morph.html
	public void morph(ArrayList<PVector> vertices) {
		// Look at each vertex
		for (int i = 0; i < crclSet.size(); i++) {
			PVector v1;
			v1 = vertices.get(i);
			// Get the vertex we will draw
			PVector v2 = morphSet.get(i);
			// Lerp to the target
			v2.lerp(v1, (float) 0.05);
		}
		app.noStroke();
		app.pushMatrix();
		// shape that represents the new enter
		PShape s = app.createShape();
		// draw relative to the center of this person
		app.translate(centerX, centerY);
		s.beginShape();
		s.scale(.01f, .01f);
		// shape drawing
		for (PVector v : morphSet)
			s.vertex(v.x, v.y);
		s.endShape(PApplet.CLOSE);
		// create this shape in its parent pApplet
		app.shape(s);
		app.popMatrix();
	}

	public void halfHeart(boolean isLeft) {
		app.smooth();
		app.strokeWeight(1f);
		app.pushMatrix();
		// shape that represents the new enter
		PShape s = app.createShape();
		// draw relative to the center of this person
		if (isLeft) {
			centerX = shoulderL.x - 0.05f; // change the coordinate a bit to adjust the heart shape
			// centerY = centerY - 0.2f;
		} else {
			centerX = shoulderR.x + 0.05f; // change the coordinate a bit to adjust the heart shape
			// centerY = centerY - 0.2f;
		}
		app.translate(centerX, centerY);
		s.beginShape();
		s.scale(.01f, .01f);
		if (isLeft) {
			s.vertex(50, 15);
			s.bezierVertex(50, -5, 0, 5, 50, 40);
			s.rotate((float) (PApplet.PI));
		} else {
			s.vertex(50, 15);
			s.bezierVertex(50, -5, 100, 5, 50, 40);
			s.rotate((float) (PApplet.PI));
		}
		s.endShape(PApplet.CLOSE);
		// create this shape in its parent pApplet
		app.shape(s);
		app.popMatrix();
	}

	/**
	 * Status to be used in case booleans are not dected fine
	 */
	public void statusQuo() {
		if (isSquare) {
			app.noStroke();
			app.pushMatrix();
			// shape that represents the new enter
			PShape s = app.createShape();
			// draw relative to the center of this person
			app.translate(centerX, centerY);
			s.beginShape();
			s.scale(.01f, .01f);
			// shape drawing
			for (PVector v : sqrSet)
				s.vertex(v.x, v.y);
			s.endShape(PApplet.CLOSE);
			// create this shape in its parent pApplet
			app.shape(s);
			app.popMatrix();
		} else {
			app.noStroke();
			app.pushMatrix();
			// shape that represents the new enter
			PShape s = app.createShape();
			// draw relative to the center of this person
			app.translate(centerX, centerY);
			s.beginShape();
			s.scale(.01f, .01f);
			// shape drawing
			for (PVector v : crclSet)
				s.vertex(v.x, v.y);
			s.endShape(PApplet.CLOSE);
			// create this shape in its parent pApplet
			app.shape(s);
			app.popMatrix();
		}

	}

	/**
	 * Creates a circle using vectors pointing from center
	 */
	public void initCircle() {
		for (int angle = 0; angle < 360; angle += 9) {
			// Note we are not starting from 0 in order to match the
			// path of a circle.
			PVector v = PVector.fromAngle(PApplet.radians(angle - 135));
			v.mult(15);
			crclSet.add(v);
			// fill out morph ArrayList with blank PVectors
			morphSet.add(new PVector());
		}
	}

	/**
	 * Creates a square by using a bunch of vertices along straight lines
	 */
	public void initSquare() {
		// Top of square
		for (int x = -20; x < 20; x += 4)
			sqrSet.add(new PVector(x, -20));
		// Right side
		for (int y = -20; y < 20; y += 4)
			sqrSet.add(new PVector(20, y));
		// Bottom
		for (int x = 20; x > -20; x -= 4)
			sqrSet.add(new PVector(x, 20));
		// Left side
		for (int y = 20; y > -20; y -= 4)
			sqrSet.add(new PVector(-20, y));
	}

	/**
	 * For future use, if use different polygons to represent one's state in society
	 * 
	 * @param npoints
	 */
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

	public boolean getIsMarried() {
		return isMarried;
	}

	public void setIsMarried(boolean value) {
		isMarried = value;
	}

	public boolean getIsDivorced() {
		return isDivorced;
	}

	public void setIsDivorced(boolean value) {
		isDivorced = value;
	}

}
