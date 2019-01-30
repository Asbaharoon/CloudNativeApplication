package com.web.cloudapp.Controllers;

import com.web.cloudapp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.web.cloudapp.model.User;
import java.util.ArrayList;
import java.util.Date;

@RestController
public class loginController {

    @Autowired
    UserRepository userRepository;

    @RequestMapping("/")
    public Date sayTime(){
        Date date = new Date();
        return date;
    }

    @RequestMapping(value= "/user/register", method= RequestMethod.POST)
    public void register(@RequestBody User user) {
        userRepository.save(user);
    }
}
