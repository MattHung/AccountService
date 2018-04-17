package com.accountservice.types;

//1:platform 2:game server
public enum ServerRole {
	Platfrom(1),
	GameServer(2);
	
	private int val;
			
	public int getVal() {return val;}

	ServerRole(final int _val){
		val = _val;
	}
}
