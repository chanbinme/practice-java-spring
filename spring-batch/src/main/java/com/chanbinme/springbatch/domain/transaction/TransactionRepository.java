package com.chanbinme.springbatch.domain.transaction;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("select t.id from Transaction t where t.transactionDate >= :startDate and t.transactionDate < :endDate order by t.id asc limit 1")
    Long findIdByTransactionDateGreaterThanEqualAndTransactionDateLessThanOrderByIdAsc(@Param(value = "startDate") LocalDateTime startDate, @Param(value = "endDate") LocalDateTime endDate);

    @Query("select t.id from Transaction t where t.transactionDate >= :startDate and t.transactionDate < :endDate order by t.id desc limit 1")
    Long findIdByTransactionDateGreaterThanEqualAndTransactionDateLessThanOrderByIdDesc(@Param(value = "startDate") LocalDateTime startDate, @Param(value = "endDate") LocalDateTime endDate);

}
