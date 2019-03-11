package com.web.cloudapp;

import com.web.cloudapp.model.User;
import com.web.cloudapp.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudappApplicationTests {

    @Autowired
    UserService userService;

    @Test
    public void checkCredentials() {

        CloudappApplicationTests cloudTests = new CloudappApplicationTests();
        User user = new User();
        user.setPassword("Northeastern@Cloud67");
        user.setUserName("cloudapp@gmail.com");
        assertTrue(userService.checkCredentialsTest(user));

    }
}

