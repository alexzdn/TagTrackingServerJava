package com.urbanairship.tags;

import java.util.Scanner;
import java.io.IOException;

public class Main {
	
	static class Message extends Thread {
	      public void run() {
	    	  System.out.println("Server shutdown");
	      }
	   }
	
    public static void main( String[] args ) throws IOException {
    	Runtime.getRuntime().addShutdownHook(new Message());
    	int port = getPortNum();
		System.out.println("http://localhost:" + port + "/api/tags");
		TagServer tagserver = new TagServer(port);
    }
    
    //Get port number
    static int getPortNum() {
    	Scanner reader = new Scanner(System.in);
    	System.out.println("Enter a port number: ");
    	int port = reader.nextInt();
    	reader.close();
    	return port;
    }
}
