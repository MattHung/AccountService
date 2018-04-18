package com.accountservice.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)//
@ContextConfiguration(locations={"file:WebContent/WEB-INF/appServlet-servlet.xml"})
@WebAppConfiguration
public class CreditTest {
	private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    @Test
    public void testAddCredit() throws Exception {
//    	http://localhost:6080/AccountService/credit/addCredit/?dealer_id=1&user_id=1&amount=9999&api_key=5a5bdeb8e31903595d6f0610e9b57f594f6464b43f95c9ebe19bead028349200
    	mockMvc.perform(get("/credit/addCredit")
    			.param("dealer_id", "1")
    			.param("user_id", "100000010")
    			.param("amount", "9")
    			.param("api_key", "5a5bdeb8e31903595d6f0610e9b57f594f6464b43f95c9ebe19bead028349200")
    			.param("op_trace", "testCase")
    			.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
    			.andExpect(status().isOk());
    }
    
    @Test
    public void testGetCredit() throws Exception {
//    	http://localhost:8080/AccountService/credit/getCredit/?dealer_id=1&user_id=1
    	mockMvc.perform(get("/credit/getCredit")
    			.param("dealer_id", "1")
    			.param("user_id", "1")
    			.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
    			.andExpect(status().isOk());
    }
}
