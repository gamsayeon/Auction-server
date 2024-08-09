package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.exception.ConnectionException;
import com.ccommit.auction_server.service.LockService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisLockServiceImpl implements LockService {
    private final RedisTemplate<String, String> redisTemplate;
    private final Logger logger = LogManager.getLogger(RedisLockServiceImpl.class);

    private static final String LOCK_KEY_PREFIX = "PRODUCT_ID_LOCK:";
    @Override
    public void lockProduct(Long productId) {
        String lockKey = LOCK_KEY_PREFIX + productId;
        redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", 10, TimeUnit.MINUTES);
    }

    @Override
    public boolean unlockProduct(Long productId) {
        try {
            String lockKey = LOCK_KEY_PREFIX + productId;
            Boolean isDeleted = redisTemplate.delete(lockKey);
            if(isDeleted != null) {
                return true;    //락이 성공적으로 해제되었음을 반환(락이 없을 경우에도 포함[그전에 해제 됨을 의미])
            }
        } catch (Exception e) {
            // Redis 연결 문제나 기타 예외 발생
            logger.error("Failed to delete lock key: " + e.getMessage());
            throw new ConnectionException("REDIS_CONNECTION_ERROR");
        }
        return false;
    }

    @Override
    public boolean isProductLocked(Long productId) {
        String lockKey = LOCK_KEY_PREFIX + productId;
        Boolean result = redisTemplate.hasKey(lockKey);
        return result != null && result;
    }
}
