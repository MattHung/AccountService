package com.accountservice.types;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public class FieldIndex {
	private String index_value;
	private List<Field> object_fields;
	
	public String getIndex_value() {return index_value;}
	public List<Field> getObject_fields() {return object_fields;}

	public void setIndex_value(String index_value) {this.index_value = index_value;}
	public void setObject_fields(List<Field> object_fields) {this.object_fields = object_fields;}	
	
	public FieldIndex() {
		index_value = "";
		object_fields = new LinkedList<>();
	}
}
