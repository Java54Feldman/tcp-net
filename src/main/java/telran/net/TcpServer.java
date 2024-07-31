package telran.net;

import java.net.*;
import java.util.*;

public class TcpServer implements Runnable {
	Protocol protocol;
	int port;
	boolean running = true;
	private static final int ACCEPT_TIMEOUT = 1000; 
    private List<TcpClientServerSession> activeSessions = new ArrayList<>();
    
	public TcpServer(Protocol protocol, int port) {
		this.protocol = protocol;
		this.port = port;
	}
	public void run() { 
		try(ServerSocket serverSocket = new ServerSocket(port)) {
			//using ServerSocket has the method setSoTimeout
			serverSocket.setSoTimeout(ACCEPT_TIMEOUT);
			System.out.println("Server is listening on port " + port);
			while(running) {
				try {
					Socket socket = serverSocket.accept();
					TcpClientServerSession session = new TcpClientServerSession(socket, protocol);
					activeSessions.add(session);
					session.run();
				} catch (SocketTimeoutException e) {

				}
			}
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		} finally {
			shutdown();
        }
	}
	public void shutdown() {
		running = false;
        for (TcpClientServerSession session : activeSessions) {
            session.shutdown();
        }
        activeSessions.clear();
	}
	
}
