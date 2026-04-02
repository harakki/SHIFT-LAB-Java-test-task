package dev.harakki.shiftlab.repository;

import dev.harakki.shiftlab.domain.Seller;
import dev.harakki.shiftlab.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    @Query("""
            SELECT t.seller
            FROM Transaction t
            WHERE t.transactionDate >= :startDate
              AND t.transactionDate <= :endDate
            GROUP BY t.seller
            ORDER BY SUM(t.amount) DESC, t.seller.id ASC
            """)
    Optional<Seller> findFirstSellerInPeriodOrderByAmountDescSellerIdAsc(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT t.transactionDate FROM Transaction t WHERE t.seller.id = :sellerId ORDER BY t.transactionDate ASC")
    List<LocalDateTime> findAllTransactionDatesBySellerIdOrderByTransactionDateAsc(Long sellerId);

    Page<Transaction> findBySellerId(Long sellerId, Pageable pageable);

}
