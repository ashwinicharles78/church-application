/**
 * @author Ashwini Charles on 3/3/2024
 * @project CentralMethodistChurch
 */

package com.example.CentralMethodistChurch.Controller;

import com.example.CentralMethodistChurch.DTO.Events;
import com.example.CentralMethodistChurch.Entity.FamilyMember;
import com.example.CentralMethodistChurch.Entity.Inventory;
import com.example.CentralMethodistChurch.Service.InventoryService;
import com.example.CentralMethodistChurch.Service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping(path = "/all-inventory")
    private List<Inventory> fetchAllInventory() {

    return inventoryService.fetchAllInventory();
    }

    @PostMapping(path = "/inventory")
    public void saveAllMembers(@RequestBody Inventory item) {
        inventoryService.saveInventory(item);
    }

}
