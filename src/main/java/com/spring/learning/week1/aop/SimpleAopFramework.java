package com.spring.learning.week1.aop;

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
public class SimpleAopFramework {

    /**
     * 为目标对象创建代理
     * @param target 目标对象（必须实现接口）
     * @return 代理对象
     */
    public static Object createProxy(Object target) {
        // TODO: 请在此处实现代码
        // 1. 获取 target 的类加载器和接口
        // 2. 创建 InvocationHandler
        // 3. 返回 Proxy.newProxyInstance(...)
        
        throw new UnsupportedOperationException("请实现 createProxy 方法");
    }
    
    // 可以在这里定义内部类实现 InvocationHandler
}
