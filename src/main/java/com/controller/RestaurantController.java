package com.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.entity.RestaurantEntity;
import com.repository.RestaurantRepository;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {
	
	@Autowired
	RestaurantRepository restaurantRepository;
	
	//Read all restaurant
	@GetMapping
	public List<RestaurantEntity> getallrestaurant(){
		List<RestaurantEntity> restaurants = restaurantRepository.findAll();
		return restaurants;
	}
	
	//read restaurant using id
	@GetMapping("{restaurantId}")
	public ResponseEntity<RestaurantEntity> getRestaurantById(@PathVariable("restaurantId") Integer restaurantId) {
		Optional<RestaurantEntity> op = restaurantRepository.findById(restaurantId);
		if(op.isEmpty()) {
			return ResponseEntity.noContent().build();
		}else {	
			RestaurantEntity restaurantEntity = op.get();
			return ResponseEntity.ok(restaurantEntity);
		}
	}
	
	//update or delete restaurant by id
	@PutMapping
	public ResponseEntity<?> updateOrDeleteRestaurant(@RequestBody RestaurantEntity restaurantEntity,@RequestParam String delete){
		Optional<RestaurantEntity> op = restaurantRepository.findById(restaurantEntity.getRestaurantId());
		if(op.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Restaurant Not Found.");
		}
		RestaurantEntity updateRestaurant = op.get();
		if ("true".equalsIgnoreCase(delete)) {
			updateRestaurant.setActive(0);
		}
		RestaurantEntity restoreRestaurant =  restaurantRepository.save(updateRestaurant);
		return ResponseEntity.ok(restoreRestaurant);
	}
}
