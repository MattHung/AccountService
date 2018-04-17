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
    
    @Test
    public void testUserLogin() throws Exception {
//    	http://localhost:8080/AccountService/account/user_login/?dealer_id=1&user_name=test01&user_password=password1
    	mockMvc.perform(get("/account/user_login")
    			.param("dealer_id", "1")
    			.param("user_name", "test01")
    			.param("user_password", "password1")
    			.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
    			.andExpect(status().isOk()); 
    }
    
    @Test
    public void testUserLogout() throws Exception {
//    	http://localhost:8080/AccountService/account/user_logout?dealer_id=1&user_name=test01
    	mockMvc.perform(get("/account/user_logout")
    			.param("dealer_id", "1")
    			.param("user_name", "test01")
    			.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
    			.andExpect(status().isOk()); 
    }
    
    @Test
    public void testCheckUserLoggedIn() throws Exception {
//    	http://localhost:8080/AccountService/account/checkUserLoggedIn/?users=[{%22dealer_id%22:1,%22user_name%22:%22test01%22,%22session_id%22:%22f91db717f92240daefd9bfa090f0a886b1a52402d05ebce04eff4abfdcef7b6b%22}]
    	mockMvc.perform(get("/account/checkUserLoggedIn")
    			.param("users", "[{\"dealer_id\":1,\"user_name\":\"test01\",\"session_id\":\"f91db717f92240daefd9bfa090f0a886b1a52402d05ebce04eff4abfdcef7b6b\"}]")
    			.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
    			.andExpect(status().isOk()); 
    }
}
