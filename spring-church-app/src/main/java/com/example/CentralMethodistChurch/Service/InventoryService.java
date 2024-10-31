package com.example.CentralMethodistChurch.Service;

import com.example.CentralMethodistChurch.Entity.Inventory;

import java.util.List;

/**
 * @author Ashwini Charles on 10/28/2024
 * @project spring-church-app
 */
public interface InventoryService {

    List<Inventory> fetchAllInventory();

    void saveInventory(Inventory item);

    void deleteInventory(Inventory item);

    void editInventory(Inventory item);
}
