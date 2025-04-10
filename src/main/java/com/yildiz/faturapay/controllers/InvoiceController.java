package com.yildiz.faturapay.controllers;

import com.yildiz.faturapay.models.Invoice;
import com.yildiz.faturapay.services.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllInvoices() {
        try {
            List<Invoice> invoices = invoiceService.getAllInvoices();
            if (invoices.isEmpty()) {
                return ResponseEntity.ok("Henüz kayıtlı fatura bulunmamaktadır.");
            }
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            logger.error("Faturalar getirilirken hata oluştu: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Faturalar getirilirken bir hata oluştu.");
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @invoiceService.isOwner(#id)")
    public ResponseEntity<Object> getInvoiceById(@PathVariable Long id) {
        try {
            Optional<Invoice> invoice = invoiceService.getInvoiceById(id);
            if (invoice.isPresent()) {
                return ResponseEntity.ok(invoice.get());
            } else {
                return ResponseEntity.status(404).body("Fatura bulunamadı: ID=" + id);
            }
        } catch (Exception e) {
            logger.error("Fatura getirilirken hata oluştu: ID={}, Hata: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().body("Fatura getirilirken bir hata oluştu.");
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> addInvoice(@RequestBody Invoice invoice) {
        try {
            if (invoice.getTitle() == null || invoice.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Fatura başlığı boş olamaz.");
            }
            if (invoice.getAmount() == null || invoice.getAmount().doubleValue() <= 0) {
                return ResponseEntity.badRequest().body("Fatura tutarı sıfırdan büyük olmalıdır.");
            }
            Invoice createdInvoice = invoiceService.addInvoice(invoice);
            return ResponseEntity.ok(createdInvoice);
        } catch (Exception e) {
            logger.error("Fatura eklenirken hata oluştu: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Fatura eklenirken bir hata oluştu.");
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @invoiceService.isOwner(#id)")
    public ResponseEntity<?> updateInvoice(@PathVariable Long id, @RequestBody Invoice invoiceDetails) {
        try {
            if (invoiceDetails.getTitle() == null || invoiceDetails.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Fatura başlığı boş olamaz.");
            }
            if (invoiceDetails.getAmount() == null || invoiceDetails.getAmount().doubleValue() <= 0) {
                return ResponseEntity.badRequest().body("Fatura tutarı sıfırdan büyük olmalıdır.");
            }

            Invoice updatedInvoice = invoiceService.updateInvoice(id, invoiceDetails);
            return ResponseEntity.ok(updatedInvoice);

        } catch (RuntimeException e) {
            logger.error("Fatura güncellenirken hata oluştu: ID={}, Hata: {}", id, e.getMessage());
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Fatura güncellenirken beklenmedik bir hata oluştu: ID={}, Hata: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().body("Fatura güncellenirken bir hata oluştu.");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @invoiceService.isOwner(#id)")
    public ResponseEntity<?> deleteInvoice(@PathVariable Long id) {
        try {
            invoiceService.deleteInvoice(id);
            return ResponseEntity.ok("Fatura başarıyla silindi: ID=" + id);
        } catch (RuntimeException e) {
            logger.error("Fatura silinirken hata oluştu: ID={}, Hata: {}", id, e.getMessage());
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Fatura silinirken beklenmedik bir hata oluştu: ID={}, Hata: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().body("Fatura silinirken bir hata oluştu.");
        }
    }
}
