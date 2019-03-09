package server;

import java.io.*;
import java.net.*;
import java.util.function.Predicate;

public class ArcBlrothServer extends Thread {
	private static ArcBlrothServer THE_SERVER = new ArcBlrothServer();
	private ServerSocket socket;
	
	private ArcBlrothServer() {}
	
	public void run() {
		//try {
		//	System.setOut(new PrintStream(new FileOutputStream("log.txt", true)));
		//} catch (FileNotFoundException e2) {e2.printStackTrace();}
		
		try {
			this.getContextClassLoader().loadClass("server.ResourceLoader");
			this.getContextClassLoader().loadClass("server.FileLoader");
			this.getContextClassLoader().loadClass("server.UtilsLib");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		  try {
			  	this.setName("ArcBlrothServer[Loop]");
				System.out.println("ArcBlrothServer[run]: Your current IP is: " + InetAddress.getLocalHost().getHostAddress());
				//Keep a socket open to listen to all the UDP trafic that is destined for this port
			socket = new ServerSocket(8123);
				
				while(true) {
					if(!isInterrupted()) {
						System.out.println("ArcBlrothServer[Loop]: Ready to receive packets!");
						
						//Receive a packet
						Socket clientSocket = socket.accept();
						
						//Packet received
						System.out.println("ArcBlrothServer[Loop]: Accepted connection from: " + clientSocket.getInetAddress().getHostAddress());
						
						//Start a new thread to handle the request.
						new ClientHandler(clientSocket.getOutputStream(),
								new BufferedReader(new InputStreamReader(clientSocket.getInputStream())),
								clientSocket.getInetAddress()).start();
						
					} else {
						socket.close();
						System.exit(0);
					}
				}
		} catch (IOException ex) {
			System.err.println("ArcBlrothServer[Loop]: OH NOES WE HAVE AN IOEXCEPTION");
			ex.printStackTrace();
		} finally {
			System.out.println("ArcBlrothServer[Loop]: Exiting...");
			try {socket.close();} catch (IOException e) {}
			System.exit(0);
		}
	}
	
  	public static ArcBlrothServer getServer() {return THE_SERVER;}
  	
  	public ServerSocket getSocket() {return socket;}
  	
  	public static void main(String[] argz) {
  		ArcBlrothServer.getServer().run();
  	}
}
