package com.accountservice.model.dealer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="dealers")
public class DealerInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private int dealer_id;
	private String dealer_name;
	public long getId() {
		return id;
	}
	public int getDealer_id() {
		return dealer_id;
	}
	public String getDealer_name() {
		return dealer_name;
	}
}
