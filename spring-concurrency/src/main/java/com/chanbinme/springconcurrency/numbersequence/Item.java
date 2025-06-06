package com.chanbinme.springconcurrency.numbersequence;

import static jakarta.persistence.GenerationType.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Comment("아이템 이름")
    private String name;

    @Comment("아이템 재고 수량")
    private int stockQuantity;

    @Version
    private Long version;

    public void decreaseStock(int quantity) {
        if (this.stockQuantity < quantity) {
            throw new RuntimeException("재고가 부족합니다. 현재 재고: " + this.stockQuantity + ", 요청 수량: " + quantity);
        }
        this.stockQuantity -= quantity;
    }
}
