package com.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.RestaurantEntity;
import com.repository.RestaurantRepository;

@Service
public class RestaurantService {
	
	@Autowired
	RestaurantRepository restaurantRepository;
	
	public String checkLoginOrNot(Integer restaurantId) {
		if (restaurantId == null) {
	        return "Please, pass restaurantId.";
	    }
//		System.out.println("Checking login status for restaurantId: " + restaurantId);
	    Optional<RestaurantEntity> op = restaurantRepository.findById(restaurantId);
	    // If restaurant is not found
	    if (op.isEmpty()) {
	        return "Restaurant Not Found.";
	    }
	    RestaurantEntity restaurant = op.get();
//	    System.out.println("Restaurant active status: " + restaurant.getActive());
	    // If the restaurant is not active
	    if (!restaurant.getActive()) {  // Assuming 0 means inactive
	        return "Restaurant is not active.";
	    }   
	    return "Success";  // Everything is fine
	}
}
