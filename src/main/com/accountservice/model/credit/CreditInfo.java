package com.accountservice.model.credit;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.accountservice.helper.JsonHelper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "credit")
public class CreditInfo{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private int dealer_id;
	private long user_id;
	private String currency;
	
	@JsonSerialize(using=JsonHelper.DoubleDesirializer.class)
	private double balance;
	
	public long getId() {return id;}
	public int getDealer_id() {return dealer_id;}
	public long getUser_id() {return user_id;}
	public String getCurrency() {return currency;}
	public double getBalance() {return balance;}
	
	public CreditInfo() {}
	
	public CreditInfo(int _dealer, long _user_id, String _currenry, double _balance) {
		dealer_id = _dealer;
		user_id = _user_id;
		currency = _currenry;
		balance = _balance;				
	}
	
	public CreditInfo(int _dealer, long _user_id, String _currenry) {
		this(_dealer, _user_id, _currenry, 0);			
	}
}
