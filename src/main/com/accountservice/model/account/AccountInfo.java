package com.accountservice.model.account;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="users")
public class AccountInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private int dealer_id;
	private int user_id;
	private String user_name;
	private String session_id;
	private Timestamp session_id_updated_at;
	
	public AccountInfo() {}
	
	public AccountInfo(int _dealer_id, String _user_name){
		dealer_id = _dealer_id;
		user_name = _user_name;
	}
	
	public void setDealer_id(int dealer_id) {this.dealer_id = dealer_id;}
	public void setUser_id(int user_id) {this.user_id = user_id;}
	public void setUser_name(String user_name) {this.user_name = user_name;}
	public void setSession_id(String session_id) {this.session_id = session_id;}
	public void setSession_id_updated_at(Timestamp session_id_updated_at) {this.session_id_updated_at = session_id_updated_at;}

	public int getDealer_id() {return dealer_id;}	
	public int getUser_id() {return user_id;}	
	public String getUser_name() {return user_name;}
	public String getSession_id() {return session_id;}
	public Timestamp getSession_id_updated_at() {return session_id_updated_at;}	
}
