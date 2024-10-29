package com.example.CentralMethodistChurch.Repository;

import com.example.CentralMethodistChurch.Entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Ashwini Charles on 10/28/2024
 * @project spring-church-app
 */

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {

}
