package com.mthree.mbank.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // E.g., "CUSTOMER", "ADMIN"
}