package com.spring.learning.week1.ioc;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.net.URL;
import java.lang.reflect.Field;

/**
 * 考题 1: 实现一个迷你 IoC 容器
 * 
 * 目标：
 * 1. 扫描指定包下的所有类
 * 2. 识别带有 @MyComponent 注解的类，实例化并放入 beanMap
 * 3. 处理 @MyAutowired 注解，实现依赖注入
 * 
 * 提示：
 * - 使用 ClassLoader 扫描 classpath 下的资源
 * - 使用反射创建实例 (Class.newInstance 或 Constructor.newInstance)
 * - 使用反射设置字段值 (Field.setAccessible(true); Field.set(target, value))
 */
public class MiniApplicationContext {

    // 存放 Bean 的容器，Key 为 beanName (首字母小写类名)，Value 为 Bean 实例
    private final Map<String, Object> beanMap = new ConcurrentHashMap<>();

    public MiniApplicationContext(String basePackage) {
        scan(basePackage);
        // 建议：可以在 scan 完成后，再统一执行依赖注入
        injectDependencies();
    }

    /**
     * TODO: 扫描包并实例化 Bean
     * 1. 根据包名获取类路径 URL
     * 2. 遍历文件，找到 .class 文件
     * 3. 加载类，判断是否有 @MyComponent 注解
     * 4. 如果有，实例化并放入 beanMap (注意 BeanName 生成规则：类名首字母小写)
     */
    private void scan(String basePackage) {
        // 请在此处编写代码
        // 1. 根据包名获取类路径 URL
        String path = basePackage.replace(".", "/");
        URL url = this.getClass().getClassLoader().getResource(path);
        if (url == null) {
            System.out.println("未找到包: " + basePackage);
            return;
        }
        File fileOrigin = new File(url.getFile());
        File[] files = fileOrigin.listFiles();
        if (files == null) {
            return;
        }
        // 2. 遍历文件，找到 .class 文件
        for (File file : files) {
            if (file.isDirectory()) {
                scan(basePackage + "." + file.getName());
            } else {
                String className = file.getName().replace(".class", "");
                Class<?> clazz = null;
                try {
                    clazz = Class.forName(basePackage + "." + className);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                // 3. 加载类，判断是否有 @MyComponent 注解
                if (clazz.isAnnotationPresent(MyComponent.class)) {

                    // 4. 如果有，实例化并放入 beanMap (注意 BeanName 生成规则：类名首字母小写)
                    // 修正 deprecated newInstance 用法
                    try {
                        String beanName = className.substring(0, 1).toLowerCase() + className.substring(1);
                        beanMap.put(beanName, clazz.getDeclaredConstructor().newInstance());
                    } catch (InstantiationException | IllegalAccessException
                            | java.lang.reflect.InvocationTargetException | NoSuchMethodException e) {
                        throw new RuntimeException("Failed to instantiate bean: " + className, e);
                    }
                }
            }
        }

    }

    /**
     * TODO: 依赖注入
     * 遍历 beanMap 中的所有 Bean，检查其字段是否有 @MyAutowired 注解
     * 如果有，从 beanMap 中找到对应的 Bean 进行注入
     */
    private void injectDependencies() {
        // 请在此处编写代码
        for (Object beanInstance : beanMap.values()) {
            Class<?> clazz = beanInstance.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(MyAutowired.class)) {
                    String beanName = field.getName();
                    Object dependency = beanMap.get(beanName);
                    if (dependency == null) {
                        throw new RuntimeException("Bean not found: " + beanName);
                    }
                    field.setAccessible(true);
                    try {
                        field.set(beanInstance, dependency);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    /**
     * 获取 Bean
     */
    public Object getBean(String beanName) {
        return beanMap.get(beanName);
    }

    /**
    
     */
    public <T> T getBean(Class<T> clazz) {
        for (Object bean : beanMap.values()) {
            if (clazz.isAssignableFrom(bean.getClass())) {
                return clazz.cast(bean);
            }
        }
        return null;
    }
}
