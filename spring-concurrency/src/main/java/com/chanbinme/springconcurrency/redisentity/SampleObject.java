package com.chanbinme.springconcurrency.redisentity;

import jakarta.persistence.Id;
import lombok.Builder;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Builder
@RedisHash("SampleObject")
public record SampleObject(
    @Id Long id,
    String field1,
    int field2,
    boolean field3,
    @TimeToLive Long ttlSeconds
) {
}
