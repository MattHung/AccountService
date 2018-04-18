package com.accountservice.model.account;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="users")
public class SQLAccount extends AccountInfo {
	@SuppressWarnings("unused")
	private String user_password;
	
	public SQLAccount() {}
	
	public SQLAccount(int _dealer_id, String _user_name, 
					String user_password, String session_id){
		super(_dealer_id, _user_name);
		
		this.user_password = user_password;
		this.setSession_id(session_id);
	}

	public void setUser_password(String user_password) {
		this.user_password = user_password;
	}
}
