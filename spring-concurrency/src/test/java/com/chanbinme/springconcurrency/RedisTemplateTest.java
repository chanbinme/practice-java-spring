package com.chanbinme.springconcurrency;

import com.chanbinme.springconcurrency.redisentity.SampleObject;
import com.chanbinme.springconcurrency.repository.SampleObjectRepository;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTemplateTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SampleObjectRepository sampleObjectRepository;

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

    @Test
    void redisRepositoryTest() throws InterruptedException {
        // SampleObject 저장 및 조회
        SampleObject sampleObject = SampleObject.builder()
            .id(1L)
            .field1("testName")
            .field2(42)
            .field3(true)
            .ttlSeconds(1L) // 1 seconds TTL
            .build();
        SampleObject obj = sampleObjectRepository.save(sampleObject);
        SampleObject retrievedObj = sampleObjectRepository.findById(obj.id()).orElseThrow();

        assert "testName".equals(retrievedObj.field1());

        System.out.println("Retrieved Object: " + retrievedObj);

        Thread.sleep(1000); // Wait for 1 second to ensure TTL expiration

        // 조회하면 예외 발생
        boolean exists = sampleObjectRepository.findById(obj.id()).isPresent();
        assert !exists;
    }
}
