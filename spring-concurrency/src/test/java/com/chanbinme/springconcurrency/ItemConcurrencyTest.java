package com.chanbinme.springconcurrency;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.chanbinme.springconcurrency.numbersequence.Item;
import com.chanbinme.springconcurrency.numbersequence.ItemRepository;
import com.chanbinme.springconcurrency.numbersequence.ItemService;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ItemConcurrencyTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        // 테스트를 위한 초기 데이터 설정
        Item item = Item.builder()
            .name("Red Potion")
            .stockQuantity(100)
            .build();
        itemRepository.save(item);
    }

    @Test
    public void testPessimisticLocking() throws InterruptedException {
        // given
        int threadCount = 10;   // 동시 실행할 스레드 수
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);   // 스레드 풀 생성
        Set<Integer> quantityHistory = ConcurrentHashMap.newKeySet();   // 재고 수량 기록을 위한 Set(중복 방지)
        int originalStockQuantity = itemService.getItem(1L).getStockQuantity(); // 초기 재고 수량

        CountDownLatch latch = new CountDownLatch(threadCount); // 모든 스레드가 작업을 완료할 때까지 대기하는 CountDownLatch

        // when
        // 10개의 스레드를 생성하여 동시에 재고 감소 작업을 수행
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    int stockQuantity = itemService.decreaseStock(1L, 1);   // 아이템 ID 1의 재고를 1 감소(비관적 락 적용)
                    quantityHistory.add(stockQuantity); // 현재 재고 수량을 기록
                } finally {
                    latch.countDown();  // 작업 완료 시 CountDownLatch 감소
                }
            });
        }

        latch.await();
        executor.shutdown();

        // then
        Item item = itemService.getItem(1L);
        System.out.println("최종 수량: " + item.getStockQuantity());

        assertAll(
            () -> assertEquals(threadCount, quantityHistory.size()),   // 중복 없이 재고 수량 기록
            () -> assertEquals(originalStockQuantity - threadCount, item.getStockQuantity())   // 최종 재고 수량이 90이어야 함
        );
    }
}
