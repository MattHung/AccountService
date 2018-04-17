package com.accountservice.types;

public class UserSessionCheck {
	private int dealer_id;
	private String user_name;
	private String session_id;
	
	private boolean session_online;	
	private String details;
	
	public void setDealer_id(int dealer_id) {this.dealer_id = dealer_id;}
	public void setUser_name(String user_name) {this.user_name = user_name;}
	public void setSession_id(String session_id) {this.session_id = session_id;}
	public void setSession_online(boolean session_online) {this.session_online = session_online;}	
	public void setDetails(String details) {this.details = details;}
	public boolean isSession_online() {return session_online;}
	public String getDetails() {return details;}
	
	public int getDealer_id() {return dealer_id;}
	public String getUser_name() {return user_name;}
	public String getSession_id() {return session_id;}
}
