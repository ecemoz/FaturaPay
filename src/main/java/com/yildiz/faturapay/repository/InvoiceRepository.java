package com.yildiz.faturapay.repository;

import com.yildiz.faturapay.models.Invoice;
import com.yildiz.faturapay.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByUser(User user);
}
