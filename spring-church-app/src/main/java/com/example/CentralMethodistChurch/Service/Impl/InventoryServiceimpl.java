package com.example.CentralMethodistChurch.Service.Impl;

import com.example.CentralMethodistChurch.Entity.Inventory;
import com.example.CentralMethodistChurch.Repository.InventoryRepository;
import com.example.CentralMethodistChurch.Service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        List<Inventory> inventories = inventoryRepository.findAll();
        for(Inventory inventory : inventories ) {
            if(inventory.getItemType().equals(item.getItemType())){
                if(Objects.equals(inventory.getPrice(), item.getPrice())) {
                    item.setQuantity(item.getQuantity() + inventory.getQuantity());
                    inventoryRepository.deleteById(inventory.getInventoryId().toString());
                }
            }
        }
        inventoryRepository.save(item);
    }

    @Override
    public void deleteInventory(Inventory item) {
        inventoryRepository.deleteById(item.getInventoryId().toString());
    }

    @Override
    public void editInventory(Inventory item) {
        Optional<Inventory> inventory = inventoryRepository.findById(item.getInventoryId().toString());
        if(inventory.isPresent()) {
            inventory.get().setQuantity(item.getQuantity());
            inventory.get().setPrice(item.getPrice());
            inventory.get().setItemType(item.getItemType());
            inventoryRepository.save(inventory.get());
        }
    }
}
