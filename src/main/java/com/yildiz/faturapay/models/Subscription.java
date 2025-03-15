package com.yildiz.faturapay.models;

import com.yildiz.faturapay.utils.SubscriptionPeriod;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate nextPaymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionPeriod period;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User consumer ;
}
