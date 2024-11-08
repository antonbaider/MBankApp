package com.mthree.mbank.entity;

import com.mthree.mbank.entity.enums.Role;
import com.mthree.mbank.entity.enums.Status;
import com.mthree.mbank.entity.enums.UserType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private UserProfile profile; // Ensure UserProfile is correctly linked

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime updateDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_family", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "family_member_id"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> family;

    @ManyToMany
    @JoinTable(name = "user_group", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    @ToString.Exclude
    private Set<UserGroup> userGroups;

    @OneToMany(mappedBy = "sender")
    @ToString.Exclude
    private Set<Transaction> sentTransactions;

    @OneToMany(mappedBy = "receiver")
    @ToString.Exclude
    private Set<Transaction> receivedTransactions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Account> accounts;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return profile != null ? profile.getPassword() : null;
    }

    @Override
    public String getUsername() {
        return profile != null ? profile.getUsername() : null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}