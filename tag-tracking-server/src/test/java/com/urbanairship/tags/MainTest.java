package com.urbanairship.tags;

import static org.junit.Assert.assertEquals;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import org.json.*;
import org.junit.Before;
import org.junit.Test;

//These are some of the possible tests that I thought are important to run
public class MainTest {

	int port;
	
    @Before
    public void setUp() throws Exception {
    	port = Main.getPortNum();
		System.out.println("http://localhost:" + port + "/api/tags");
    	TagServer testTagserver = new TagServer(port);
    }
    
    //Helper function, sends post request 
    public String sendPostReq(String testRequest) throws Exception {
    	URL url = new URL("https://localhost:" + port + "/api/tags");
    	URLConnection con = url.openConnection();
    	HttpURLConnection http = (HttpURLConnection)con;
    	http.setRequestMethod("POST"); 
    	http.setDoOutput(true);
    	
    	byte[] out = testRequest.getBytes(StandardCharsets.UTF_8);
    	int length = out.length;

    	http.setFixedLengthStreamingMode(length);
    	http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    	http.connect();
    	try (OutputStream os = http.getOutputStream()) {
    	    os.write(out);
    	}
    	String testResponse = new String(http.getInputStream().readAllBytes());
    	return testResponse;
    }
    
    
    @Test
    public void testAddNewTag() throws Exception {
    	String testRequest = "{\"user\":\"user1\",\"add\":[\"tag1\"],\"remove\":[]}";
    	String testResponse = sendPostReq(testRequest);
    	JSONObject testUser = (JSONObject) new JSONTokener(testResponse).nextValue();
    	DataHandler.jsondata = null;
    	assertEquals(testUser.get("user").toString(), "user1");
    	assertEquals(new JSONArray (testUser.get("tags")).length(), 1);
    	assertEquals(new JSONArray (testUser.get("tags")).get(0).toString(), "tag1");
    }
    
    @Test
    public void testAddExistingTag() throws Exception {
    	String testRequest = "{\"user\":\"user1\",\"add\":[\"tag1\"],\"remove\":[]}";
    	sendPostReq(testRequest);
    	String testResponse = sendPostReq(testRequest);
    	JSONObject testUser = (JSONObject) new JSONTokener(testResponse).nextValue();
    	DataHandler.jsondata = null;
    	assertEquals(testUser.get("user").toString(), "user1");
    	assertEquals(new JSONArray (testUser.get("tags")).length(), 1);
    	assertEquals(new JSONArray (testUser.get("tags")).get(0).toString(), "tag1");
    }
    
    @Test
    public void testAddTagsDuplicatedInAdd() throws Exception {
    	String testRequest = "{\"user\":\"user1\",\"add\":[\"tag1\",\"tag2\",\"tag2\"],\"remove\":[]}";
    	String testResponse = sendPostReq(testRequest);
    	JSONObject testUser = (JSONObject) new JSONTokener(testResponse).nextValue();
    	DataHandler.jsondata = null;
    	assertEquals(testUser.get("user").toString(), "user1");
    	assertEquals(new JSONArray (testUser.get("tags")).length(), 2);
    	assertEquals(new JSONArray (testUser.get("tags")).get(0).toString(), "tag1");
    	assertEquals(new JSONArray (testUser.get("tags")).get(1).toString(), "tag2");
    }
    
    @Test
    public void testRemoveExistingTag() throws Exception {
    	String testRequest = "{\"user\":\"user1\",\"add\":[\"tag1\",\"tag2\"],\"remove\":[]}";
    	sendPostReq(testRequest);
    	testRequest = "{\"user\":\"user1\",\"add\":[],\"remove\":[\"tag1\"]}";
    	String testResponse = sendPostReq(testRequest);
    	JSONObject testUser = (JSONObject) new JSONTokener(testResponse).nextValue();
    	DataHandler.jsondata = null;
    	assertEquals(testUser.get("user").toString(), "user1");
    	assertEquals(new JSONArray (testUser.get("tags")).length(), 1);
    	assertEquals(new JSONArray (testUser.get("tags")).get(0).toString(), "tag2");
    }
    
    @Test
    public void testRemoveNonExistentTag() throws Exception {
    	String testRequest = "{\"user\":\"user1\",\"add\":[\"tag1\",\"tag2\"],\"remove\":[]}";
    	sendPostReq(testRequest);
    	testRequest = "{\"user\":\"user1\",\"add\":[],\"remove\":[\"tag3\"]}";
    	String testResponse = sendPostReq(testRequest);
    	JSONObject testUser = (JSONObject) new JSONTokener(testResponse).nextValue();
    	DataHandler.jsondata = null;
    	assertEquals(testUser.get("user").toString(), "user1");
    	assertEquals(new JSONArray (testUser.get("tags")).length(), 2);
    	assertEquals(new JSONArray (testUser.get("tags")).get(0).toString(), "tag1");
    	assertEquals(new JSONArray (testUser.get("tags")).get(1).toString(), "tag2");
    }
    
    @Test
    public void testRemoveTagsDuplictedInRemove() throws Exception {
    	String testRequest = "{\"user\":\"user1\",\"add\":[\"tag1\",\"tag2\"],\"remove\":[]}";
    	sendPostReq(testRequest);
    	testRequest = "{\"user\":\"user1\",\"add\":[],\"remove\":[\"tag2\",\"tag2\"]}";
    	String testResponse = sendPostReq(testRequest);
    	JSONObject testUser = (JSONObject) new JSONTokener(testResponse).nextValue();
    	DataHandler.jsondata = null;
    	assertEquals(testUser.get("user").toString(), "user1");
    	assertEquals(new JSONArray (testUser.get("tags")).length(), 1);
    	assertEquals(new JSONArray (testUser.get("tags")).get(0).toString(), "tag1");
    }
    
    @Test
    public void testAddAndRemoveSameTag() throws Exception {
    	String testRequest = "{\"user\":\"user1\",\"add\":[\"tag1\",\"tag2\"],\"remove\":[\"tag1\"]}";
    	String testResponse = sendPostReq(testRequest);
    	JSONObject testUser = (JSONObject) new JSONTokener(testResponse).nextValue();
    	DataHandler.jsondata = null;
    	assertEquals(testUser.get("user").toString(), "user1");
    	assertEquals(new JSONArray (testUser.get("tags")).length(), 1);
    	assertEquals(new JSONArray (testUser.get("tags")).get(0).toString(), "tag2");
    }
}
