package com.chanbinme.springconcurrency;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTemplateTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void testRedisTemplate() {
        // String 값 저장 및 조회
        redisTemplate.opsForValue().set("key", "value");
        String value = (String) redisTemplate.opsForValue().get("key");

        assert "value".equals(value);

        System.out.println("Value for 'key': " + value);

        // Hash 값 저장 및 조회
        Map<String, Integer> map = Map.of("a", 1, "b", 2);
        redisTemplate.opsForHash().putAll("myHash", map);
        Map<Object, Object> retrievedMap = redisTemplate.opsForHash().entries("myHash");

        assert retrievedMap.size() == 2;
        assert retrievedMap.get("a").equals(1);
        assert retrievedMap.get("b").equals(2);

        System.out.println("Retrieved Map: " + retrievedMap);
    }
}
