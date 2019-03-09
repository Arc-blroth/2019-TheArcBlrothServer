package server;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Date;

public class ClientHandler extends Thread {

	private OutputStream w;
	private BufferedReader r;
	private InetAddress dest;
	
	public ClientHandler(OutputStream w, BufferedReader r, InetAddress dest) {
		this.w = w;
		this.r = r;
		this.dest = dest;
	}
	
	public void run() {
		try {
			String inputLine = "", in = "";
			while ((inputLine = r.readLine()) != null) {
				in = in + inputLine + UtilsLib.crlf;
				if(inputLine.equals("")) {
					byte[] outputLine;
					try {
						outputLine = processInput(in);
					    log("File sent.");
					} catch(Exception e) {
						outputLine = getExceptionReturn(e);
					    err("Server 500 sent.");
					}
					w.write(outputLine);
				    in = "";
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {r.close(); w.close();} catch(IOException e1) {}
		}
	}
	
	public byte[] processInput(String in) throws IOException {
		String req = in.trim();
		log("Recieved request \"" + req.replaceAll(UtilsLib.crlf, "  ") + "\"");
		if(req.startsWith("GET")) {
			return get(req.substring(4));
		} else if(req.startsWith("HEAD")) {
			return head(req.substring(5));
		} else if(req.startsWith("OPTIONS")) {
			return options(req.substring(8));
		} else {
			return null;
		}
	}
	
	private Pair<String, byte[]> path(String req, boolean log) throws IOException {
		String path = req.split(" ")[0];
		if(path.contains("/?")) path = path.substring(0, path.lastIndexOf("/?") + 1); //effectively remove all arguments
		if(req.substring(0, 8).equals("/server/")) {
			if(log) err("Illegal attempt to access source code file: " + req.split(" ")[0]);
			return new Pair<String, byte[]>(req.split(" ")[0], new byte[] {});
		} else if(path.contains("__root__.html")) {
			if(log) err("Illegal attempt to access __root__." + req.split(" ")[0]);
			return new Pair<String, byte[]>(req.split(" ")[0], new byte[] {});
		} else {
			if(path.lastIndexOf("/") > path.lastIndexOf(".")) { //If path ! end with ".*"
				//log("path does not end in an file extension, appending .html.");
				path = path.concat("__root__.html");
			}
		}
		if(log) log("Fetching file: " + path);
		byte[] file = FileLoader.load(path, log);
		return new Pair<String, byte[]>(path, file);
	}
	
	private byte[] head(String req) throws IOException {
		Pair<String, byte[]> pair = path(req, true);
		String path = pair.getT1();
		byte[] file = pair.getT2();
		String ext = path.lastIndexOf(".") != -1 ? path.substring(path.lastIndexOf(".") + 1, path.length()) : "html";
		if(file == null) {
			err("404: File \"" + path + "\" does not exist.");
			return ("HTTP/1.1 404 IT NEVER EXISTED" + UtilsLib.crlf +
					"Date: " + new Date().toString() + UtilsLib.crlf +
					"Server: ArcBlrothServer/A.0.0.1" + UtilsLib.crlf).getBytes();
		} else {
			return  ("HTTP/1.1 200 READY TO ROLL" + UtilsLib.crlf +
					"Date: " + new Date().toString() + UtilsLib.crlf +
					"Server: ArcBlrothServer/A.0.0.1" + UtilsLib.crlf).getBytes();
		}
	}
	
	private byte[] get(String req) throws IOException {
		Pair<String, byte[]> pair = path(req, true);
		String path = pair.getT1();
		byte[] file = pair.getT2();
		String ext = path.lastIndexOf(".") != -1 ? path.substring(path.lastIndexOf(".") + 1, path.length()) : "html";
		if(file == null) {
			err("404: File \"" + path + "\" does not exist.");
			return get404();
			/*else return "HTTP/1.1 404 IT-NEVER-EXISTED" + UtilsLib.crlf +
						"Date: " + new Date().toString() + UtilsLib.crlf +
						"Server: ArcBlrothServer/A.0.0.1" + UtilsLib.crlf;*/
		} else {
			return  UtilsLib.concatArrays(("HTTP/1.1 200 READY TO ROLL" + UtilsLib.crlf +
					"Date: " + new Date().toString() + UtilsLib.crlf +
					"Content-Type: " + UtilsLib.toMIME(ext) + "; charset=UTF-8" + UtilsLib.crlf +
					"Content-Length: " + file.length + UtilsLib.crlf +
					"Server: ArcBlrothServer/A.0.0.1" + UtilsLib.crlf +
					UtilsLib.crlf).getBytes(),
					file);
		}
	}
	
	private byte[] get404() throws IOException {
		Pair<String, byte[]> pair = path("/404.html", false);
		String path = pair.getT1();
		byte[] file = pair.getT2();
		String ext = path.lastIndexOf(".") != -1 ? path.substring(path.lastIndexOf(".") + 1, path.length()) : "html";
		return  UtilsLib.concatArrays(("HTTP/1.1 404 IT NEVER EXISTED" + UtilsLib.crlf +
				"Date: " + new Date().toString() + UtilsLib.crlf +
				"Content-Type: " + UtilsLib.toMIME(ext) + "; charset=UTF-8" + UtilsLib.crlf +
				"Content-Length: " + file.length + UtilsLib.crlf +
				"Server: ArcBlrothServer/A.0.0.1" + UtilsLib.crlf +
				UtilsLib.crlf).getBytes(),
				file);
	}
	
	private byte[] getExceptionReturn(Exception e) {
		String exc = "<html><head></head><title>SERVER 500 ERROR</title>"
				+ "<body style=\"font-family: monospace; font-size: 16px;\">"
				+ "<span style=\"color: red;\">SERVER 500 ERROR<br/><br/>";
		exc = exc + e.toString() + "<br/>";
		for(StackTraceElement ste : e.getStackTrace()) {
			exc = exc + "<span style=\"padding-left: 16px;\"> "
					+ "at " + ste.toString() + "</span><br/>";
		}
		exc = exc + "</style></body></html>";
		return ("HTTP/1.1 500 " + e.getClass().getName().toUpperCase() + UtilsLib.crlf +
				"Date: " + new Date().toString() + UtilsLib.crlf +
				"Content-Type: " + "text/html" + "; charset=UTF-8" + UtilsLib.crlf +
				"Content-Length: " + exc.length() + UtilsLib.crlf +
				"Server: ArcBlrothServer/A.0.0.1" + UtilsLib.crlf +
				UtilsLib.crlf +
				exc + UtilsLib.crlf +
				UtilsLib.crlf).getBytes();
	}


	private byte[] options(String req) {
		return null;
	}
	
	private void log(String s) {
		System.out.println("ClientHandler[" + dest.getHostAddress() + "]: " + s);
	}
	private void err(String s) {
		System.err.println("ClientHandler[" + dest.getHostAddress() + "]: " + s);
	}
}
