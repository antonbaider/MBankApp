package com.mthree.mbank.repository;

import com.mthree.mbank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProfile_Username(String username);

    boolean existsByProfile_Username(String username);

    boolean existsByProfile_Email(String email);

    boolean existsByProfile_Ssn(String ssn);

    boolean existsByProfile_Phone(String phone);

    @Query("SELECT u FROM User u JOIN u.family f WHERE f.id = :familyMemberId")
    List<User> findUsersByFamilyMemberId(@Param("familyMemberId") Long familyMemberId);
}