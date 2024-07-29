package telran.net;

import java.net.*;
import java.io.*;

public class TcpClientServerSession extends Thread {
	Socket socket;
	Protocol protocol;

	public TcpClientServerSession (Socket socket, Protocol protocol) {
		this.socket = socket;
		//TODO
		//using the method setSoTimeout and some solution for getting session
		//to know about shutdown
		//you should stop he thread after shutdown command
		
		this.protocol = protocol;
	}

	public void run() {
		try (BufferedReader receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintStream sender = new PrintStream(socket.getOutputStream())) {
			String line = null;
			//FIXME
			//figure out solution 
			//for exiting from the thread after shutdown
			while ((line = receiver.readLine()) != null) {
				String responseStr = protocol.getResponseWithJSON(line);
				sender.println(responseStr);
			}
			//TODO 
			//handling SocketTimeoutException for exiting
			//from the thread on two conditions:
			//1. Shutdown has been performed
			//2. Thread exists in IDLE state more than 1 min
			//exiting from the cycle should be followed by closing connection	
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}