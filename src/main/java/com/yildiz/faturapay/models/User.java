package com.yildiz.faturapay.models;

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
        private Role role;

        @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Invoice> invoices;

    }