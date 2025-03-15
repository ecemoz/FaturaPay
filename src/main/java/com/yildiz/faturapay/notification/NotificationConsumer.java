package com.yildiz.faturapay.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);

    @KafkaListener(topics = "notifications", groupId = "notification-group")
    public void consumeNotification(String message) {
        logger.info("Kafka'dan bildirim alındı: {}", message);
    }
}
