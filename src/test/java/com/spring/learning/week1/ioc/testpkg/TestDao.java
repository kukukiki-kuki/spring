package com.spring.learning.week1.ioc.testpkg;

import com.spring.learning.week1.ioc.MyComponent;

@MyComponent
public class TestDao {
    public String select() {
        return "data";
    }
}
