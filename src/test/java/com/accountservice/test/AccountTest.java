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
public class AccountTest {	
	private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    @Test
    public void testAddAccount() throws Exception {
//    	http://localhost:8080/AccountService/account/addAccount/?dealer_id=1&user_name=test01&user_password=password1&currency=CNY
    	mockMvc.perform(get("/account/addAccount")
    			.param("dealer_id", "1")
    			.param("user_name", "test01")
    			.param("user_password", "password1")
    			.param("currency", "CNY")
    			.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
    			.andExpect(status().isOk()); 
    }
	 
    @Test
    public void testGetAccount() throws Exception {
//    	http://localhost:8080/AccountService/account/getAccount/?dealer_id=1&user_id=1,2,3
    	mockMvc.perform(get("/account/getAccount")
    			.param("dealer_id", "1")
    			.param("user_id", "1,2,3")
    			.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
    			.andExpect(status().isOk()); 
    }
}
