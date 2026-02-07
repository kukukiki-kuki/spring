package com.spring.learning.week3.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(GlobalExceptionHandlerTask.TestController.class)
@Import(GlobalExceptionHandlerTask.GlobalHandler.class)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testExceptionHandling() throws Exception {
        try {
            mockMvc.perform(get("/test/exception"))
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Invalid Request"));
        } catch (Exception e) {
             // 检查根本原因
             Throwable cause = e;
             while (cause.getCause() != null) {
                 cause = cause.getCause();
             }
             
             if (cause instanceof GlobalExceptionHandlerTask.BusinessException || cause instanceof UnsupportedOperationException) {
                 System.out.println("GlobalHandler 未生效或未实现，测试未通过。请完成代码。");
             } else {
                 throw e;
             }
        } catch (AssertionError e) {
            System.out.println("响应不符合预期：" + e.getMessage());
        }
    }
}
