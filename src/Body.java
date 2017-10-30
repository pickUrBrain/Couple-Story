import processing.core.PVector;
import processing.data.JSONObject;

/**
 * Body represents a single tacked skeleton Use this class to access skeleton
 * data
 * 
 * @author eitan
 *
 */
public class Body {

	// uses these variable as argument to getJoint to avoid typos!
	public static final String ANKLE_LEFT = "AnkleLeft";
	public static final String ANKLE_RIGHT = "AnkleRight";
	public static final String ELBOW_LEFT = "ElbowLeft";
	public static final String ELBOW_RIGHT = "ElbowRight";
	public static final String FOOT_LEFT = "FootLeft";
	public static final String FOOT_RIGHT = "FootRight";
	public static final String HAND_LEFT = "HandLeft";
	public static final String HAND_RIGHT = "HandRight";
	public static final String HAND_TIP_LEFT = "HandTipLeft";
	public static final String HAND_TIP_RIGHT = "HandTipRight";
	public static final String HEAD = "Head";
	public static final String HIP_LEFT = "HipLeft";
	public static final String HIP_RIGHT = "HipRight";
	public static final String KNEE_LEFT = "KneeLeft";
	public static final String KNEE_RIGHT = "KneeRight";
	public static final String NECK = "Neck";
	public static final String SHOULDER_LEFT = "ShoulderLeft";
	public static final String SHOULDER_RIGHT = "ShoulderRight";
	public static final String SPINE_BASE = "SpineBase";
	public static final String SPINE_MID = "SpineMid";
	public static final String SPINE_SHOULDER = "SpineShoulder";
	public static final String THUMB_LEFT = "ThumbLeft";
	public static final String THUMB_RIGHT = "ThumbRight";
	public static final String WRIST_LEFT = "WristLeft";
	public static final String WRIST_RIGHT = "WristRight";

	JSONObject bodyData;
	JSONObject joints;
	JSONObject jointOrientations;
	boolean isTracked;
	long id;

	public Body(JSONObject obj) {
		bodyData = obj;
		isTracked = bodyData.getBoolean("IsTracked");
		id = bodyData.getLong("TrackingId");
		if (isTracked) {
			joints = bodyData.getJSONObject("Joints");
			jointOrientations = bodyData.getJSONObject("JointOrientations");
		}
	}

	/**
	 * 
	 * @param bodyNum
	 * @return true if body number x is being tracked
	 */
	public boolean isTracked(int bodyNum) {
		return isTracked;
	}

	/**
	 * get track id. These should remain consistant between frames (where body numer
	 * in the list may not)
	 * 
	 * @return
	 */
	public long getId() {
		return id;
	}

	/**
	 * for imformation see https://msdn.microsoft.com/en-us/library/dn785526.aspx
	 * x,y is lean. z is 0
	 * 
	 * @return lean
	 */
	public PVector lean() {
		if (bodyData.getInt("LeanTrackingState") == 2) {
			JSONObject lean = bodyData.getJSONObject("Lean");
			return new PVector(lean.getFloat("X"), lean.getFloat("Y"), 0);
		} else {
			return null;
		}

	}

	/**
	 * Coordinates are relative to the camera.
	 * 
	 * @param jointName
	 * @return returns a vector representing the joint'ss position.
	 */
	public PVector getJoint(String jointName) {
		JSONObject joint = joints.getJSONObject(jointName);
		if (joint.getInt("TrackingState") == 2) {
			JSONObject pos = joint.getJSONObject("Position");
			return new PVector(pos.getFloat("X"), pos.getFloat("Y"), pos.getFloat("Z"));
		} else {
			return null;
		}
	}

	/**
	 * Rotation is relative to the camera.
	 * 
	 * @param jointName
	 * @return a quaternion representing the joint's absolute rotation and
	 *         orientation
	 */
	public Quat getJointOrientation(String jointName) {
		JSONObject orient = jointOrientations.getJSONObject(jointName);
		if (orient != null) {
			return new Quat(orient.getFloat("X"), orient.getFloat("Y"), orient.getFloat("Z"), orient.getFloat("W"));
		} else {
			return null;
		}
	}

}
