package com.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "menuItems")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuItemEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer itemId;
	
	@ManyToOne
	@JoinColumn(name = "menuId")
	@JsonBackReference
	MenuEntity menuEntity;
	
	@ManyToOne
	@JoinColumn(name = "restaurantId")
	@JsonBackReference
	RestaurantEntity restaurantEntity;
	
	Boolean active = true;
	String title;
	String description;
	String itemaImagePath;
	Integer price;
	Boolean isOffer = false;
	Integer offerQty;
	Integer offerPercentage;
	Integer offerQtyCount;
	Integer uptoAmt;
	Integer uptoAmtCondition;
	
	@OneToMany(mappedBy = "menuItemEntity")
	@JsonBackReference
	List<CartItemEntity> cartItems = new ArrayList<>();
}