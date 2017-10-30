
import processing.core.PVector;

/**
 * Quat is a Quaternion. A Quaternion represent an orientation in space. They
 * consist of a vector x,y,z and rotation w about the vector. (The also have
 * other interesting properties).
 * 
 * This class is modified and extended version of this:
 * http://introcs.cs.princeton.edu/java/32class/Quaternion.java
 * 
 * @author eitan
 *
 */
public class Quat {
	private final double x, y, z, w;

	public Quat(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	/**
	 * Creates Quaternion based on Euler anges. warning: this method is untested!
	 * based on
	 * https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles
	 * 
	 * @param pitch
	 * @param roll
	 * @param yaw
	 */
	public Quat(double pitch, double roll, double yaw) {
		double cy = Math.cos(yaw * 0.5);
		double sy = Math.sin(yaw * 0.5);
		double cr = Math.cos(roll * 0.5);
		double sr = Math.sin(roll * 0.5);
		double cp = Math.cos(pitch * 0.5);
		double sp = Math.sin(pitch * 0.5);

		w = cy * cr * cp + sy * sr * sp;
		x = cy * sr * cp - sy * cr * sp;
		y = cy * cr * sp + sy * sr * cp;
		z = sy * cr * cp - cy * sr * sp;
	}

	public String toString() {
		return "[(" + x + ", " + y + ", " + z + "), w=" + w + "]";
	}

	public double norm() {
		return Math.sqrt(x * x + y * y + z * z + w * w);
	}

	public Quat conjugate() {
		return new Quat(x, -y, -z, -w);
	}

	public Quat plus(Quat b) {
		return new Quat(this.x + b.x, this.y + b.y, this.z + b.z, this.w + b.w);
	}

	public Quat mult(Quat b) {
		double x = this.x * b.x - this.y * b.y - this.z * b.z - this.w * b.w;
		double y = this.x * b.y + this.y * b.x + this.z * b.w - this.w * b.z;
		double z = this.x * b.z - this.y * b.w + this.z * b.x + this.w * b.y;
		double w = this.x * b.w + this.y * b.z - this.z * b.y + this.w * b.x;
		return new Quat(x, y, z, w);
	}

	// return a new Quaternion whose value is the inverse of this
	public Quat inverse() {
		double d = x * x + y * y + z * z + w * w;
		return new Quat(x / d, -y / d, -z / d, -w / d);
	}

	/**
	 * we use the definition a * q^-1 (as opposed to q^-1 a)
	 * 
	 * @param q
	 * @return this/q
	 */
	public Quat div(Quat q) {
		return this.mult(q.inverse());
	}

	/**
	 * warning: this method is untested! based on
	 * https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles
	 * 
	 * @return a vector with the quaternion's rotation represented as pitch yaw and
	 *         roll
	 */
	public PVector getEulerAngles() {
		// roll (x-axis rotation)
		double sinr = +2.0 * (w * x + y * z);
		double cosr = +1.0 - 2.0 * (x * x + y * y);
		double roll = Math.atan2(sinr, cosr);

		// pitch (y-axis rotation)
		double sinp = +2.0 * (w * y - z * x);
		double pitch;
		if (Math.abs(sinp) >= 1)
			pitch = Math.copySign(Math.PI / 2, sinp); // use 90 degrees if out of range
		else
			pitch = Math.asin(sinp);

		// yaw (z-axis rotation)
		double siny = +2.0 * (w * z + x * y);
		double cosy = +1.0 - 2.0 * (y * y + z * z);
		double yaw = Math.atan2(siny, cosy);

		return new PVector((float) pitch, (float) yaw, (float) roll);
	}

}