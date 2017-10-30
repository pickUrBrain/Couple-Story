import processing.core.PApplet;
import processing.core.PVector;

public class Person {
	Body body;
	PApplet app;
	int color;
	float size;

	public Person(PApplet app, float size) {
		this.app = app;
		this.size = size;

		app.colorMode(PApplet.HSB);
		color = app.color(app.random(255), 255, 255);
	}

	public void update(Body body) {
		this.body = body;
	}

	public void draw() {
		app.fill(color);
		app.noStroke();
		drawIfValid(body.getJoint(Body.HEAD));
		drawIfValid(body.getJoint(Body.NECK));
		drawIfValid(body.getJoint(Body.SHOULDER_LEFT));
		drawIfValid(body.getJoint(Body.SHOULDER_RIGHT));
		drawIfValid(body.getJoint(Body.ELBOW_LEFT));
		drawIfValid(body.getJoint(Body.ELBOW_RIGHT));
		drawIfValid(body.getJoint(Body.HAND_LEFT));
		drawIfValid(body.getJoint(Body.HAND_RIGHT));
		drawIfValid(body.getJoint(Body.KNEE_LEFT));
		drawIfValid(body.getJoint(Body.KNEE_RIGHT));
		drawIfValid(body.getJoint(Body.FOOT_LEFT));
		drawIfValid(body.getJoint(Body.FOOT_RIGHT));
		drawIfValid(body.getJoint(Body.SPINE_BASE));
	}

	public void drawIfValid(PVector vec) {
		if (vec != null)
			app.ellipse(vec.x, vec.y, size, size);

	}

}
