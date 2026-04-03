package dev.harakki.shiftlab.repository;

import dev.harakki.shiftlab.TestcontainersConfiguration;
import dev.harakki.shiftlab.domain.PaymentType;
import dev.harakki.shiftlab.domain.Seller;
import dev.harakki.shiftlab.domain.Transaction;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class JpaMappingTest {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void transactionMapping_shouldPersistRelationAndEnumAsString() {
        Seller seller = sellerRepository.save(Seller.builder().name("MapSeller").contactInfo("m").build());

        Transaction saved = transactionRepository.save(Transaction.builder()
                .seller(seller)
                .amount(new BigDecimal("15.50"))
                .paymentType(PaymentType.CARD)
                .build());

        entityManager.flush();
        entityManager.clear();

        var reloaded = transactionRepository.findById(saved.getId());
        assertTrue(reloaded.isPresent());
        assertEquals(seller.getId(), reloaded.get().getSeller().getId());

        Object rawPaymentType = entityManager.createNativeQuery("select payment_type from transactions where id = :id")
                .setParameter("id", saved.getId())
                .getSingleResult();
        assertEquals("CARD", rawPaymentType);
    }

    @Test
    void creationTimestamps_shouldBeFilledOnPersist() {
        Seller seller = sellerRepository.save(Seller.builder().name("Stamped").contactInfo("s").build());

        Transaction tx = transactionRepository.save(Transaction.builder()
                .seller(seller)
                .amount(new BigDecimal("10.00"))
                .paymentType(PaymentType.CASH)
                .build());

        entityManager.flush();

        assertNotNull(seller.getRegistrationDate());
        assertNotNull(tx.getTransactionDate());
    }

    @Test
    void softDelete_shouldHideSellerFromRepositoryAndKeepDatabaseRow() {
        Seller seller = sellerRepository.save(Seller.builder().name("Soft").contactInfo("d").build());
        Long sellerId = seller.getId();

        sellerRepository.deleteById(sellerId);
        entityManager.flush();

        assertTrue(sellerRepository.findById(sellerId).isEmpty());

        Number rowCount = (Number) entityManager.createNativeQuery("select count(*) from sellers where id = :id")
                .setParameter("id", sellerId)
                .getSingleResult();
        assertEquals(1L, rowCount.longValue());
    }

}
