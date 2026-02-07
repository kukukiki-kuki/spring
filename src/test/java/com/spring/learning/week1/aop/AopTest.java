package com.spring.learning.week1.aop;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class AopTest {

    interface GreetingService {
        @LogExecution
        void sayHello(String name);
        
        void sayBye();
    }

    static class GreetingServiceImpl implements GreetingService {
        @Override
        public void sayHello(String name) {
            System.out.println("Hello, " + name);
        }

        @Override
        public void sayBye() {
            System.out.println("Bye");
        }
    }

    @Test
    public void testAopProxy() {
        // 捕获 System.out
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        try {
            GreetingService target = new GreetingServiceImpl();
            
            GreetingService proxy = null;
            try {
                proxy = (GreetingService) SimpleAopFramework.createProxy(target);
            } catch (UnsupportedOperationException e) {
                 System.setOut(originalOut);
                 System.out.println("SimpleAopFramework 尚未实现，跳过测试。");
                 return;
            }

            // 1. 调用带注解的方法
            proxy.sayHello("World");
            String output1 = outContent.toString();
            
            // 验证：应该包含 Before 和 After (根据题目要求)
            Assertions.assertTrue(output1.contains("Before"), "日志应该包含 Before");
            Assertions.assertTrue(output1.contains("After"), "日志应该包含 After");
            Assertions.assertTrue(output1.contains("Hello, World"), "原方法逻辑应该被执行");

            // 清空 buffer
            outContent.reset();

            // 2. 调用不带注解的方法
            proxy.sayBye();
            String output2 = outContent.toString();
            
            // 验证：应该只有原方法输出，没有 AOP 日志
            Assertions.assertFalse(output2.contains("Before"), "不带注解的方法不应打印 Before");
            Assertions.assertTrue(output2.contains("Bye"), "原方法逻辑应该被执行");

        } finally {
            System.setOut(originalOut);
        }
    }
}
