package com.entity;

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
@Table(name = "menuItems")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuItemEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer itemId;
	
	@ManyToOne
	@JoinColumn(name = "menuId")
	MenuEntity menuEntity;
	
	@ManyToOne
	@JoinColumn(name = "restaurantId")
	RestaurantEntity restaurantEntity;
	
	Integer active;
	String title;
	String description;
	String itemaImagePath;
	Integer price;
	Integer isOffer;
	Integer offerQty;
	Integer offerPercentage;
	Integer offerQtyCount;
	Integer uptoAmt;
	Integer uptoAmtCondition;
}
