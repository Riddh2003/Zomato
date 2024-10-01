package com.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "menus")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer menuId;
	
	@ManyToOne
	@JoinColumn(name = "restaurantId")
	@JsonBackReference
	RestaurantEntity restaurantEntity;
	
	boolean active = true; 
	String title;
	String menuImagePath;
	
	@OneToMany(mappedBy = "menuEntity")
	@JsonManagedReference
	List<MenuItemEntity> menuItems;
	
	@OneToMany(mappedBy = "menuEntity")
	@JsonBackReference
	List<CartItemEntity> cartItems;
}
