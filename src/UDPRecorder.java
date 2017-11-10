
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;

// records udp messages on port to file filename for approximiatly duration ms 
public class UDPRecorder {
	UDPReceiver receiver;
	long duration;
	String filename;

	public UDPRecorder(String filename, int port, long duration) {
		this.filename = filename;
		this.duration = duration;
		receiver = new UDPReceiver(port);
	}

	public void record() throws FileNotFoundException, IOException {
		System.out.println("Recording UDP data to " + filename);
		BlockingQueue<Msg> queue = receiver.getMsgQueue();
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(filename));
		receiver.start();
		long curTime = System.currentTimeMillis();
		long stopTime = curTime + duration;
		while (curTime < stopTime) {
			try {
				// this assumes we can save packets
				// faster than they are generated on the network
				Msg msg = queue.take();
				curTime = System.currentTimeMillis();
				msg.timeStamp = curTime;
				stream.writeObject(msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		receiver.stop();
		stream.close();
		System.out.println("Saved UDP data to " + filename);
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		new UDPRecorder("exitTest.kinect", 8008, 15000).record();
		;
	}

}
