package dev.harakki.shiftlab.repository;

import dev.harakki.shiftlab.domain.Seller;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long>, JpaSpecificationExecutor<Seller> {

    @Query("""
            SELECT s
            FROM Seller s
                     LEFT JOIN Transaction t ON t.seller = s
                AND t.transactionDate >= :startDate
                AND t.transactionDate <= :endDate
            GROUP BY s
            HAVING COALESCE(SUM(t.amount), 0) < :thresholdAmount
            """)
    List<Seller> findSellersWithTotalAmountLessThanInPeriod(
            @Param("thresholdAmount") BigDecimal thresholdAmount,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    Optional<Seller> findSellerById(Long id);

}
