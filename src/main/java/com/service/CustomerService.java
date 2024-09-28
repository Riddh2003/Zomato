package com.service;

import org.springframework.stereotype.Service;

@Service
public class CustomerService {
	
	public String checkLoginorNot(Integer customerId,Integer loginCustomerId) {
		if(loginCustomerId == null) {
			return "You are not authorized to add any address.";
		}
		if(!customerId.equals(loginCustomerId)) {
			return "Please, Login first for this "+customerId+".";
		}
		return "success";
	}
}
