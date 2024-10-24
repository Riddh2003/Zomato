package com.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.CustomerEntity;
import com.repository.CustomerRepository;

@Service
public class CustomerService {
	
	@Autowired
	CustomerRepository customerRepository;
	
	public String checkLoginorNot(Integer customerId,Integer loginCustomerId) {
		if(loginCustomerId == null) {
			return "You are not authorized to add any address.";
		}
		if(!customerId.equals(loginCustomerId)) {
			return "Please, Login first for this "+customerId+".";
		}
		return "success";
	}
	
	public String getEmailByToken(String token) {
		Optional<CustomerEntity> op = customerRepository.findByToken(token);
		if(op.isEmpty()) {
			return null;
		}else {
			CustomerEntity customerEntity = op.get();
			return customerEntity.getEmail();
		}
	}
}
