import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PVector;

public class MorphingApplication extends PApplet {

	int count = 0; // count of people
	PApplet app;
	// app.colorMode(PApplet.HSB);
	// int color = color(random(255), 255, 255);
	Color cRed = new Color(255, 0, 0);
	Color cGreen = new Color(0, 255, 0);
	Color cBlue = new Color(0, 0, 255);

	HashMap<Long, Shape> shapes = new HashMap<Long, Shape>();

	KinectBodyDataProvider kinectReader;

	PersonTracker tracker = new PersonTracker();

	boolean isMorph = false;

	boolean isSquare = true;

	private Body body1;
	private Body body2;

	public static float PROJECTOR_RATIO = 1080f / 1920.0f;

	public void draw() {

		setScale(.5f);
		colorMode(PApplet.RGB);
		background(0);

		// KinectBodyData bodyData = kinectReader.getData();
		KinectBodyData bodyData = kinectReader.getMostRecentData();
		tracker.update(bodyData);
		isMorph = tracker.getMorph();

		for (Long id : tracker.getEnters()) {
			shapes.put(id, new Shape(this));
		}
		for (Long id : tracker.getExits()) {
			shapes.remove(id);
		}

		int numPeople = shapes.size(); // tested: detects correct count of
										// people
		if (numPeople >= 2) {
			body1 = null;
			body2 = null;
			isSquare = false;
		} else if (numPeople == 1)
			isSquare = true;

		for (Body b : tracker.getPeople().values()) {
			Shape s = shapes.get(b.getId());
			if (s != null) {
				s.update(b, isMorph, isSquare); // if there's any changes in
												// number of ppl, change morph
												// true
				// if there's two people, change isSquare to false

				if (numPeople == 1) {
					// s.draw(2, new Color(0, 0, 255));
					s.draw(2, new Color(51, 171, 249));
					body1 = b;

				} else if (numPeople >= 2) {
					if (body1 == null) {
						body1 = b;
					} else {
						body2 = b;
					}
					if (isClose(body1, body2)) {
						System.out.println(body1.getId() + "body 2: " + body2.getId());
						// s.draw(1, new Color(232, 64, 170));
					}
				}
			}

		}
	}

	public static void main(String[] args) {
		PApplet.main(MorphingApplication.class.getName());
	}

	public void setup() {

		/*
		 * use this code to run your PApplet from data recorded by UPDRecorder
		 */
		try {
			kinectReader = new KinectBodyDataProvider("exitTest.kinect", 2);
		} catch (IOException e) {
			System.out.println("Unable to creat e kinect producer");
		}

		// kinectReader = new KinectBodyDataProvider(8008);
		kinectReader.start();

	}

	public void settings() {
		createWindow(true, true, .25f);
	}

	public void createWindow(boolean useP2D, boolean isFullscreen, float windowsScale) {
		if (useP2D) {
			if (isFullscreen) {
				fullScreen(P2D);
			} else {
				size((int) (1920 * windowsScale), (int) (1080 * windowsScale), P2D);
			}
		} else {
			if (isFullscreen) {
				fullScreen();
			} else {
				size((int) (1920 * windowsScale), (int) (1080 * windowsScale));
			}
		}
	}

	// use lower numbers to zoom out (show more of the world)
	// zoom of 1 means that the window is 2 meters wide and appox 1 meter tall.
	public void setScale(float zoom) {
		scale(zoom * width / 2.0f, zoom * -width / 2.0f);
		translate(1f / zoom, -PROJECTOR_RATIO / zoom);
	}

	public boolean isClose(Body b1, Body b2) {

		Body bodyL;
		Body bodyR;
		float inter;

		if (b1 != null && b2 != null) {

			Shape s1 = shapes.get(b1.getId());
			Shape s2 = shapes.get(b2.getId());

			PVector p1 = b1.getJoint(Body.SPINE_BASE);
			PVector p2 = b2.getJoint(Body.SPINE_BASE);

			// determine which body is on the left or right

			if (p1 != null && p2 != null) {

				if (p2.x < p1.x) {
					System.out.println("CHECK");
					bodyR = b1;
					bodyL = b2;
				} else {
					bodyL = b1;
					bodyR = b2;
				}

				if (bodyL.getJoint(Body.SHOULDER_RIGHT) != null && bodyR.getJoint(Body.SHOULDER_LEFT) != null) {

					System.out.println(
							bodyL.getJoint(Body.SHOULDER_RIGHT).x + " body R: " + bodyR.getJoint(Body.SHOULDER_LEFT).x);

					// if two shapes are close enough
					if (Math.abs(
							(bodyL.getJoint(Body.SHOULDER_RIGHT).x) - (bodyR.getJoint(Body.SHOULDER_LEFT).x)) < 0.8) {

						s1 = shapes.get(bodyL.getId());
						s2 = shapes.get(bodyR.getId());

						if (!s1.isDivorced && !s2.isDivorced) {

							// draw the heart
							s1.draw(0, new Color(232, 64, 170));
							s2.draw(-1, new Color(232, 64, 170));

							if (Math.abs((bodyL.getJoint(Body.SHOULDER_RIGHT).x)
									- (bodyR.getJoint(Body.SHOULDER_LEFT).x)) < 0.3 && !s1.isMarried && !s2.isMarried) {
								s1.setIsMarried(true);
								s2.setIsMarried(true);
							}
						}

						return true;
					}

					// if the shape is married before, we're never getting back

					if (s1.isMarried && s2.isMarried) {
						// change the gradient color
						s1.draw(3, new Color(168, 111, 186));
						s2.draw(3, new Color(168, 111, 186));
						s1.setIsMarried(false);
						s2.setIsMarried(false);
						s1.setIsDivorced(true);
						s2.setIsDivorced(true);
					} else if (s1.isDivorced && s2.isDivorced) {
						s1.draw(4, new Color(168, 111, 186));
						s2.draw(4, new Color(168, 111, 186));
					} else {
						// if two shapes are apart, just draw circle
						s1.draw(1, new Color(232, 64, 170));
						s2.draw(1, new Color(232, 64, 170));
					}

				}

			}

		}

		return false;
	}

	public boolean isApart(PVector p1, PVector p2) {

		// gradient color

		return false;
	}

}
