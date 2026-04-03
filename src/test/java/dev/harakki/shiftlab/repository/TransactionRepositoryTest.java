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
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findFirstSellerInPeriodOrderByAmountDescSellerIdAsc_shouldRespectTieBreak() {
        Seller first = sellerRepository.save(Seller.builder().name("First").contactInfo("a").build());
        Seller second = sellerRepository.save(Seller.builder().name("Second").contactInfo("b").build());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusDays(1);
        LocalDateTime to = now.plusDays(1);

        transactionRepository.save(Transaction.builder()
                .seller(first)
                .amount(new BigDecimal("100.00"))
                .paymentType(PaymentType.CARD)
                .build());

        transactionRepository.save(Transaction.builder()
                .seller(second)
                .amount(new BigDecimal("100.00"))
                .paymentType(PaymentType.CASH)
                .build());

        var result = transactionRepository.findFirstSellerInPeriodOrderByAmountDescSellerIdAsc(from, to);

        assertTrue(result.isPresent());
        assertEquals(first.getId(), result.get().getId());
    }

    @Test
    void findAllTransactionDatesBySellerIdOrderByTransactionDateAsc_shouldReturnSortedDates() {
        Seller seller = sellerRepository.save(Seller.builder().name("Dates").contactInfo("d").build());

        LocalDateTime d1 = LocalDateTime.now().plusHours(3);
        LocalDateTime d2 = LocalDateTime.now().plusHours(1);
        LocalDateTime d3 = LocalDateTime.now().plusHours(2);

        var saved = transactionRepository.saveAll(List.of(
                Transaction.builder().seller(seller).amount(new BigDecimal("1.00")).paymentType(PaymentType.CARD).build(),
                Transaction.builder().seller(seller).amount(new BigDecimal("2.00")).paymentType(PaymentType.CASH).build(),
                Transaction.builder().seller(seller).amount(new BigDecimal("3.00")).paymentType(PaymentType.TRANSFER).build()
        ));

        updateTransactionDate(saved.get(0).getId(), d1);
        updateTransactionDate(saved.get(1).getId(), d2);
        updateTransactionDate(saved.get(2).getId(), d3);
        entityManager.flush();
        entityManager.clear();

        var result = transactionRepository.findAllTransactionDatesBySellerIdOrderByTransactionDateAsc(seller.getId());

        assertEquals(List.of(d2, d3, d1), result);
    }

    private void updateTransactionDate(Long transactionId, LocalDateTime transactionDate) {
        entityManager.createNativeQuery("update transactions set transaction_date = :transactionDate where id = :id")
                .setParameter("transactionDate", transactionDate)
                .setParameter("id", transactionId)
                .executeUpdate();
    }

    @Test
    void findBySellerId_shouldReturnOnlySellerTransactions() {
        Seller target = sellerRepository.save(Seller.builder().name("Target").contactInfo("t").build());
        Seller other = sellerRepository.save(Seller.builder().name("Other").contactInfo("o").build());

        transactionRepository.save(Transaction.builder()
                .seller(target)
                .amount(new BigDecimal("10.00"))
                .paymentType(PaymentType.CARD)
                .transactionDate(LocalDateTime.of(2026, 1, 1, 12, 0))
                .build());

        transactionRepository.save(Transaction.builder()
                .seller(other)
                .amount(new BigDecimal("20.00"))
                .paymentType(PaymentType.CARD)
                .transactionDate(LocalDateTime.of(2026, 1, 1, 13, 0))
                .build());

        var page = transactionRepository.findBySellerId(target.getId(), PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals(target.getId(), page.getContent().getFirst().getSeller().getId());
    }

}
