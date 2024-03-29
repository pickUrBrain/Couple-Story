import java.io.IOException;
import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PVector;
import sun.audio.*;
import java.io.*;

/**
 * @author Crystal, Zhiling
 */
public class CoupleApplication extends PApplet {

	int count = 0; // count of people

	PApplet app;
	KinectBodyDataProvider kinectReader;
	HashMap<Long, Shape> shapes = new HashMap<Long, Shape>();
	PersonTracker tracker = new PersonTracker();

	boolean isMorph = false;
	boolean isSquare = true;
	boolean playMusic = false;
	boolean married = false;
	boolean drewHeart = false;

	private Body body2;
	private Body bodyL;
	private Body bodyR;

	public static float PROJECTOR_RATIO = 1080f / 1920.0f;

	public void draw() {
		setScale(.5f);
		colorMode(PApplet.RGB);

		if (married)
			background(255, 227, 235); // baby pink;
		else
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
			body2 = null;
			isSquare = false;
		} else if (numPeople == 1) {
			isSquare = true;
			married = false;
		}

		//closest distance that will draw heart
		float closeDistance = Float.MAX_VALUE;

		//make sure there is no two hearts drawn in the same screen
		for (Body b : tracker.getPeople().values()) {
			Shape s = shapes.get(b.getId());
			if (s != null) {
				
				if (numPeople == 1) {
					s.updateLocation(b);
					s.draw(1);
					drewHeart = false;
				}
				
				//make sure if the heart was already drawn, just draw the circle
				else if (numPeople >= 3 && drewHeart){
					s.updateLocation(b);
					drawCircle(b);
				}

				else {
					// go through other partners
					for (Body p : tracker.getPeople().values()) {

						// if found possible partners,
						if (p != b) {
							if (closeDistance > isClose(b, p)) {
								closeDistance = isClose(b, p);
								body2 = p;
							}
						}
					}

					s.update(b, s.getIsMarried(), body2, s.getIsDivorced());
					if (closeDistance < 0.8) {
						drawHeart(bodyL, bodyR);
						drewHeart = true;
					} else {
						drawCircle(bodyL, bodyR);
						drewHeart = false;
					}
				}
				
				

			}
		}
	}

	/**
	 * detects which body is on the right and left to determine how to form a heart
	 * @param b1
	 * @param b2
	 * @return
	 */
	public float isClose(Body b1, Body b2) {

		float result = Float.MAX_VALUE;

		if (b1 != null && b2 != null) {

			PVector p1 = b1.getJoint(Body.SPINE_BASE);
			PVector p2 = b2.getJoint(Body.SPINE_BASE);

			if (p1 != null && p2 != null) {

				if (p2.x < p1.x) {
					bodyR = b1;
					bodyL = b2;
				} else {
					bodyL = b1;
					bodyR = b2;
				}

				if (bodyL.getJoint(Body.SHOULDER_RIGHT) != null && bodyR.getJoint(Body.SHOULDER_LEFT) != null) {
					result = Math.abs((bodyL.getJoint(Body.SHOULDER_RIGHT).x) - (bodyR.getJoint(Body.SHOULDER_LEFT).x));
				}
			}
		}
		return result;
	}

	/**
	 * Draws a left shape for left person and right shape for right person
	 * @param bodyL
	 * @param bodyR
	 */
	public void drawHeart(Body bodyL, Body bodyR) {
		Shape s1 = shapes.get(bodyL.getId());
		Shape s2 = shapes.get(bodyR.getId());

		// draw the heart
		s1.draw(0);
		s2.draw(-1);

		if (Math.abs((bodyL.getJoint(Body.SHOULDER_RIGHT).x) - (bodyR.getJoint(Body.SHOULDER_LEFT).x)) < 0.3
				&& !s1.isMarried && !s2.isMarried) {
			s1.setIsMarried(true);
			s2.setIsMarried(true);
			married = true;
			playMusic = false;
		}

	}

	/**
	 * draws a circle for two people and make sure to change the divorce state
	 * called when two people are not close to each other
	 * @param bodyL
	 * @param bodyR
	 */
	public void drawCircle(Body bodyL, Body bodyR) {

		Shape s1 = shapes.get(bodyL.getId());
		Shape s2 = shapes.get(bodyR.getId());

		// if the shape is married before, we're never getting back

		if (s1.isMarried && s2.isMarried) {
			// draw circle
			s1.draw(2);
			s2.draw(2);
			s1.setIsMarried(false);
			s2.setIsMarried(false);
			s1.setIsDivorced(true);
			s2.setIsDivorced(true);
		} else if (s1.isDivorced && s2.isDivorced) {
			s1.draw(3);
			s2.draw(3);
			// if (!playMusic)
			// playMusic();
		} else {
			// if two shapes are apart, just draw circle
			s1.draw(2);
			s2.draw(2);
		}
		married = false;
	}
	
	/**
	 * draws a circle for remaining person
	 * necessary to draw more than 2 people
	 * @param body
	 */
	public void drawCircle(Body body){
		
		Shape s1 = shapes.get(body.getId());
		s1.draw(2);
		s1.setIsMarried(false);
		s1.setIsDivorced(false);
	}
	

	public void playMusic() {
		music();
		playMusic = true;
	}

	/**
	 * plays music when the couple is apart and got divorced
	 */
	public static void music() {
		AudioPlayer MGP = AudioPlayer.player;
		AudioStream BGM;
		AudioData MD;
		AudioDataStream loop = null;
		try {
			BGM = new AudioStream(new FileInputStream("taylor6.wav"));
			MD = BGM.getData();
			loop = new AudioDataStream(MD);
		} catch (IOException e) {
		}
		MGP.start(loop);
	}

	public static void main(String[] args) {
		PApplet.main(CoupleApplication.class.getName());
	}

	public void setup() {

		/*
		 * use this code to run your PApplet from data recorded by UPDRecorder
		 */
		try {
			kinectReader = new KinectBodyDataProvider("exitTest.kinect", 1);
		} catch (IOException e) {
			System.out.println("Unable to create kinect producer");
		}

		// kinectReader = new KinectBodyDataProvider(8008);
		kinectReader.start();

	}

	public void settings() {
		createWindow(true, false, .25f);
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

}
