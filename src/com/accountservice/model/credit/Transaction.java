package com.accountservice.model.credit;

import com.accountservice.helper.CloneHelper;
import com.accountservice.helper.JsonHelper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Transaction extends CreditInfo{
	
	@JsonSerialize(using=JsonHelper.DoubleDesirializer.class)
	public double beforeTrans;
	
	@JsonSerialize(using=JsonHelper.DoubleDesirializer.class)
	public double afterTrans;
	
	public Transaction(CreditInfo creditInfo) {
		CloneHelper.cloneObject(creditInfo, this);
	}
}
