package dev.harakki.shiftlab.repository;

import dev.harakki.shiftlab.TestcontainersConfiguration;
import dev.harakki.shiftlab.domain.PaymentType;
import dev.harakki.shiftlab.domain.Seller;
import dev.harakki.shiftlab.domain.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class SellerRepositoryTest {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void findSellersWithTotalAmountLessThanInPeriod_shouldApplyPeriodAndThreshold() {
        Seller low = sellerRepository.save(Seller.builder().name("Low").contactInfo("l").build());
        Seller high = sellerRepository.save(Seller.builder().name("High").contactInfo("h").build());

        transactionRepository.save(Transaction.builder()
                .seller(low)
                .amount(new BigDecimal("90.00"))
                .paymentType(PaymentType.CARD)
                .build());

        transactionRepository.save(Transaction.builder()
                .seller(high)
                .amount(new BigDecimal("120.00"))
                .paymentType(PaymentType.CASH)
                .build());

        var now = java.time.LocalDateTime.now();
        var result = sellerRepository.findSellersWithTotalAmountLessThanInPeriod(
                new BigDecimal("100.00"),
                now.minusDays(1),
                now.plusDays(1),
                PageRequest.of(0, 10, Sort.by("name"))
        );

        assertEquals(1, result.getTotalElements());
        assertEquals("Low", result.getContent().getFirst().getName());
    }

    @Test
    void findSellersWithTotalAmountLessThanInPeriod_shouldSupportNullBounds() {
        Seller included = sellerRepository.save(Seller.builder().name("Included").contactInfo("i").build());
        Seller excluded = sellerRepository.save(Seller.builder().name("Excluded").contactInfo("e").build());

        transactionRepository.save(Transaction.builder()
                .seller(included)
                .amount(new BigDecimal("40.00"))
                .paymentType(PaymentType.CARD)
                .build());

        transactionRepository.save(Transaction.builder()
                .seller(excluded)
                .amount(new BigDecimal("80.00"))
                .paymentType(PaymentType.TRANSFER)
                .build());

        var result = sellerRepository.findSellersWithTotalAmountLessThanInPeriod(
                new BigDecimal("50.00"),
                null,
                null,
                PageRequest.of(0, 10, Sort.by("name"))
        );

        assertEquals(1, result.getTotalElements());
        assertEquals("Included", result.getContent().getFirst().getName());
    }

}
