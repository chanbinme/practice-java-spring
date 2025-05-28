package com.chanbinme.springconcurrency.numbersequence;


import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * 아이템을 ID로 조회합니다. 쓰기 락을 사용하여 동시성 문제를 방지합니다.
     * 쓰기 락을 사용하면 해당 아이템을 조회하는 동안 다른 트랜잭션이 해당 아이템을 수정할 수 없습니다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Item> findById(Long id);
}
