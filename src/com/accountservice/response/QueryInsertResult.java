package com.accountservice.response;

public class QueryInsertResult<T> extends ApiResult<T> {
	public QueryInsertResult<T> setData(boolean _succeed, T _data, String _note) {
		return (QueryInsertResult<T>) super.setData(_succeed, _data, _note);
	}
	
	public QueryInsertResult<T> setNote(String _note) {
		return (QueryInsertResult<T>) super.setNote(_note);
	}
}
