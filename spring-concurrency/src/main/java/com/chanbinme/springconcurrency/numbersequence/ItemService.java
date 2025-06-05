package com.chanbinme.springconcurrency.numbersequence;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    @Retryable(
        retryFor = {ObjectOptimisticLockingFailureException.class},
        maxAttempts = 10, // 최대 10번 재시도
        backoff = @Backoff(delay = 200) // 재시도 간 200ms 대기
    )
    public int decreaseStock(Long itemId, int quantity) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        item.decreaseStock(quantity);

        return item.getStockQuantity();
    }

    @Transactional(readOnly = true)
    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    // 모든 재시도가 실패했을 때 호출되는 메서드 (선택)
    @Recover
    public void recover(OptimisticLockingFailureException e, Long itemId, int quantity) {
        // 실패 후 처리 로직 (예: 알림, 로그, 대체 처리 등)
        System.err.println("모든 재시도가 실패했습니다: " + e.getMessage());
    }
}
