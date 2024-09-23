package com.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.RestaurantEntity;
import java.util.List;


@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Integer> {
	Optional<RestaurantEntity> findByEmailAndPassword(String email,String password); 
	Optional<RestaurantEntity> findByEmail(String email);
}
