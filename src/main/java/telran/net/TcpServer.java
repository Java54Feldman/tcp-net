package telran.net;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static telran.net.TcpConfigurationProperties.*;
public class TcpServer implements Runnable{
	Protocol protocol;
	int port;
	boolean running = true;
	ExecutorService sessionExecutor;
	public TcpServer(Protocol protocol, int port) {
		this.protocol = protocol;
		this.port = port;
		this.sessionExecutor = Executors.newFixedThreadPool(getNumberOfThreads());
	}

	public void shutdown() {
		running = false;
		sessionExecutor.shutdown();
		try {
			sessionExecutor.awaitTermination(SESSION_IDLE_TIMEOUT, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// no interrupts
		}
	}
	public void run() {
		try(ServerSocket serverSocket = new ServerSocket(port)){
			//using ServerSocket method setSoTimeout 
			System.out.println("Server is listening on port " + port);
			serverSocket.setSoTimeout(SOCKET_TIMEOUT);
			while(running) {
				try {
					Socket socket = serverSocket.accept();

					TcpClientServerSession session =
							new TcpClientServerSession(socket, protocol, this);
					session.start();
					sessionExecutor.execute(session);
					
				} catch (SocketTimeoutException e) {
					
				}
				
			}
			
				
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	private int getNumberOfThreads() {
		Runtime runtime = Runtime.getRuntime();
		return runtime.availableProcessors();
	}
	
}