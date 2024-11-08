package com.mthree.mbank.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "user_groups")
@Data
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(mappedBy = "userGroups")
    @ToString.Exclude
    private Set<User> users;

    @ManyToMany
    @JoinTable(name = "group_accounts", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "account_id"))
    @ToString.Exclude
    private Set<Account> accounts;
}