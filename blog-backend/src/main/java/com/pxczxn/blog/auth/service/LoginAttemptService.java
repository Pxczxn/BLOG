/**
 * 登录尝试服务
 * <p>
 * 记录登录失败的次数，防止暴力破解。
 * 达到最大尝试次数后锁定账号一段时间（默认30分钟）。
 */
package com.pxczxn.blog.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class LoginAttemptService {

    /** 最大登录失败次数，超过后锁定账号 */
    private static final int MAX_ATTEMPTS = 5;
    /** 锁定时间（毫秒），默认 30 分钟 */
    private static final long LOCK_TIME_MS = 30 * 60 * 1000; // 30分钟

    /** 登录失败次数缓存：identifier -> 失败次数 */
    private final ConcurrentHashMap<String, AtomicInteger> attemptsCache = new ConcurrentHashMap<>();
    /** 账号锁定时间缓存：identifier -> 锁定起始时间 */
    private final ConcurrentHashMap<String, Long> lockTimeCache = new ConcurrentHashMap<>();

    /**
     * 检查账号是否被锁定
     * <p>
     * 若锁定时间已过，自动清除锁定状态。
     */
    public boolean isLocked(String identifier) {
        Long lockTime = lockTimeCache.get(identifier);
        if (lockTime == null) {
            return false;
        }

        if (System.currentTimeMillis() - lockTime > LOCK_TIME_MS) {
            // 锁定时间已过，清除失败记录。
            lockTimeCache.remove(identifier);
            attemptsCache.remove(identifier);
            return false;
        }

        return true;
    }

    /** 记录一次登录失败，达到上限时锁定账号 */
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

    /** 登录成功，清除失败记录和锁定状态 */
    public void loginSucceeded(String identifier) {
        attemptsCache.remove(identifier);
        lockTimeCache.remove(identifier);
        log.info("登录成功，清除失败记录: identifier={}", identifier);
    }

    /** 获取当前失败次数 */
    public int getAttempts(String identifier) {
        AtomicInteger attempts = attemptsCache.get(identifier);
        return attempts == null ? 0 : attempts.get();
    }

    /** 获取剩余锁定时间（毫秒），未锁定返回 0 */
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