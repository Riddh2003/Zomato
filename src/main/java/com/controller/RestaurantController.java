package com.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bean.RestaurantBean;
import com.entity.RestaurantEntity;
import com.repository.RestaurantRepository;
import com.service.RestaurantService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {
	
	@Autowired
	RestaurantRepository restaurantRepository;
	
	@Autowired
	RestaurantService restaurantService;
	
	//Read all restaurant
	@GetMapping
	public ResponseEntity<List<RestaurantEntity>> getallrestaurant(){
		Optional<List<RestaurantEntity>> op = restaurantRepository.findByActive(true);
		if(op.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		List<RestaurantEntity> restaurants = op.get();
		return ResponseEntity.ok(restaurants);
	}
	
	//read restaurant using id
	@GetMapping("{restaurantId}")
	public ResponseEntity<?> getRestaurantById(@PathVariable("restaurantId") Integer restaurantId,HttpSession session) {
		Optional<RestaurantEntity> op = restaurantRepository.findById(restaurantId);
		if(op.isEmpty()) {
			return ResponseEntity.noContent().build();
		}else {	
			RestaurantEntity restaurantEntity = op.get();
			if(!restaurantEntity.getRestaurantId().equals((Integer)session.getAttribute("restaurantId"))) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please, Login first."); 
			}
			return ResponseEntity.ok(restaurantEntity);
		}
	}
	
	//update restaurant
	@PutMapping("{restaurantId}")
	public ResponseEntity<?> updateRestaurantById(@PathVariable Integer restaurantId,
			@RequestBody RestaurantBean restaurantBean,
			HttpSession session){
		String str = restaurantService.checkLoginOrNot(restaurantId);
		if(!str.toLowerCase().equals("success")) {
			return ResponseEntity.badRequest().body(str);
		}
		Optional<RestaurantEntity> op = restaurantRepository.findById(restaurantId);
		RestaurantEntity restaurant = op.get() ;
		if(!op.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Restaurant not found with ID: " + restaurantId);
		}
		Integer loginRestaurantId = (Integer) session.getAttribute("restaurantId");
		if(loginRestaurantId == null || !restaurant.getRestaurantId().equals(loginRestaurantId)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update the details."); 
		}
		restaurant.setTitle(restaurantBean.getTitle());
		restaurant.setCategory(restaurantBean.getCategory());
		restaurant.setDescription(restaurantBean.getDescription());
		restaurant.setTimings(restaurantBean.getTimings());
		restaurant.setAddress(restaurantBean.getAddress());
		restaurant.setContactNum(restaurantBean.getContactNum());
		restaurant.setLat(restaurantBean.getLat());
		restaurant.setLog(restaurantBean.getLog());
		restaurant.setPincode(restaurantBean.getPincode());
		restaurant.setOnline(restaurantBean.getOnline());
		restaurant.setEmail(restaurantBean.getEmail());
		restaurant.setPassword(restaurantBean.getPassword());
		restaurant.setRestaurantImagePath(restaurantBean.getRestaurantImagePath());
		
		RestaurantEntity updatedRestaurant = restaurantRepository.save(restaurant);
		return ResponseEntity.ok(updatedRestaurant);
	}
	
	//soft delete restaurant by id
	@DeleteMapping("{restaurantId}")
	public ResponseEntity<?> softDeleteRestaurant(@PathVariable Integer restaurantId,HttpSession session){
		Integer loginRestaurantId = (Integer) session.getAttribute("restaurantId");
		String loginStatus = restaurantService.checkLoginOrNot(loginRestaurantId);
		if(!loginStatus.equalsIgnoreCase("success")){
			return ResponseEntity.badRequest().body(loginStatus);
		}
		Optional<RestaurantEntity> op = restaurantRepository.findById(restaurantId);
		if(!op.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Restaurant not found with ID: "+restaurantId);
		}
		RestaurantEntity updateRestaurant = op.get();
		if(!updateRestaurant.getRestaurantId().equals(loginRestaurantId)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete.");
		}
		updateRestaurant.setActive(false);
		restaurantRepository.save(updateRestaurant);
		return ResponseEntity.ok("Restaurant soft deleted successfully.");
	}
}
