package com.accountservice.model.machine;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="servers")
public class ServerInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private int role;
	private String name;
	private String ip_address;
	private String api_key;
	
	public int getId() {return id;}
	public int getRole() {return role;}
	public String getName() {return name;}
	public String getIp_address() {return ip_address;}
	public String getApi_key() {return api_key;}
	
	public void setApi_key(String api_key) {this.api_key = api_key;}
	
	public ServerInfo() {}
	
	public ServerInfo(int _role, String _name, String _ip_address) {
		role = _role;
		name = _name;
		ip_address = _ip_address;
	}
}
