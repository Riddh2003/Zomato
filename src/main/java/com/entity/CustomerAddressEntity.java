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
@Table(name = "customerAddress")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerAddressEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer addressId;
	
	@ManyToOne
	@JoinColumn(name = "customerId")
	@JsonBackReference
	CustomerEntity customerEntity;
	
	String title;
	String addressLine;
	String pincode;
	String lat;
	String log;
	Boolean isactive = true;
}
