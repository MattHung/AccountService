package com.accountservice.helper;

public class ExceptionHelper {
	public static String getDetails(Exception e) {
		return e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
	}
}
