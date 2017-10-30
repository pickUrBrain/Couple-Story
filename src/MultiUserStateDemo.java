import java.io.IOException;
import java.util.HashMap;
import processing.core.PApplet;

/**
 * @author eitan
 *
 */
public class MultiUserStateDemo extends PApplet {

	public static enum COLOR_STATE {
		RED, GREEN, BLUE
	};

	public COLOR_STATE colorState = COLOR_STATE.RED;

	String recordingFile = "floorTest.kinect";
	HashMap<Long, Person> tracks = new HashMap<Long, Person>();

	KinectBodyDataProvider kinectReader;
	PersonTracker tracker = new PersonTracker();

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
		createWindow(true, false, .25f);
	}

	public void setup() {

		/*
		 * use this code to run your PApplet from data recorded by UPDRecorder
		 */

		if (recordingFile != null)
			try {
				kinectReader = new KinectBodyDataProvider(recordingFile, 10);
			} catch (IOException e) {
				System.out.println("Unable to creat e kinect producer");
			}

		else {
			kinectReader = new KinectBodyDataProvider(8008);
		}

		kinectReader.start();

	}

	public void draw() {
		setScale(.5f);

		// noStroke();

		// hsb
		switch (colorState) {
		case RED:
			background(0, 255, 255);
			break;
		case BLUE:
			background(180, 255, 255);
			break;
		default:
			background(270, 255, 255);
		}

		KinectBodyData bodyData = kinectReader.getData();

		tracker.update(bodyData);

		for (Long id : tracker.getEnters()) {
			tracks.put(id, new Person(this, .1f));
		}
		for (Long id : tracker.getExits()) {
			tracks.remove(id);
		}

		if (!tracker.getEnters().isEmpty()) {
			System.out.println("enters" + colorState);

			switch (colorState) {
			case RED:
				colorState = COLOR_STATE.BLUE;
				break;
			case BLUE:
				colorState = COLOR_STATE.GREEN;
				break;
			default:
				colorState = COLOR_STATE.RED;
			}

		}

		for (Body b : tracker.getPeople().values()) {
			Person p = tracks.get(b.getId());
			p.update(b);
			p.draw();
		}

	}

	public static void main(String[] args) {

		PApplet.main(MultiUserStateDemo.class.getName());
	}

}
