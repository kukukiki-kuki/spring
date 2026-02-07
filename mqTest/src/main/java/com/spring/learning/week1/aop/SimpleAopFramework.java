package com.spring.learning.week1.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 考题 2: 手写 AOP (基于 JDK 动态代理)
 * 
 * 目标：
 * 实现 createProxy 方法，为目标对象创建一个代理对象。
 * 当调用代理对象的方法时，如果该方法上标记了 @LogExecution 注解，
 * 则在方法执行前打印 "Before..."，执行后打印 "After..."，并记录耗时。
 * 
 * 提示：
 * - 使用 Proxy.newProxyInstance 创建代理
 * - 实现 InvocationHandler 接口处理方法调用
 * - 使用 method.isAnnotationPresent(LogExecution.class) 判断是否需要增强
 */
@Slf4j
public class SimpleAopFramework {

    /**
     * 为目标对象创建代理
     * 
     * @param target 目标对象（必须实现接口）
     * @return 代理对象
     */
    public static Object createProxy(Object target) {
        // 1. 获取 target 的类加载器和接口
        ClassLoader classLoader = target.getClass().getClassLoader();
        Class<?>[] interfaces = target.getClass().getInterfaces();
        // 2. 创建 InvocationHandler
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object result = null;
                if (method.isAnnotationPresent(LogExecution.class)){
                    System.out.println(" Before 方法执行前");
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    result =  method.invoke(target,args);
                    stopWatch.stop();
                    long totalTimeMillis = stopWatch.getTotalTimeMillis();
                    log.info("方法执行时间为:{}",totalTimeMillis);
                    System.out.println("方法执行时间为："+totalTimeMillis);
                    System.out.println("方法执行后 After ");
                }else {
                    result =  method.invoke(target,args);
                }
                return result;
            }
        };
        // 3. 返回 Proxy.newProxyInstance(...)
        return Proxy.newProxyInstance(classLoader, interfaces, handler);
    }
    // 可以在这里定义内部类实现 InvocationHandler
}
