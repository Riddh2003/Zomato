package com.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entity.CartEntity;
import com.entity.CustomerEntity;
import com.entity.RestaurantEntity;
import java.util.List;


public interface CartRepository extends JpaRepository<CartEntity, Integer> {
	Optional<CartEntity> findByCustomerEntityAndRestaurantEntity(CustomerEntity customerEntity,RestaurantEntity restaurantEntity);
	Optional<List<CartEntity>> findByCustomerEntity(CustomerEntity customerEntity);
}
