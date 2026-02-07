package com.spring.learning.week4.transaction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
public class TransferServiceTest {

    @Autowired
    private TransferServiceTask.TransferService transferService;

    @Autowired
    private TransferServiceTask.AccountRepository accountRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    public void testTransferRollback() {
        // 1. 准备数据 (在事务外执行，确保提交)
        transactionTemplate.execute(status -> {
            accountRepository.deleteAll();
            accountRepository.save(new TransferServiceTask.Account(1L, 2000.0));
            accountRepository.save(new TransferServiceTask.Account(2L, 0.0));
            return null;
        });

        // 2. 执行转账 1500 (触发异常)
        try {
            transferService.transfer(1L, 2L, 1500.0);
        } catch (RuntimeException e) {
            System.out.println("捕获预期异常: " + e.getMessage());
        }

        // 3. 验证回滚
        // 如果 @Transactional 生效，异常抛出后，数据库状态应回滚到转账前
        TransferServiceTask.Account sourceAfter = accountRepository.findById(1L).orElseThrow();
        TransferServiceTask.Account targetAfter = accountRepository.findById(2L).orElseThrow();

        // 检查
        if (sourceAfter.getBalance() != 2000.0) {
            Assertions.fail("事务回滚失败：源账户已被扣款，说明事务未生效或未回滚。当前余额: " + sourceAfter.getBalance());
        }
        
        Assertions.assertEquals(0.0, targetAfter.getBalance(), "目标账户不应收到款项");
    }
}
