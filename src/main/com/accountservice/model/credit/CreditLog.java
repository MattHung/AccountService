package com.accountservice.model.credit;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="_logs_credit")
public class CreditLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String action;
	private String details;
	private String source_ip;

	public CreditLog(String _action, String _details, String _source_ip) {
		action = _action;
		details = _details;
		source_ip = _source_ip;
	}
}
