package com.yildiz.faturapay.controllers;

import com.yildiz.faturapay.models.Subscription;
import com.yildiz.faturapay.services.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/subscriptions")
@PreAuthorize("hasRole('USER')")
public class SubscriptionController {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }


    @GetMapping
    public ResponseEntity<?> getUserSubscriptions() {
        try {
            List<Subscription> subscriptions = subscriptionService.getUserSubscriptions();
            if (subscriptions.isEmpty()) {
                return ResponseEntity.ok("Henüz kayıtlı abonelik bulunmamaktadır.");
            }
            return ResponseEntity.ok(subscriptions);
        } catch (Exception e) {
            logger.error("Abonelikler getirilirken hata oluştu: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Abonelikler getirilirken bir hata oluştu.");
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getSubscriptionById(@PathVariable Long id) {
        try {
            Optional<Subscription> subscription = subscriptionService.getSubscriptionById(id);
            if (subscription.isPresent()) {
                return ResponseEntity.ok(subscription.get());
            } else {
                return ResponseEntity.status(404).body("Abonelik bulunamadı: ID=" + id);
            }
        } catch (Exception e) {
            logger.error("Abonelik getirilirken hata oluştu: ID={}, Hata: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().body("Abonelik getirilirken bir hata oluştu.");
        }
    }


    @PostMapping
    public ResponseEntity<?> addSubscription(@RequestBody Subscription subscription) {
        try {
            if (subscription.getServiceName() == null || subscription.getServiceName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Abonelik adı boş olamaz.");
            }
            if (subscription.getAmount() == null || subscription.getAmount().doubleValue() <= 0) {
                return ResponseEntity.badRequest().body("Abonelik ücreti sıfırdan büyük olmalıdır.");
            }

            Subscription createdSubscription = subscriptionService.addSubscription(subscription);
            return ResponseEntity.ok(createdSubscription);
        } catch (Exception e) {
            logger.error("Abonelik eklenirken hata oluştu: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Abonelik eklenirken bir hata oluştu.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSubscription(@PathVariable Long id, @RequestBody Subscription subscriptionDetails) {
        try {
            if (subscriptionDetails.getServiceName() == null || subscriptionDetails.getServiceName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Abonelik adı boş olamaz.");
            }
            if (subscriptionDetails.getAmount() == null || subscriptionDetails.getAmount().doubleValue() <= 0) {
                return ResponseEntity.badRequest().body("Abonelik ücreti sıfırdan büyük olmalıdır.");
            }

            Subscription updatedSubscription = subscriptionService.updateSubscription(id, subscriptionDetails);
            return ResponseEntity.ok(updatedSubscription);

        } catch (RuntimeException e) {
            logger.error("Abonelik güncellenirken hata oluştu: ID={}, Hata: {}", id, e.getMessage());
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Abonelik güncellenirken beklenmedik bir hata oluştu: ID={}, Hata: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().body("Abonelik güncellenirken bir hata oluştu.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubscription(@PathVariable Long id) {
        try {
            subscriptionService.deleteSubscription(id);
            return ResponseEntity.ok("Abonelik başarıyla silindi: ID=" + id);
        } catch (RuntimeException e) {
            logger.error("Abonelik silinirken hata oluştu: ID={}, Hata: {}", id, e.getMessage());
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Abonelik silinirken beklenmedik bir hata oluştu: ID={}, Hata: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().body("Abonelik silinirken bir hata oluştu.");
        }
    }
}
