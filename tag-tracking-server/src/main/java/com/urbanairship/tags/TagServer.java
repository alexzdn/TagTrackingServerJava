package com.urbanairship.tags;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.*;

//Server class
public class TagServer {
	HttpServer server;
	
	public TagServer(int port) throws IOException {
		server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/api/tags", new RequestHandler());
		server.setExecutor(null);
		server.start();
	}

	//Handles request and response for http://localhost:[port]/api/tags
	static class RequestHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange e) throws IOException {
			String request;
			String response = "Unexpected error occured";
			if (e.getRequestMethod().equalsIgnoreCase("POST")) {
				InputStream is = e.getRequestBody();
				request = new String(is.readAllBytes());
				try {
					response = DataHandler.updateJson(request);
				} 
				catch (Exception ex) {	
					System.out.println("Unknown exception occured: " + ex);
				}				
			}
			e.sendResponseHeaders(200, response.length());
            OutputStream os = e.getResponseBody();
            os.write(response.getBytes());
            os.close();
		}
		
	}	
}
