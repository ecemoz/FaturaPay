package com.yildiz.faturapay.repository;

import com.yildiz.faturapay.models.Subscription;
import com.yildiz.faturapay.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByConsumer(User user);
    List<Subscription> findByNextPaymentDate(LocalDate nextPaymentDate);
}
