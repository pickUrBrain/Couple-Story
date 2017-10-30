import java.io.Serializable;

public class Msg implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	byte[] msg;
	long timeStamp = -1;

	public Msg(byte[] msg, int length, long timeStamp) {
		this(msg, length);
		this.timeStamp = timeStamp;
	}

	public Msg(byte[] msg, int length) {
		this.msg = new byte[length];
		System.arraycopy(msg, 0, this.msg, 0, length);

	}

}
