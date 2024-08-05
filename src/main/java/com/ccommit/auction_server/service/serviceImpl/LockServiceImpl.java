package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.service.LockService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LockServiceImpl implements LockService {

    private final RedissonClient redissonClient;

    @Override
    public void lockProduct(Long productId) {
        RLock lock = redissonClient.getLock("ProductIdLock:" + productId);
        lock.lock(10, TimeUnit.MINUTES);
    }

    @Override
    public boolean unlockProduct(Long productId) {
        RLock lock = redissonClient.getLock("ProductIdLock:" + productId);
        if(!lock.isLocked()){
            return true;    //락이 이미 해제되었음을 반환
        }
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
            return true;    //락이 정상적으로 해제 되었음을 반환
        }
        return false;
    }

    @Override
    public boolean isProductLocked(Long productId) {
        RLock lock = redissonClient.getLock("ProductIdLock:" + productId);
        return lock.isLocked();
    }
}
