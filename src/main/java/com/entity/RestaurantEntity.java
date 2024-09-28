package com.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	
	@JsonIgnore
	@OneToMany(mappedBy = "restaurantEntity")
	List<MenuEntity> menus;
	
	@JsonIgnore
	@OneToMany(mappedBy = "restaurantEntity")
	List<CartEntity> carts;//restaurant have multiple cart
}
