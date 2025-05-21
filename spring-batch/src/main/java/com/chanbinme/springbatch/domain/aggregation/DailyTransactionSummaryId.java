package com.chanbinme.springbatch.domain.aggregation;

import static jakarta.persistence.EnumType.*;

import com.chanbinme.springbatch.domain.transaction.TransactionType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
@Getter
@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyTransactionSummaryId implements Serializable {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate transactionDate;

    @Enumerated(value = STRING)
    private TransactionType transactionType;
}
