package com.reqres.parameters;

import org.testng.annotations.DataProvider;

/*Sample data only. This does not cover all possible scenarios.
Data is used to test if automation script is working.*/
public class DataProviderClass {

	@DataProvider(name = "create-users")
	public static Object[][] getCreateUsers() {
		return new Object[][] { { "Myka", "Developer" }, { "", "Developer" }, { "Myka", "" }, {"", ""} };
	}
	@DataProvider(name = "create-user")
	public static Object[][] getCreateUser() {
		return new Object[][] { { "Myka Tolentino", "Software Developer" } };
	}
	@DataProvider(name = "existing-user")
	public static Object[][] getExistingUser() {
		return new Object[][] { 
			{ 7, "michael.lawson@reqres.in", "Michael", "Lawson" }
		};
	}
	@DataProvider(name = "update-user")
	public static Object[][] getUpdateUser() {
		return new Object[][] { 
			{ 10, "Myka", "Developer 2" }
		};
	}
}
