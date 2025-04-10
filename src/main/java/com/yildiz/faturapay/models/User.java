package com.yildiz.faturapay.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yildiz.faturapay.utils.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true)
        private String username;

        @Column(nullable = false)
        private String password;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Role role;

        @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonManagedReference
        private List<Invoice> invoices;

        @OneToMany(mappedBy = "consumer", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonManagedReference
        private List<Subscription> subscriptions;
    }