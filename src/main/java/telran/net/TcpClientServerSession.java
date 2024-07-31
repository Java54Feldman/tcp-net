package telran.net;

import java.net.*;
import java.io.*;

public class TcpClientServerSession extends Thread {
	Socket socket;
	Protocol protocol;
	private static final int IDLE_TIMEOUT = 60000;
	private static final int READLINE_TIMEOUT = 100;
	private boolean isShutdown = false;
	int idleTime = 0;
	// using the method setSoTimeout and some solution for getting session
	// to know about shutdown
	// you should stop the thread after shutdown command

	public TcpClientServerSession(Socket socket, Protocol protocol) {
		this.socket = socket;
		this.protocol = protocol;
		try {
			this.socket.setSoTimeout(READLINE_TIMEOUT);
		} catch (SocketException e) {

		}
	}

	public void run() {
		try (BufferedReader receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintStream sender = new PrintStream(socket.getOutputStream())) {
			String line = "";
			// exiting from the thread after shutdown
			// handling SocketTimeoutException for exiting from the thread on two
			// conditions:
			// 1. Shutdown has been performed
			// 2. Thread exists in IDLE state more than 1 min
			// exiting from the cycle should be followed by closing connection
			while (!isShutdown && line != null && !socket.isClosed()) {
				try {
					if ((line = receiver.readLine()) != null) {
						String responseStr = protocol.getResponseWithJSON(line);
						sender.println(responseStr);
						idleTime = 0;
					}
				} catch (SocketTimeoutException e) {
					idleTime += READLINE_TIMEOUT;
					if (idleTime > IDLE_TIMEOUT) {
						try {
							socket.close();
							System.out.println("Session timed out due to inactivity");
						} catch (IOException ex) {
							System.out.println("Error closing socket: " + ex.getMessage());
						}
					}
				}
			}

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void shutdown() {
		isShutdown = true;
	}

}