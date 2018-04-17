package com.accountservice.model.game;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="games")
public class GameInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private int enabled;
	private int type;
	private int game_id;
	private String name;
	
	public int getEnabled() {return enabled;}
	public int getType() {return type;}
	public int getGame_id() {return game_id;}
	public String getName() {return name;}
	
	public void setEnabled(int enabled) {this.enabled = enabled;}
	
	public GameInfo() {}
	
	public GameInfo(int _type, int _game_id, String _name) {
		type = _type;
		game_id = _game_id;
		name = _name;
	}
}
