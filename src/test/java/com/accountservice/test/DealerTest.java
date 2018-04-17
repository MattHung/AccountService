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
public class DealerTest {
	private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    @Test
    public void testAddDealer() throws Exception {
//    	http://localhost:8080/AccountService/dealer/addDealer/?dealer_name=包網名稱
    	mockMvc.perform(get("/dealer/addDealer")
    			.param("dealer_name", "包網名稱")
    			.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
    			.andExpect(status().isOk());
    }
    
    @Test
    public void testDelDealer() throws Exception {
//    	http://localhost:8080/AccountService/dealer/delDealer/?dealer_id=5
    	mockMvc.perform(get("/dealer/delDealer")
    			.param("dealer_id", "5")
    			.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
    			.andExpect(status().isOk());
    }
    
    @Test
    public void testGetDealerByID() throws Exception {
//    	http://localhost:8080/AccountService/dealer/getDealer/?dealer_id=1,2,3
    	mockMvc.perform(get("/dealer/getDealer")
    			.param("dealer_id", "1,2,3")
    			.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
    			.andExpect(status().isOk());
    }
    
    @Test
    public void testGetDealerByName() throws Exception {
//    	http://localhost:8080/AccountService/dealer/getDealer/?dealer_name=包網名稱1,包網名稱2,包網名稱3
    	mockMvc.perform(get("/dealer/getDealer")
    			.param("dealer_name", "包網名稱1,包網名稱2,包網名稱3")
    			.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
    			.andExpect(status().isOk());
    }
}
