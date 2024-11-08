package com.mthree.mbank.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_statuses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status; // E.g., "ACTIVE", "INACTIVE"
}