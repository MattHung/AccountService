package com.accountservice.types;

public class FieldSet {
	private String fieldName;
	private Object fieldValue;
	
	public String getFieldName() {return fieldName;}
	public Object getFieldValue() {return fieldValue;}
	
	public FieldSet(String _fieldName, Object _fieldValue) {
		fieldName = _fieldName;
		fieldValue = _fieldValue;
	}
}
