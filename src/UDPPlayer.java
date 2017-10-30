
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class UDPPlayer implements MsgProvider, Runnable {

	ArrayBlockingQueue<Msg> readMsgs = new ArrayBlockingQueue<Msg>(2000);
	private int timeOut = 5000;

	String filename;
	boolean isRunning = false;
	int loopCnt;

	public UDPPlayer(String filename) throws FileNotFoundException, IOException {
		this(filename, 0);

	}

	// number of times to "play" the file
	// use -1 to play until .stop is called
	public UDPPlayer(String filename, int loopCnt) throws FileNotFoundException, IOException {
		this.filename = filename;
		this.loopCnt = loopCnt;

	}

	@Override
	public void run() {
		long lastMsgTime = 0;
		long timeForNextMsg = 0;
		long lastTimeStamp = Long.MAX_VALUE; // max val will cause first msg to be immediate
		ObjectInputStream stream;
		while (isRunning && ((loopCnt > 0) || (loopCnt < 0))) {
			loopCnt--;
			try {
				stream = new ObjectInputStream(new FileInputStream(filename));
				boolean fileHasMoreObjects = true;
				while (isRunning && fileHasMoreObjects) {
					try {
						// fix timing!
						Msg msg = (Msg) stream.readObject();
						long curTime = System.currentTimeMillis();
						long diff = msg.timeStamp - lastTimeStamp;
						timeForNextMsg = lastMsgTime + diff;
						if (timeForNextMsg > curTime) {
							Thread.sleep(timeForNextMsg - curTime);
						}
						readMsgs.offer(msg, timeOut, TimeUnit.MILLISECONDS);
						lastTimeStamp = msg.timeStamp;
						lastMsgTime = System.currentTimeMillis();
					} catch (EOFException e) {
						fileHasMoreObjects = false;
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						if (isRunning) {
							e.printStackTrace();
						}
					}

				}
			} catch (FileNotFoundException e) {
				System.out.println(filename + "not found");
				stop();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void start() {
		isRunning = true;
		new Thread(this).start();
	}

	@Override
	public void stop() {
		isRunning = false;
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public BlockingQueue<Msg> getMsgQueue() {
		return readMsgs;
	}

}
