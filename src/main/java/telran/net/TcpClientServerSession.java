package telran.net;

import java.net.*;
import java.io.*;

public class TcpClientServerSession implements Runnable {
	Socket socket;
	Protocol protocol;
	private static final int IDLE_TIMEOUT = 60000;
	private boolean isShutdown = false;
	// using the method setSoTimeout and some solution for getting session
	// to know about shutdown
	// you should stop the thread after shutdown command

	public TcpClientServerSession(Socket socket, Protocol protocol) {
		this.socket = socket;
		this.protocol = protocol;
		try {
			this.socket.setSoTimeout(IDLE_TIMEOUT);
		} catch (SocketException e) {

		}
	}

	@Override
	public void run() {
		try (BufferedReader receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintStream sender = new PrintStream(socket.getOutputStream())) {
			String line = null;
			// exiting from the thread after shutdown
			// handling SocketTimeoutException for exiting from the thread on two conditions:
			// 1. Shutdown has been performed
			// 2. Thread exists in IDLE state more than 1 min
			// exiting from the cycle should be followed by closing connection
			while (!isShutdown && (line = receiver.readLine()) != null) {
				String responseStr = protocol.getResponseWithJSON(line);
				sender.println(responseStr);
			}
		} catch (SocketTimeoutException e) {
			System.out.println("Session timed out due to inactivity");

		} catch (Exception e) {
			System.out.println(e);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Error closing socket: " + e.getMessage());
			}
		}

	}
	
	public void shutdown() {
        isShutdown = true; 
    }

}