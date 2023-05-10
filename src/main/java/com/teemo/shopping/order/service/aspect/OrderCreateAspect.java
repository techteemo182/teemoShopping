package com.teemo.shopping.order.service.aspect;

import com.teemo.shopping.account.dto.request.GamePaymentInformation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
class OrderCreateAspect {

    static private ConcurrentHashMap<LockCondition, ReentrantLock> mutexMap = new ConcurrentHashMap<>();
    @Around("@annotation(CreateOrderTransaction)") // todo: 어노테이션으로 변경
    public Object Around(ProceedingJoinPoint pointcut) throws Throwable {
        List<ReentrantLock> locks = new ArrayList<>();
        Long accountId = (Long) pointcut.getArgs()[0];
        List<Long> gameIds = ((List<GamePaymentInformation>) pointcut.getArgs()[2]).stream()
            .map(gamePaymentInformation -> gamePaymentInformation.getGameId()).toList();
        for (var gameId : gameIds) {
            LockCondition condition = new LockCondition(accountId, gameId);
            ReentrantLock lock;
            synchronized (mutexMap) {
                lock = mutexMap.get(new LockCondition(accountId, gameId));
                if (lock == null) {
                    lock = new ReentrantLock();
                }
                mutexMap.put(condition, lock);
            }
            if (!lock.tryLock()) {
                for (var beforeLock : locks) {
                    beforeLock.unlock();
                }
                locks.clear();
                break;
            } else {
                locks.add(lock);
            }
        }
        Object result;
        try {
            if (locks.size() == gameIds.size()) {
                result = pointcut.proceed();
            } else {
                throw new IllegalStateException("트랜잭션 충돌");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            for (var lock : locks) {
                lock.unlock();
            }
        }
        return result;
    }

    @Getter
    @AllArgsConstructor
    static class LockCondition {

        final private Long accountId;
        final private Long gameId;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof LockCondition) {
                LockCondition condition = (LockCondition) obj;
                return accountId.equals(condition.accountId) && gameId.equals(condition.gameId);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (int) (863743 * accountId + 1209109 * gameId);
        }
    }
}
