package com.yildiz.faturapay.services;

import com.yildiz.faturapay.models.Subscription;
import com.yildiz.faturapay.models.User;
import com.yildiz.faturapay.notification.NotificationProducer;
import com.yildiz.faturapay.repository.SubscriptionRepository;
import com.yildiz.faturapay.repository.UserRepository;
import com.yildiz.faturapay.utils.SubscriptionPeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final NotificationProducer notificationProducer;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserRepository userRepository, NotificationProducer notificationProducer) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.notificationProducer = notificationProducer;
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Kimliği doğrulanan kullanıcı bulunamadı: {}", username);
                    return new UsernameNotFoundException("Kullanıcı bulunamadı: " + username);
                });
    }

    public List<Subscription> getUserSubscriptions() {
        try {
            User user = getAuthenticatedUser();
            List<Subscription> subscriptions = subscriptionRepository.findByConsumer(user);
            logger.info("{} kullanıcısının abonelikleri getirildi.", user.getUsername());
            return subscriptions;
        } catch (Exception e) {
            logger.error("Abonelikler getirilirken hata oluştu: {}", e.getMessage());
            throw new RuntimeException("Abonelikler getirilirken bir hata oluştu.");
        }
    }

    @Transactional
    public Subscription addSubscription(Subscription subscription) {
        try {
            User user = getAuthenticatedUser();
            subscription.setConsumer(user);

            if (subscription.getPeriod() == SubscriptionPeriod.MONTHLY) {
                subscription.setNextPaymentDate(subscription.getStartDate().plusMonths(1));
            } else if (subscription.getPeriod() == SubscriptionPeriod.YEARLY) {
                subscription.setNextPaymentDate(subscription.getStartDate().plusYears(1));
            }

            Subscription savedSubscription = subscriptionRepository.save(subscription);
            logger.info("{} kullanıcısı yeni abonelik ekledi: {} - Tutar: {} - Sonraki ödeme: {}",
                    user.getUsername(), subscription.getServiceName(), subscription.getAmount(), subscription.getNextPaymentDate());
            return savedSubscription;
        } catch (Exception e) {
            logger.error("Abonelik eklenirken hata oluştu: {}", e.getMessage());
            throw new RuntimeException("Abonelik eklenirken bir hata oluştu.");
        }
    }

    public Optional<Subscription> getSubscriptionById(Long id) {
        try {
            return subscriptionRepository.findById(id);
        } catch (Exception e) {
            logger.error("Abonelik getirilirken hata oluştu: ID={}, Hata: {}", id, e.getMessage());
            throw new RuntimeException("Abonelik getirilirken bir hata oluştu.");
        }
    }

    @Transactional
    public Subscription updateSubscription(Long id, Subscription updatedSubscription) {
        try {
            User user = getAuthenticatedUser();
            Subscription existingSubscription = subscriptionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Güncellenmek istenen abonelik bulunamadı!"));

            if (!existingSubscription.getConsumer().getId().equals(user.getId())) {
                logger.error("Yetkisiz işlem! {} kullanıcısı başkasına ait aboneliği güncellemeye çalıştı: ID={}",
                        user.getUsername(), id);
                throw new RuntimeException("Yetkisiz işlem! Bu aboneliği güncelleme yetkiniz yok.");
            }

            existingSubscription.setServiceName(updatedSubscription.getServiceName());
            existingSubscription.setAmount(updatedSubscription.getAmount());
            existingSubscription.setNextPaymentDate(updatedSubscription.getNextPaymentDate());

            Subscription savedSubscription = subscriptionRepository.save(existingSubscription);
            logger.info("{} kullanıcısı aboneliği güncelledi: {} - Yeni tutar: {}",
                    user.getUsername(), savedSubscription.getServiceName(), savedSubscription.getAmount());
            return savedSubscription;
        } catch (Exception e) {
            logger.error("Abonelik güncellenirken hata oluştu: {}", e.getMessage());
            throw new RuntimeException("Abonelik güncellenirken bir hata oluştu.");
        }
    }

    @Transactional
    public void deleteSubscription(Long id) {
        try {
            User user = getAuthenticatedUser();
            Subscription existingSubscription = subscriptionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Silinmek istenen abonelik bulunamadı!"));

            if (!existingSubscription.getConsumer().getId().equals(user.getId())) {
                logger.error("Yetkisiz işlem! {} kullanıcısı başkasına ait aboneliği silmeye çalıştı: ID={}",
                        user.getUsername(), id);
                throw new RuntimeException("Yetkisiz işlem! Bu aboneliği silme yetkiniz yok.");
            }

            subscriptionRepository.deleteById(id);
            logger.info("{} kullanıcısı aboneliği sildi: {}", user.getUsername(), existingSubscription.getServiceName());
        } catch (Exception e) {
            logger.error("Abonelik silinirken hata oluştu: {}", e.getMessage());
            throw new RuntimeException("Abonelik silinirken bir hata oluştu.");
        }
    }


    @Scheduled(cron = "0 0 9 * * ?") // Her gün sabah 9'da çalışır
    public void sendPaymentReminders() {
        LocalDate today = LocalDate.now();
        List<Subscription> dueSubscriptions = subscriptionRepository.findByNextPaymentDate(today);

        for (Subscription subscription : dueSubscriptions) {
            String message = "Ödeme hatırlatması: " + subscription.getServiceName() +
                    " servisi için " + subscription.getAmount() + " tutarında ödeme zamanı geldi.";
            notificationProducer.sendNotification(message);
        }
    }

}
