package com.example.userservice1.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tokens", schema = "public")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @NotNull
    @Column(name = "id")
    private Integer id;

    @Column(name = "token", unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "tokenType")
    private TokenType tokenType;

    @Column(name = "expired")
    private boolean expired;

    @Column(name = "revoked")
    private boolean revoked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;


}
