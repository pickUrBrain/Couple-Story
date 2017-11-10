import java.io.IOException;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * @author eitan
 *
 */
public class KinectRenderDemo extends PApplet {

	KinectBodyDataProvider kinectReader;
	public static float PROJECTOR_RATIO = 1080f / 1920.0f;

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

	public void settings() {
		createWindow(true, true, .5f);
	}

	public void setup() {

		//use this code to run your PApplet from data recorded by UPDRecorder

		 try { kinectReader = new KinectBodyDataProvider("floorTest.kinect", 10); } catch
		  (IOException e) { System.out.println("Unable to creat e kinect producer"); 
		  }
		 

		//kinectReader = new KinectBodyDataProvider(8008);
		kinectReader.start();

	}

	public void draw() {
		setScale(.5f);

		noStroke();

		background(200, 200, 200);

		// leave trails instead of clearing background \
		// noStroke();
		// fill(0,0,0, 10);
		// rect(-1,-1, 2, 2); //draw transparent rect of the window

		// KinectBodyData bodyData = kinectReader.getMostRecentData();
		KinectBodyData bodyData = kinectReader.getData();
		Body person = bodyData.getPerson(0);
		if (person != null) {
			PVector head = person.getJoint(Body.HEAD);
			PVector spine = person.getJoint(Body.SPINE_SHOULDER);
			PVector spineBase = person.getJoint(Body.SPINE_BASE);
			PVector shoulderLeft = person.getJoint(Body.SHOULDER_LEFT);
			PVector shoulderRight = person.getJoint(Body.SHOULDER_RIGHT);
			PVector footLeft = person.getJoint(Body.FOOT_LEFT);
			PVector footRight = person.getJoint(Body.FOOT_RIGHT);
			PVector handLeft = person.getJoint(Body.HAND_LEFT);
			PVector handRight = person.getJoint(Body.HAND_RIGHT);
			

			fill(255, 255, 255);
			noStroke();
			drawIfValid(head);
			drawIfValid(spine);
			drawIfValid(spineBase);
			drawIfValid(shoulderLeft);
			drawIfValid(shoulderRight);
			drawIfValid(footLeft);
			drawIfValid(footRight);
			drawIfValid(handLeft);
			drawIfValid(handRight);

			if ((footRight != null) && (footLeft != null) &&
			// (shoulderLeft != null) &&
			// (shoulderRight != null)
					(handLeft != null) && (handRight != null)) {
				stroke(255, 0, 0, 100);
				noFill();
				strokeWeight(.05f); // because of scale weight needs to be much thinner
				curve(footLeft.x, footLeft.y, handLeft.x, handLeft.y, handRight.x, handRight.y, footRight.x,
						footRight.y);

			}

		}
	}

	/**
	 * Draws an ellipse in the x,y position of the vector (it ignores z). Will do
	 * nothing is vec is null. This is handy because get joint will return null if
	 * the joint isn't tracked.
	 * 
	 * @param vec
	 */
	public void drawIfValid(PVector vec) {
		if (vec != null) {
			ellipse(vec.x, vec.y, .1f, .1f);
		}

	}

	public static void main(String[] args) {
		PApplet.main(KinectRenderDemo.class.getName());
	}

}
