package com.yildiz.faturapay.services;

import com.yildiz.faturapay.models.Invoice;
import com.yildiz.faturapay.models.User;
import com.yildiz.faturapay.repository.InvoiceRepository;
import com.yildiz.faturapay.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public InvoiceService(InvoiceRepository invoiceRepository, UserRepository userRepository, AuthService authService) {
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Kimlik doğrulanan kullanıcı: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Kullanıcı bulunamadı: {}", username);
                    return new UsernameNotFoundException("Kullanıcı bulunamadı: " + username);
                });
    }

    public List<Invoice> getAllInvoices() {
        User user = getAuthenticatedUser();
        logger.info("{} kullanıcısının faturaları alınıyor.", user.getUsername());
        return invoiceRepository.findByUser(user);
    }

    public Invoice addInvoice(Invoice invoice) {
        User user = getAuthenticatedUser();
        invoice.setUser(user);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        logger.info("Yeni fatura eklendi: ID={}, Başlık={}, Tutar={}", savedInvoice.getId(), savedInvoice.getTitle(), savedInvoice.getAmount());
        return savedInvoice;
    }

    public Optional<Invoice> getInvoiceById(Long id) {
        logger.info("Fatura aranıyor: ID={}", id);
        return invoiceRepository.findById(id);
    }

    public Invoice updateInvoice(Long id, Invoice invoiceDetails) {
        User user = getAuthenticatedUser();
        logger.info("{} kullanıcısı faturayı güncellemeye çalışıyor: ID={}", user.getUsername(), id);

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Güncellenmek istenen fatura bulunamadı: ID={}", id);
                    return new RuntimeException("Fatura bulunamadı!");
                });


        if (!invoice.getUser().getId().equals(user.getId())) {
            logger.error("Yetkisiz işlem! {} kullanıcısı kendisine ait olmayan faturayı güncellemeye çalıştı: ID={}", user.getUsername(), id);
            throw new RuntimeException("Yetkisiz işlem! Bu faturayı güncelleyemezsiniz.");
        }

        invoice.setTitle(invoiceDetails.getTitle());
        invoice.setAmount(invoiceDetails.getAmount());
        invoice.setDueDate(invoiceDetails.getDueDate());

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        logger.info("Fatura güncellendi: ID={}, Kullanıcı={}, Yeni Başlık={}, Yeni Tutar={}",
                updatedInvoice.getId(), user.getUsername(), updatedInvoice.getTitle(), updatedInvoice.getAmount());

        return updatedInvoice;
    }

    public boolean isOwner(Long invoiceId) {
        User authenticatedUser = authService.getAuthenticatedUser();
        Optional<Invoice> invoice = getInvoiceById(invoiceId);
        return invoice.isPresent() && invoice.get().getUser().getId().equals(authenticatedUser.getId());
    }


    public void deleteInvoice(Long id) {
        User user = getAuthenticatedUser();
        logger.info("{} kullanıcısı faturayı silmeye çalışıyor: ID={}", user.getUsername(), id);

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Fatura bulunamadı: ID={}", id);
                    return new RuntimeException("Fatura bulunamadı!");
                });

        if (!invoice.getUser().getId().equals(user.getId())) {
            logger.error("Yetkisiz işlem! {} kullanıcısı kendisine ait olmayan faturayı silmeye çalıştı: ID={}", user.getUsername(), id);
            throw new RuntimeException("Yetkisiz işlem! Bu faturayı silemezsiniz.");
        }

        invoiceRepository.deleteById(id);
        logger.info("Fatura silindi: ID={}, Kullanıcı={}", id, user.getUsername());
    }
}