package com.example.CentralMethodistChurch.DTO;

import com.example.CentralMethodistChurch.Enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Ashwini Charles on 5/6/2024
 * @project spring-church-app
 */

@Data
@AllArgsConstructor
public class Events {
    private EventType eventType;
    private LocalDate date;
    private List<String> memberName;
}
