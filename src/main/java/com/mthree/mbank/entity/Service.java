package com.mthree.mbank.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "services")
@Data
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;

    @ManyToMany(mappedBy = "services")
    @ToString.Exclude
    private Set<Account> accounts;
}