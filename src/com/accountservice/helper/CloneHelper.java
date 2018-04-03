package com.accountservice.helper;

import java.lang.reflect.Field;

public class CloneHelper {
	public static boolean cloneObject(Object source, Object destination) {
		try {
			for (Field field : source.getClass().getDeclaredFields()) {
	            field.setAccessible(true);
	            field.set(destination, field.get(source));
	        }
		}catch (Exception e) {
			return false;
		}
		
		return true;
	}
}
