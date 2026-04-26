





package com.pxczxn.blog.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class LoginAttemptService {

    
    private static final int MAX_ATTEMPTS = 5;
    
    private static final long LOCK_TIME_MS = 30 * 60 * 1000; 

    
    private final ConcurrentHashMap<String, AtomicInteger> attemptsCache = new ConcurrentHashMap<>();
    
    private final ConcurrentHashMap<String, Long> lockTimeCache = new ConcurrentHashMap<>();

    




    public boolean isLocked(String identifier) {
        Long lockTime = lockTimeCache.get(identifier);
        if (lockTime == null) {
            return false;
        }

        if (System.currentTimeMillis() - lockTime > LOCK_TIME_MS) {
            
            lockTimeCache.remove(identifier);
            attemptsCache.remove(identifier);
            return false;
        }

        return true;
    }

    
    public void loginFailed(String identifier) {
        AtomicInteger attempts = attemptsCache.computeIfAbsent(identifier, k -> new AtomicInteger(0));
        int newAttempts = attempts.incrementAndGet();

        log.warn("登录失败: identifier={}, attempts={}", identifier, newAttempts);

        if (newAttempts >= MAX_ATTEMPTS) {
            long lockTime = System.currentTimeMillis();
            lockTimeCache.put(identifier, lockTime);
            log.warn("账号已锁定: identifier={}, lockTime={}", identifier, lockTime);
        }
    }

    
    public void loginSucceeded(String identifier) {
        attemptsCache.remove(identifier);
        lockTimeCache.remove(identifier);
        log.info("登录成功，清除失败记录: identifier={}", identifier);
    }

    
    public int getAttempts(String identifier) {
        AtomicInteger attempts = attemptsCache.get(identifier);
        return attempts == null ? 0 : attempts.get();
    }

    
    public long getRemainingLockTime(String identifier) {
        Long lockTime = lockTimeCache.get(identifier);
        if (lockTime == null) {
            return 0;
        }

        long elapsed = System.currentTimeMillis() - lockTime;
        long remaining = LOCK_TIME_MS - elapsed;
        return Math.max(0, remaining);
    }
}