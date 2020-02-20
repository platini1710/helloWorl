package com.javahungry.controller;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
public class test {

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		 JSONParser jsonParser = new JSONParser();
     
	JSONObject jsonObject = (JSONObject) jsonParser.parse("{\"user\": \"sebas\",\"pwd\": null,\"token\": \"Bearer eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiJzb2Z0dGVrSldUIiwic3ViIjoic2ViYXMiLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwiaWF0IjoxNTgyMTcyNjk2LCJleHAiOjE1ODIxNzMyOTZ9.GcqLJ85nRP4MzCrAfmQejLPfo0RpzOymNIe0JYTZxfS5FFsN8o9XOqLeAJm6arsd2_qfWG3jJsI1sfwiAtBJKw\"}");
	  String token = (String) jsonObject.get("token");
	  System.out.println(token);
	}

}
