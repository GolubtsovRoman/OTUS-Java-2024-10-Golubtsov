package ru.otus.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cache.listener.HwListener;

import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("MyCache должен")
class MyCacheTest {
    private static final Logger logger = LoggerFactory.getLogger(MyCacheTest.class);

    private final HwCache<String, BigObject> testCache = new MyCache<>();


    @BeforeEach
    void setUp() {
        // только чтобы видеть логи
        testCache.addListener(new HwListener<String, BigObject>() {
            @Override
            public void notify(String key, BigObject value, String action) {
                logger.info("key:{}, value:{}, action: {}", key, value, action);
            }
        });
    }

    @DisplayName("сбрасываться при недостатке памяти")
    @Test
    void cleanCacheIfRamIsFull() {
        int cacheSize = 1100;
        IntStream.range(1, cacheSize).forEach(idx -> testCache.put(String.valueOf(idx), new BigObject()));

        int countAlive = 0;
        for (int idx = 1; idx < cacheSize; idx++) {
            if (testCache.get(String.valueOf(idx)) != null) {
                ++countAlive;
            }
        }

        assertThat(countAlive).isLessThan(cacheSize);
    }



    static class BigObject {
        final byte[] array = new byte[1024 * 1024];

        @Override
        public String toString() {
            return "BigObject{arraySize=" + array.length + '}';
        }
    }

}

