package com.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "restaurants")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestaurantEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer restaurantId;
	String title;
	String category;
	String description;
	String timings;
	String address;
	String contactNum;
	String lat;
	String log;
	Integer pincode;
	Integer online;
	String email;
	String password;
	Boolean active = true;
	String restaurantImagePath;
	String token;
	
	@OneToMany(mappedBy = "restaurantEntity")
	@JsonManagedReference
	List<MenuEntity> menus = new ArrayList<>();
	
	@OneToMany(mappedBy = "restaurantEntity")
	@JsonBackReference
	List<CartEntity> carts = new ArrayList<>();//restaurant have multiple cart
}
