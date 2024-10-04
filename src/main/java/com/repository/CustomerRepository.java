package com.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.CustomerEntity;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer>{
	Optional<CustomerEntity> findByEmail(String email);
	Optional<List<CustomerEntity>> findByActive(boolean active);
}
