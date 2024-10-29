package com.example.CentralMethodistChurch.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Ashwini Charles on 10/28/2024
 * @project spring-church-app
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "inventory_id", nullable = false)
    private Long inventoryId;

    private String itemType;

    private Integer quantity;

    private long price;

}
