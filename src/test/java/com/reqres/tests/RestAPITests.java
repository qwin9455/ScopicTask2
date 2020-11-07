package com.reqres.tests;

import com.reqres.HttpMethod;
import com.reqres.parameters.DataProviderClass;
import com.reqres.requests.User;

import io.restassured.response.Response;

import org.junit.Assert;
import org.testng.annotations.Test;

public class RestAPITests {

	Response response;

	@Test(dataProvider = "create-users", dataProviderClass = DataProviderClass.class)
	public void givenUserWhenPostAndDataIsValidThenReturn201(String name, String job) {
		User cur = new User(name, job);
		response = HttpMethod.post(cur.toJsonString(), "/api/users");

		Assert.assertEquals(201, response.getStatusCode());
		Assert.assertEquals(name, response.path("name"));
		Assert.assertEquals(job, response.path("job"));
		Assert.assertNotNull(response.path("id"));
		Assert.assertNotNull(response.path("createdAt"));
	}
	
	@Test(dataProvider = "create-user", dataProviderClass = DataProviderClass.class)
	public void givenNewlyCreatedUserWhenGetThenReturn200(String name, String job) {
		// Create user.
		User cur = new User(name, job);
		response = HttpMethod.post(cur.toJsonString(), "/api/users");
		Assert.assertEquals(201, response.getStatusCode());

		String id = response.path("id");

		// Get the newly created user.
		response = HttpMethod.get("/api/users/" + id);
		
		//BUG. Unable to retrieve newly created user.
		Assert.assertEquals(200, response.getStatusCode());
		Assert.assertEquals(name, response.path("name"));
		Assert.assertEquals(job, response.path("job"));
	}
	@Test(dataProvider = "create-user", dataProviderClass = DataProviderClass.class)
	public void givenNewlyCreatedUserWhenUpdateAndGetThenReturn200(String name, String job) {
		// Create user.
		User cur = new User(name, job);
		response = HttpMethod.post(cur.toJsonString(), "/api/users");
		Assert.assertEquals(201, response.getStatusCode());

		String id = response.path("id");

		// Update user.
		cur.setJob("Designer");
		response = HttpMethod.put(cur.toJsonString(), "/api/users/" + id);
		Assert.assertEquals(200, response.getStatusCode());
		
		// Get the newly created user.
		response = HttpMethod.get("/api/users/" + id);
		
		//BUG. Unable to retrieve newly created user.
		Assert.assertEquals(200, response.getStatusCode());
		Assert.assertEquals(cur.getJob(), response.path("job"));
	}
	
	@Test(dataProvider = "create-user", dataProviderClass = DataProviderClass.class)
	public void givenNewlyCreatedUserWhenDeleteThenReturn201(String name, String job) {
		// Create user.
		User cur = new User(name, job);
		response = HttpMethod.post(cur.toJsonString(), "/api/users");
		Assert.assertEquals(201, response.getStatusCode());

		String id = response.path("id");

		// Delete the user.
		response = HttpMethod.delete("/api/users/" + id);
		Assert.assertEquals(201, response.getStatusCode());
	}

	@Test(dataProvider = "existing-user", dataProviderClass = DataProviderClass.class)
	public void givenUserExistWhenGetThenReturn200(int id, String email, String firstName, String lastName) {
		response = HttpMethod.get("api/users/" + id);

		Assert.assertEquals(200, response.getStatusCode());
		Assert.assertEquals(id, response.path("data.id"));
		Assert.assertEquals(email, response.path("data.email"));
		Assert.assertEquals(firstName, response.path("data.first_name"));
		Assert.assertEquals(lastName, response.path("data.last_name"));
	}

	@Test
	public void giverUserDoesNotExistWhenGetThenReturn404() {
		response = HttpMethod.get("/api/users/23");
		Assert.assertEquals(404, response.getStatusCode());
	}

