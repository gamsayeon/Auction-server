package com.ccommit.auction_server.service;

public interface LockService {
    void lockProduct(Long productId);

    boolean unlockProduct(Long productId);

    boolean isProductLocked(Long productId);
}
