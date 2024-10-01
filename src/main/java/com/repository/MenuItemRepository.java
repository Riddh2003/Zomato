package com.repository;

import com.entity.MenuItemEntity;
import com.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Integer> {
    Optional<List<MenuItemEntity>> findAllByMenuEntityAndActiveTrue(MenuEntity menuEntity);
}
