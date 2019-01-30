package com.web.cloudapp.Controllers;


import com.web.cloudapp.Repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.web.cloudapp.model.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCrypt;

@RestController
public class loginController {

    private static int workload = 18;

    @Autowired
    UserRepository userRepository;

    @RequestMapping("/")
    public Date sayTime(){
        Date date = new Date();
        return date;
    }

    @RequestMapping(value= "/user/register", method= RequestMethod.POST)
    public @ResponseBody String register(@RequestBody User user) {
        Map<String,String> map = new HashMap<String, String>();
        String hashpass = hashpwd(user.getPassword());
        userRepository.save(new User(user.getUserName(),hashpass));

        map.put("message","Login Successful");
        map.put("status",HttpStatus.OK.toString());

        return new JSONObject(map).toString();

    }


    public String hashpwd(String pwd){
        String salt = BCrypt.gensalt(workload);
        String hashpassword = BCrypt.hashpw(pwd,salt);
        return hashpassword;
    }

    public boolean findUser(String username){
        Iterable<User> userIterable = userRepository.findAll();
        for(User u: userIterable){
            if(u.getUserName().equalsIgnoreCase(username)){
                return true;
            }
        }
    return false;}

}
