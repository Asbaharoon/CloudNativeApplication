package com.web.cloudapp;

import com.web.cloudapp.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudappApplicationTests {

    @Test
    public void checkCredentials() {

        CloudappApplicationTests cloudTests = new CloudappApplicationTests();
        UserService userService = new UserService();
        assertTrue(userService.checkPassword("Northeastern@Cloud67"));
        assertTrue(userService.checkPassword("$Cloudcomputing2019"));
        assertFalse(userService.checkPassword("Cloudcomputing2019"));
        assertTrue(userService.checkUserName("cloudapp@gmail.com"));

    }

}

