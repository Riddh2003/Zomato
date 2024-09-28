package com.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
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
@Table(name = "carts")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer	cartId;//	PK
	
	//Many carts can belong to one customer
	@ManyToOne
	@JoinColumn(name = "customerId")
	CustomerEntity customerEntity;
	
	//Each cart is associated with one restaurant
	@ManyToOne
	@JoinColumn(name = "restaurantId")
	RestaurantEntity restaurantEntity;
	
	@OneToMany(mappedBy = "cartEntity",cascade = CascadeType.ALL)
	List<CartItemEntity> cartItems;
	
	Integer totalQty;
}
/*
 * 
 * 			cart			Restaurant
 * 			cartid   		restaurantId
 * 			2					1
 * 			3					2		
 * 			4					3
 * 			
 * 			A CustomerEntity (many carts per customer).
			A RestaurantEntity (one cart per restaurant).
			A MenuEntity (one or more menus associated with the items in the cart).
			MenuItemEntity (items from menus can be added to the cart).
 * 
 * 
 * 
 * */

	
