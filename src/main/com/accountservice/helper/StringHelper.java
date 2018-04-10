package com.accountservice.helper;

public class StringHelper {
	public static String clearInvalidChar(String input) {
		return input.replaceAll("[^A-Za-z0-9()\\[\\]]", "").replaceAll("\"", "");
	}
}
