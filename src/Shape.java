import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
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

	boolean isMarried = false;
	boolean isDivorced = false;

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

	String text = ""; // future implementations: text to display status
	public static final Color BABY_PINK = new Color(255, 182, 193);
	public static final Color HOT_PINK = new Color(255, 105, 180);
	public static final Color BLUE = new Color(168, 111, 186);
	public static final Color RED = new Color(232, 64, 170);
	public static final Color pureRed = new Color(255, 0, 0);
	public static final Color pureGreen = new Color(0, 255, 0);
	public static final Color pureBlue = new Color(0, 0, 255);
	public static final Color WHITE = new Color(255, 255, 255);

	public Shape(PApplet app) {
		this.app = app;
		// initialization for vertices set
		initCircle();
		initSquare();
	}

	// don't really see the point of isMarried, isDivorced parameter
	public void update(Body body, boolean isMarried, Body body2, boolean isDivorced) {
		this.body = body;
		head = body.getJoint(Body.HEAD);
		spineBase = body.getJoint(Body.SPINE_BASE);
		shoulderL = body.getJoint(Body.SHOULDER_LEFT);
		shoulderR = body.getJoint(Body.SHOULDER_RIGHT);

		if (head != null && spineBase != null) {
			centerX = spineBase.x;
			centerY = spineBase.y;
		}

		newTraces.add(new PVector(centerX, centerY));

		// Long partnerId = new Long (partner.getId());
		// if married stated
		if (isMarried) {
			text = "We fall in love!";
			newTraces.add(new PVector(centerX, centerY));
			if (newTraces.size() > 20) // have been together for a long time
				text = "I love you so much.";
			if (newTraces.size() > 80) // have been together for a long long
										// time
				text = "I love you so so much.";
			// just got married
			if (this.isMarried != isMarried) {
				this.isDivorced = false;
				// if never married to this person
				if (body2 != null) {
					long partnerId = body2.getId();
					if (!marriedTo.contains(partnerId)) {
						marriedTo.add(partnerId);
						newTraces = new ArrayList<PVector>();
					}
				} else // married to the same person again
					text = "Can't believe we fall in love again!"; // override
																	// the
																	// displaying
																	// text
			}
		} else if (isDivorced) {
			if (newTraces.size() <= 10) // Just break up
				text = "We should take a break.";
			else // return to the game
				text = "Single.";
			// just got divorced
			if (this.isDivorced != isDivorced) {
				this.isDivorced = isDivorced;
				this.isMarried = false;
				// remember all the traces being together
				exTraces = newTraces;
				newTraces = new ArrayList<PVector>(); // record new traces until
														// meeting the next
														// partner
			}
			newTraces.add(new PVector(centerX, centerY));
		}

	}

	/**
	 * Draws relative shape
	 * 
	 * @param state
	 */
	public void draw(int state) {
		if (isDivorced)
			breakUp(); // display traces of meeting ex

		switch (state) {
		case -1: // married, left person
			app.fill(HOT_PINK.getRGB());
			halfHeart(true); // left heart
			break;
		case 0: // married, right person
			app.fill(HOT_PINK.getRGB());
			halfHeart(false); // right heart
			break;
		case 1: // is alone
			app.fill(BLUE.getRGB());
			morph(sqrSet); //
			break;
		case 2: // more than one person
			app.fill(BABY_PINK.getRGB());
			morph(crclSet);
			break;
		case 3:
			app.fill(BABY_PINK.getRGB());
			morph(crclSet);
			break;
		}
	}

	/**
	 * Displays broken heart
	 * 
	 * @param isLeft
	 */
	public void halfHeart(boolean isLeft) {
		app.smooth();
		app.pushMatrix();
		// shape that represents the new enter
		PShape s = app.createShape();
		// draw relative to the center of this person
		if (isLeft) {
			if (shoulderL != null)
				centerX = shoulderL.x - 0.05f; // change the coordinate a bit to
												// adjust the heart shape
			// centerY = centerY - 0.2f;
		} else {
			if (shoulderR != null)
				centerX = shoulderR.x + 0.05f; // change the coordinate a bit to
												// adjust the heart shape
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
	 * Morphing bewteen circle and square https://processing.org/examples/morph.html
	 * 
	 * @param vertices
	 */
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
		app.fill(BABY_PINK.getRGB());
		s.scale(.01f, .01f);
		// shape drawing
		for (PVector v : morphSet)
			s.vertex(v.x, v.y);
		s.endShape(PApplet.CLOSE);
		// create this shape in its parent pApplet
		app.shape(s);
		app.popMatrix();
	}

	/**
	 * Displays the traces until meeting that ex
	 */
	public void breakUp() {
		app.strokeWeight((float) 0.02);
		for (int i = 1; i < exTraces.size(); i++) {
			// float val = (float) (i / exTraces.size() * 204.0 + 51);
			app.stroke(255, 255, 255);
			app.line(exTraces.get(i - 1).x, exTraces.get(i - 1).y, exTraces.get(i).x, exTraces.get(i).y);
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
		exTraces = newTraces;
		newTraces = new ArrayList<PVector>();
	}

	public void updateLocation(Body body) {
		this.body = body;
		head = body.getJoint(Body.HEAD);
		spineBase = body.getJoint(Body.SPINE_BASE);
		if (head != null && spineBase != null) {
			centerX = spineBase.x;
			centerY = spineBase.y;
		}
	}

}
