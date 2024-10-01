package com.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "cartItems")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer cartItemId;

	//Each cart item belongs to one cart
	@ManyToOne
	@JoinColumn(name = "cardId")
	@JsonBackReference
	CartEntity cartEntity;
	
	@ManyToOne
	@JoinColumn(name = "itemId")
	@JsonBackReference
	MenuItemEntity menuItemEntity;
	
	@ManyToOne
	@JoinColumn(name = "menuId")
	@JsonBackReference
	MenuEntity menuEntity;
	
	Integer qty;
}