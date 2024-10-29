package com.example.CentralMethodistChurch.Service.Impl;

import com.example.CentralMethodistChurch.Entity.Inventory;
import com.example.CentralMethodistChurch.Repository.InventoryRepository;
import com.example.CentralMethodistChurch.Service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Ashwini Charles on 10/28/2024
 * @project spring-church-app
 */
@Service
public class InventoryServiceimpl implements InventoryService {
    /**
     * @return
     */

    @Autowired
    InventoryRepository inventoryRepository;

    @Override
    public List<Inventory> fetchAllInventory() {
        return inventoryRepository.findAll();
    }

    @Override
    public void saveInventory(Inventory item) {
        inventoryRepository.save(item);
    }
}
