package telran.net;

import java.net.*;

public class TcpServer {
	Protocol protocol;
	int port;
	boolean running = true;
	public TcpServer(Protocol protocol, int port) {
		this.protocol = protocol;
		this.port = port;
	}
	public void run() {
		try(ServerSocket serverSocket = new ServerSocket(port)) {
			//TODO 
			//using ServerSocket has the method 
			//setSoTimeout
			System.out.println("Server is listening on port " + port);
			while(running) {
				Socket socket = serverSocket.accept();
				TcpClientServerSession session = new TcpClientServerSession(socket, protocol);
				session.start();

			}
			//TODO
			//handling timeout exception
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	public void shutdown() {
		running = false;
	}
	
}
