package com.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bean.CustomerAddressBean;
import com.entity.CustomerAddressEntity;
import com.repository.CustomerAddressRepository;

@Service
public class CustomerAddressService {
	
	@Autowired
	CustomerAddressRepository customerAddressRepository;
	
	public String updateCustomerAddress(CustomerAddressBean customerAddressBean,Integer customerId,Integer loginCustomerId,Integer addressId) {
	    if (loginCustomerId == null || !loginCustomerId.equals(customerId)) {
	        return "Unauthorized. Please log in first.";
	    }
	    // Fetch the address by ID
	    Optional<CustomerAddressEntity> optionalAddress = customerAddressRepository.findById(addressId);
	    // Check if address exists
	    if (!optionalAddress.isPresent()) {
	        return "Address not found.";
	    }

	    CustomerAddressEntity customerAddress = optionalAddress.get();
	    // Check if the address belongs to the logged-in customer
	    if (!customerAddress.getCustomerEntity().getCustomerId().equals(loginCustomerId)) {
	        return "You are not authorized to view this address.";
	    }
	    customerAddress.setTitle(customerAddressBean.getTitle());
	    customerAddress.setAddressLine(customerAddressBean.getAddressLine());
	    customerAddress.setPincode(customerAddressBean.getPincode());
	    customerAddress.setLat(customerAddressBean.getLat());
	    customerAddress.setLog(customerAddressBean.getLog());
	    
	    customerAddressRepository.save(customerAddress);
	    return "success";
	}
	
	public String checkCustomerandAddress(Integer addressId, Integer loginCustomerId) {
		if (loginCustomerId == null) {
	        return "Unauthorized. Please log in first.";
	    }
	    // Fetch the address by ID
	    Optional<CustomerAddressEntity> optionalAddress = customerAddressRepository.findById(addressId);
	    // Check if address exists
	    if (!optionalAddress.isPresent()) {
	        return "Address not found.";
	    }
	    CustomerAddressEntity customerAddress = optionalAddress.get();
	    // Check if the address belongs to the logged-in customer
	    if (!customerAddress.getCustomerEntity().getCustomerId().equals(loginCustomerId)) {
	        return "You are not authorized to view this address.";
	    }
		return "success";
	}
}
