package com.reqres;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class HttpMethod {

	private static String host = "https://reqres.in";
	public static Response post(String body, String endpoint) {
		return given().contentType(ContentType.JSON).body(body).when().post(host + endpoint);
	}

	public static Response put(String body, String endpoint) {
		return given().contentType(ContentType.JSON).body(body).when().put(host + endpoint);
	}

	public static Response get(String endpoint) {
		return given().when().get(host + endpoint);
	}

	public static Response delete(String endpoint) {
		return given().when().delete(host + endpoint);
	}
}
