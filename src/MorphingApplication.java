import java.io.IOException;
import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PVector;

public class MorphingApplication extends PApplet {

	int count = 0; // count of people

	HashMap<Long, Shape> shapes = new HashMap<Long, Shape>();

	KinectBodyDataProvider kinectReader;

	PersonTracker tracker = new PersonTracker();
	
	boolean isMorph = false;
	
	boolean isSquare = true;

	public static float PROJECTOR_RATIO = 1080f / 1920.0f;

	public void draw() {
		setScale(.5f);
		colorMode(PApplet.HSB);
		background(173);

		KinectBodyData bodyData = kinectReader.getData();
		tracker.update(bodyData);
		isMorph = tracker.getMorph();

		for (Long id : tracker.getEnters()) {
			shapes.put(id, new Shape(this));
		}
		for (Long id : tracker.getExits()) {
			shapes.remove(id);
		}

		int numPeople = shapes.size(); // tested: detects correct count of people
		if (numPeople == 2) isSquare = false;
		for (Body b : tracker.getPeople().values()) {
			Shape s = shapes.get(b.getId());
			if (s != null) {
				s.update(b, isMorph, isSquare); //if there's any changes in number of ppl, change morph true
				//if there's two people, change isSquare to false
				System.out.println("body: "+ b.getId()+ "morph: " + isMorph + "is square" + isSquare);
				
				if (numPeople == 1){
					System.out.println("num p: 1");
					s.draw(2);
				}
				else if (numPeople >= 2){
					System.out.println("num p: 2");
					s.draw(1);
				}
			}

		}
	}

	public void drawIfValid(PVector vec) {
		if (vec != null) {
			ellipse(vec.x, vec.y, .1f, .1f);
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
