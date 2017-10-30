
import processing.core.PApplet;

public class ProjectorProportionsApp extends PApplet {

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
		setScale(.5f);

		strokeWeight(.1f);
		stroke(0, 0, 0);
		// center of the screen is 0,0
		ellipse(0f, 0f, 1, 1);

		stroke(255, 0, 0);
		// positive y is up
		// positive x is right
		line(-1, .5f, 0, .5f);

		stroke(0, 0, 255);
		line(0, -.5f, 1, -.5f);

	}

	public void draw() {

	}

	public static void main(String[] args) {
		PApplet.main(ProjectorProportionsApp.class.getName());
	}

}
