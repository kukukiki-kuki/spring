package com.spring.learning.week1.ioc.testpkg;

import com.spring.learning.week1.ioc.MyAutowired;
import com.spring.learning.week1.ioc.MyComponent;

@MyComponent
public class TestService {
    
    @MyAutowired
    private TestDao testDao;

    public String getData() {
        if (testDao == null) {
            return "dao is null";
        }
        return testDao.select();
    }
    
    public TestDao getTestDao() {
        return testDao;
    }
}
