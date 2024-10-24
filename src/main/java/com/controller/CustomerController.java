package com.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bean.CustomerBean;
import com.entity.CustomerEntity;
import com.repository.CustomerRepository;
import com.service.CustomerService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/private/customer")
public class CustomerController{
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	BCryptPasswordEncoder encoder;
	
	//read all customer
	@GetMapping
	public ResponseEntity<List<CustomerEntity>> getAllCustomer() {
		Optional<List<CustomerEntity>> op = customerRepository.findByActive(true);
		if(op.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		List<CustomerEntity> customers = op.get();
//		List<CustomerEntity> customers = customerRepository.findAll();
		return ResponseEntity.ok(customers);
	}
	
	//customer get by it id
	@GetMapping("{customerId}")
	public ResponseEntity<?> getCustomerById(@PathVariable Integer customerId) {
		Optional<CustomerEntity> op = customerRepository.findById(customerId);
		if(op.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		else {
			return ResponseEntity.ok(op.get());
		}
	}
	
	//update customer by it id
	@PutMapping
	public ResponseEntity<?> updateCustomer(@RequestBody CustomerBean customerBean){
		Optional<CustomerEntity> op = customerRepository.findById(customerBean.getCustomerId());
		if(op.isEmpty()) {
			return ResponseEntity.ok("Invalid CustomerId");
		}
		
		// Q: sir we write directly like customerRepository.save(customerBean)
		CustomerEntity customerEntity = op.get();
		customerEntity.setFirstName(customerBean.getFirstName());
		customerEntity.setLastName(customerBean.getLastName());
		customerEntity.setEmail(customerBean.getEmail());
		customerEntity.setPassword(encoder.encode(customerBean.getPassword()));
		customerEntity.setProfilePicPath(customerBean.getProfilePicPath());
		customerEntity.setGender(customerBean.getGender());
		customerEntity.setBornYear(customerBean.getBornYear());
		customerEntity.setContactNum(customerBean.getContactNum());
		customerEntity.setActive(customerBean.getActive());
		
		customerRepository.save(customerEntity);
		return ResponseEntity.ok("Customer Details are successfully updated.");
	}
	
	//customer delete by it id
	@DeleteMapping("{customerId}")
	public ResponseEntity<?> deleteCustomer(@PathVariable("customerId") Integer customerId) {
		Optional<CustomerEntity> op = customerRepository.findById(customerId);
		if(op.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		CustomerEntity customerEntity = op.get();
		customerEntity.setActive(false);
		
		customerRepository.save(customerEntity);
		return ResponseEntity.ok("Customer is successfully soft deleted.");
	}
}