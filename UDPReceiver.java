
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zhiling on 5/20/17. Modifyed by Eitan
 */
public class UDPReceiver implements Runnable, MsgProvider {

	ArrayBlockingQueue<Msg> receivedMsgs = new ArrayBlockingQueue<Msg>(2000);
	// if you are more than 2000 frames behind you have other problems

	private DatagramSocket mySocket;
	// private int myPort;

	private boolean isRunning = false;
	Thread thread;

	private int timeOut = 5000;

	// public static final int WIDTH = 1920;
	// public static final int HEIGHT = 1080;

	public UDPReceiver(int port) {

		try {
			mySocket = new DatagramSocket(port);
			mySocket.setSoTimeout(timeOut); // wait for 5 second for data
		} catch (SocketException e) {
			e.printStackTrace();
		}
		thread = new Thread(this);
	}

	public void start() {
		isRunning = true;
		thread.start();
	}

	@Override
	public void run() {
		DatagramPacket packet;
		while (isRunning) {
			/* buffer is filled with the data received */
			try {
				// InetAddress address = InetAddress.getByName("127.0.0.1");
				byte[] msg = new byte[6000];
				packet = new DatagramPacket(msg, msg.length);
				mySocket.receive(packet);

				receivedMsgs.offer(new Msg(packet.getData(), packet.getLength()), timeOut, TimeUnit.MILLISECONDS);

				// String s= new String(packet.getData(), 0 , packet.getLength());
				// System.out.println("|"+s+"|" + s.length());
			} catch (IOException | InterruptedException ioe) {
				if (isRunning) {
					ioe.printStackTrace();
				}
			}
		}
	}

	/*
	 * private void HandlePacket(DatagramPacket packet, byte[] msg){
	 * 
	 * System.out.println("hi"); }
	 */
	/*
	 * private void SaveAsColorIMG(byte[] imageBytes) throws IOException {
	 * 
	 * // BufferedImage img = new BufferedImage(WIDTH,HEIGHT
	 * BufferedImage.TYPE_INT_ARGB); System.out.println("hi"); InputStream in = new
	 * ByteArrayInputStream(imageBytes); BufferedImage img = read(in); if (img ==
	 * null) System.out.println("jho");
	 * 
	 * write(img, "jpg", new File("C:\\Users\\durian_milk\\Pictures\\hi.jpg")); }
	 */
	/**
	 * Stops the thread that is running and shut down
	 */
	public void stop() {
		// interrupt a blocked socket.
		isRunning = false;
		mySocket.close();
	}

	public BlockingQueue<Msg> getMsgQueue() {
		return receivedMsgs;
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	/*
	 * public static void main(String[] args) { UDPReceiver r= new
	 * UDPReceiver(8008); r.start(); } /* public static void main(String[] args) {
	 * // String ipAddress = null; JSONParser parser = new JSONParser(); int port =
	 * -1; if (args.length < 1) {
	 * 
	 * } // Check given file is Simple Text File using Java else if (args.length ==
	 * 1) try { Object obj = parser.parse(new FileReader(args[0])); JSONObject
	 * jsonObject = (JSONObject) obj;
	 * 
	 * port = Integer.parseInt((String) jsonObject.get("port")); // ipAddress =
	 * (String)jsonObject.get("ip address");
	 * 
	 * } catch (ParseException | IOException e) { e.printStackTrace(); }
	 * 
	 * else if (args.length == 2) { port = Integer.parseInt(args[0]); // ipAddress =
	 * args[1]; } else System.out.println("Invalid arguments");
	 * 
	 * Receiver receiver = new Receiver(port); new Thread(receiver).start();
	 * 
	 * }
	 */
}