package com.accountservice.response;

public class RequestResult<T> {
	public boolean succeed;
	public T data;
	public String note; 
	
	public RequestResult() {}	
	
	public RequestResult(boolean _succeed, T _data, String _note) {
		setData(_succeed, _data, _note);
	}
	
	public RequestResult<T> setData(boolean _succeed, T _data, String _note) {
		succeed = _succeed;
		data = _data;
		
		setNote(_note);
		return this;
	}
	
	public RequestResult<T> setNote(String _note) {
		note = _note;
		return this;
	}
}
