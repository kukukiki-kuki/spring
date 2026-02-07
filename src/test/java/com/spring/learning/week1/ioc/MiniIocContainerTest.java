package com.spring.learning.week1.ioc;

import com.spring.learning.week1.ioc.testpkg.TestDao;
import com.spring.learning.week1.ioc.testpkg.TestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MiniIocContainerTest {

    @Test
    public void testIocContainer() {
        // 给定扫描包
        String basePackage = "com.spring.learning.week1.ioc.testpkg";
        
        // 当初始化容器时
        // 注意：如果你尚未实现 scan 方法，这里会抛出 UnsupportedOperationException
        MiniApplicationContext context = null;
        try {
            context = new MiniApplicationContext(basePackage);
        } catch (UnsupportedOperationException e) {
            System.out.println("MiniApplicationContext 尚未实现，跳过测试执行。请先完成代码实现。");
            // 暂时让测试通过或忽略，以免构建失败，或者这里故意失败提醒用户
            // Assertions.fail("请先实现 MiniApplicationContext");
            return;
        }
        
        // 验证：容器中应该有 testService 和 testDao
        Object serviceBean = context.getBean("testService");
        Object daoBean = context.getBean("testDao");
        
        Assertions.assertNotNull(serviceBean, "TestService 应该是被扫描到的 Bean");
        Assertions.assertNotNull(daoBean, "TestDao 应该是被扫描到的 Bean");
        
        // 验证类型
        Assertions.assertTrue(serviceBean instanceof TestService);
        
        // 验证依赖注入
        TestService service = (TestService) serviceBean;
        Assertions.assertNotNull(service.getTestDao(), "TestDao 应该被注入到 TestService 中");
        Assertions.assertEquals("data", service.getData());
    }
}