	@Test(dataProvider = "update-user", dataProviderClass = DataProviderClass.class)
	public void givenIdExistWhenUpdateThenReturn200(int id, String name, String job) {
		// Get current user data before update
		response = HttpMethod.get("/api/users/" + id);

		String curName = response.path("name");
		String curJob = response.path("job");

		Assert.assertNotSame(curName, name);
		Assert.assertNotSame(curJob, job);
		
		// Verify that data has been updated.
		User cur = new User(name, job);
		response = HttpMethod.put(cur.toJsonString(), "/api/users/" + id);

		Assert.assertEquals(200, response.getStatusCode());
		Assert.assertEquals(name, response.path("name"));
		Assert.assertEquals(job, response.path("job"));
	}

	@Test(dataProvider = "update-user", dataProviderClass = DataProviderClass.class)
	public void givenIdDoesNotExistWhenUpdateAndGetThenReturn200(int id, String name, String job) {
		// Verify that ID does not exist
		response = HttpMethod.get("/api/users/1200");
		Assert.assertEquals(404, response.getStatusCode());

		// PUT request should create the user if does not exist
		User cur = new User(name, job);
		response = HttpMethod.put(cur.toJsonString(), "/api/users/1200");
		
		Assert.assertEquals(200, response.getStatusCode());
		Assert.assertEquals(name, response.path("name"));
		Assert.assertEquals(job, response.path("job"));

		// Verify that the user was created after PUT request.
		response = HttpMethod.get("/api/users/1200");

		// BUG. User was not created on PUT request. Return should be 200 but was 404.
		Assert.assertEquals(200, response.getStatusCode());
		Assert.assertEquals(name, response.path("name"));
		Assert.assertEquals(job, response.path("job"));

	}

	@Test(dataProvider = "existing-user", dataProviderClass = DataProviderClass.class)
	public void givenUserExistWhenDeleteAndGetThenReturn404(int id, String email, String firstName, String lastName) {
		// Verify that the user exist.
		response = HttpMethod.get("/api/users/" + id);
		Assert.assertEquals(200, response.getStatusCode());

		// Delete existing user.
		response = HttpMethod.delete("/api/users/" + id);
		Assert.assertEquals(204, response.getStatusCode());

		// Verify that the user no longer exist.
		response = HttpMethod.get("/api/users/" + id);

		// BUG. User was not deleted after DELETE request. Return should be 404 but was 200.
		Assert.assertEquals(404, response.getStatusCode());
	}

	@Test
	public void givenUserDoesNotExistWhenDeleteThenReturn204() {
		// Verify that user does not exist.
		response = HttpMethod.get("/api/users/23");
		Assert.assertEquals(404, response.getStatusCode());

		// Delete the non-existent user.
		response = HttpMethod.delete("/api/users/23");

		Assert.assertEquals(204, response.getStatusCode());
	}
	
	@Test(dataProvider = "update-user", dataProviderClass = DataProviderClass.class)
	public void givenIdExistWhenDeleteAndUpdateThenReturn200(int id, String name, String job) {
		// Verify that ID exist
		response = HttpMethod.get("/api/users/" + id);
		Assert.assertEquals(200, response.getStatusCode());
		
		// Delete existing user.
		response = HttpMethod.delete("/api/users/" + id);
		Assert.assertEquals(204, response.getStatusCode());
		
		// PUT request should create the user if does not exist
		User cur = new User(name, job);
		response = HttpMethod.put(cur.toJsonString(), "/api/users/" + id);
		
		Assert.assertEquals(200, response.getStatusCode());
		Assert.assertEquals(name, response.path("name"));
		Assert.assertEquals(job, response.path("job"));
	}
	@Test(dataProvider = "update-user", dataProviderClass = DataProviderClass.class)
	public void givenIdDoesNotExistWhenDeleteAndUpdateThenReturn200(int id, String name, String job) {
		// Verify that ID does not exist
		response = HttpMethod.get("/api/users/1200");
		Assert.assertEquals(404, response.getStatusCode());
		
		// Delete non-existent user.
		response = HttpMethod.delete("/api/users/1200");
		Assert.assertEquals(404, response.getStatusCode());
		
		// PUT request should create the user if does not exist
		User cur = new User(name, job);
		response = HttpMethod.put(cur.toJsonString(), "/api/users/1200");
		
		Assert.assertEquals(200, response.getStatusCode());
		Assert.assertEquals(name, response.path("name"));
		Assert.assertEquals(job, response.path("job"));
	}
}
