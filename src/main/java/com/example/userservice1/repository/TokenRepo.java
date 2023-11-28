package com.example.userservice1.repository;

import com.example.userservice1.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepo extends JpaRepository<Token, Integer> {

    @Query("""
    select t from Token t
        inner join User u
            on t.user.id = u.id
    where u.id = :userId and (t.expired = false or t.revoked = false)
    """)
    List<Token> findTokensByUser(Integer userId);

    Optional<Token> findByToken(String token);
}
