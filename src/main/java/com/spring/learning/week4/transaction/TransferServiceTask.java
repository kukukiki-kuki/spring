package com.spring.learning.week4.transaction;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 考题 5: 事务管理
 * 
 * 目标：
 * 实现转账功能：从 source 账户扣款，给 target 账户加款。
 * 要求整个过程是原子性的，如果中间发生异常，必须回滚。
 * 
 * 提示：
 * - 使用 @Transactional
 * - 处理余额不足的情况
 */
public class TransferServiceTask {

    @Entity
    public static class Account {
        @Id
        private Long id;
        private Double balance;
        
        public Account() {}
        public Account(Long id, Double balance) { this.id = id; this.balance = balance; }
        public Long getId() { return id; }
        public Double getBalance() { return balance; }
        public void setBalance(Double balance) { this.balance = balance; }
    }

    public interface AccountRepository extends JpaRepository<Account, Long> {
    }

    @Service
    public static class TransferService {
        
        private final AccountRepository accountRepository;

        public TransferService(AccountRepository accountRepository) {
            this.accountRepository = accountRepository;
        }

        // TODO: 请添加 @Transactional 注解，并确保异常时回滚
        public void transfer(Long sourceId, Long targetId, Double amount) {
            Account source = accountRepository.findById(sourceId).orElseThrow(() -> new RuntimeException("用户不存在"));
            Account target = accountRepository.findById(targetId).orElseThrow(() -> new RuntimeException("用户不存在"));
            
            if (source.getBalance() < amount) {
                throw new RuntimeException("余额不足");
            }
            
            // 1. 扣款
            source.setBalance(source.getBalance() - amount);
            accountRepository.save(source);
            
            // 模拟异常点 (当转账金额 > 1000 时抛出异常)
            if (amount > 1000) {
                throw new RuntimeException("转账金额过大，触发风控拦截");
            }
            
            // 2. 加款
            target.setBalance(target.getBalance() + amount);
            accountRepository.save(target);
        }
    }
}
