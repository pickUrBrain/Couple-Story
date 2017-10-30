

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class KinectBodyDataProvider {
	MsgProvider msgProvider;
	KinectBodyData mostRecentData = new KinectBodyData("");
	ArrayList<Msg> dataDrain = new ArrayList<Msg>();

	PersonTracker tracker = new PersonTracker();


	//read data from file recorded by UDPRecorded
	public KinectBodyDataProvider(String filename, int loopCnt) throws FileNotFoundException, IOException {
		msgProvider = new UDPPlayer(filename, loopCnt);
	}

	//receive data via UDP
	public KinectBodyDataProvider(int port) {
		msgProvider = new UDPReceiver(port);
	}

	public void start() {
		msgProvider.start();
	}
	public void stop() {
		msgProvider.stop();
	}
	public boolean isRunning() {
		return msgProvider.isRunning();
	}

	public KinectBodyData getData() {

		try {
			// get a message if there is one in the next 1/60th of a sec
			String jsonStr  = new String(msgProvider.getMsgQueue().poll((long)(1000.0/60.0), TimeUnit.MILLISECONDS).msg);
			mostRecentData = new KinectBodyData(jsonStr);
			
			tracker.update(mostRecentData);
			
		} catch (Exception e) {
			//exceptions are expected here
		}
		return mostRecentData;
	}



public KinectBodyData getMostRecentData() {
	msgProvider.getMsgQueue().drainTo(dataDrain);
	if(dataDrain.size() > 0) {
		String jsonStr = new String(dataDrain.get(dataDrain.size()-1).msg);
		mostRecentData = new KinectBodyData(jsonStr);
		tracker.update(mostRecentData);

	}
	return mostRecentData;
}


}
