package com.mthree.mbank.entity;

import com.mthree.mbank.entity.enums.CurrencyType;
import com.mthree.mbank.listener.AccountEntityListener;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "accounts", uniqueConstraints = {
        @UniqueConstraint(columnNames = "card_number")
})
@EntityListeners(AccountEntityListener.class) // Registering the entity listener
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    private LocalDate creationDate;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType currency;

    @Column(nullable = false)
    private BigDecimal balance;

    @ToString.Exclude
    @Column(name = "card_number", nullable = false, unique = true, length = 16)
    private String cardNumber;

    @ManyToMany(mappedBy = "accounts")
    @ToString.Exclude
    private Set<UserGroup> userGroups;

    @ManyToMany
    @JoinTable(name = "account_services", joinColumns = @JoinColumn(name = "account_id"), inverseJoinColumns = @JoinColumn(name = "service_id"))
    private Set<Service> services;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @OneToMany(mappedBy = "senderAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> sentTransactions;

    @OneToMany(mappedBy = "receiverAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> receivedTransactions;
}