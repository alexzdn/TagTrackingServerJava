package com.urbanairship.tags;

import org.json.*;

//Handles request data, produces response
public class DataHandler {
	
	static JSONArray jsondata = new JSONArray();
	
	/*
	 * Validates request
	 * if user already exist -> update based on request data -> save to response
	 * if user doesn't exist -> create new with request data -> save to response
	 * if wasn't able to validate request data or error occurred -> save error message to response
	 * Returns: response string
	 */
	public static String updateJson(String request) throws Exception {
		String response;
		JSONObject dataToWrite = (JSONObject) new JSONTokener(request).nextValue();
		try {
			if (validatedData(dataToWrite)) {
				JSONObject user = userExists(jsondata, dataToWrite);
				if (user != null) {
					addTags(user, dataToWrite);
					removeTags(user, dataToWrite);
					response = user.toString();
				}
				else {
					response = addNewUserEntry(dataToWrite).toString();
				}
			}
			else {
				response = new JSONObject().put("error", "Helpful message here.").toString();
			}
		}
		catch (Exception ex) {
			response = new JSONObject().put("error", "Helpful message here.").toString();
		}
		
		return response;
	}
	
	//Validates data for presence of keys and user_id value
	private static Boolean validatedData(JSONObject entry) throws JSONException {
		if (!entry.isNull("user") && entry.has("add") && entry.has("remove")) {
			return true;
		}		
		return false;
	}
	
	//creates new user (Json object), appends to users data (Json array)
	private static JSONObject addNewUserEntry(JSONObject dataToWrite) throws JSONException {
		JSONObject userEntry = new JSONObject();
		userEntry.put("user", dataToWrite.get("user"));
		userEntry.put("tags", new JSONArray());
		addTags(userEntry, dataToWrite);
		removeTags(userEntry, dataToWrite);
		jsondata.put(userEntry);
		return userEntry;
	}
	
	//adds request data tags to user
	private static void addTags(JSONObject userEntry, JSONObject dataToWrite) throws JSONException {
		Boolean tagExists = false;
		JSONArray tagsToAdd = dataToWrite.getJSONArray("add");		
		for (int i = 0; i < tagsToAdd.length(); ++i) {
			for (int j = 0; j < userEntry.getJSONArray("tags").length(); ++j) {
				if (tagsToAdd.get(i).equals(userEntry.getJSONArray("tags").get(j))) {
					tagExists = true;
					break;
				}
				tagExists = false;
			}
			if (!tagExists) {
				userEntry.accumulate("tags", tagsToAdd.get(i));
			}
		}			
	}
	
	//removes request data tags from user
	private static void removeTags(JSONObject userEntry, JSONObject dataToWrite) throws JSONException {
		JSONArray tagsToRemove = dataToWrite.getJSONArray("remove");		
		for (int i = 0; i < tagsToRemove.length(); ++i) {
			for (int j = 0; j < userEntry.getJSONArray("tags").length(); ++j) {
				if (tagsToRemove.get(i).equals(userEntry.getJSONArray("tags").get(j))) {
					userEntry.getJSONArray("tags").remove(j);
					break;
				}
			}
		}
	}
	
	/*
	 * checks if user exists in users data
	 * yes -> return user
	 * no -> return null
	 */
	private static JSONObject userExists(JSONArray jsondata, JSONObject dataToWrite) throws JSONException {
		if (jsondata == null) {
			return null;
		}
		else {
			for (int i = 0; i < jsondata.length(); ++i) {
				JSONObject user = jsondata.getJSONObject(i);
				String userid = (String) user.get("user");
		    	String useridUpd = (String) dataToWrite.get("user");
		    	if (userid.equals(useridUpd)) {
		    		return user;
		    	}
			}
		}		
		return null;
	}
	
}
