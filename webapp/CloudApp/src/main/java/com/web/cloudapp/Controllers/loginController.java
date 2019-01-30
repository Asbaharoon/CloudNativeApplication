package com.web.cloudapp.Controllers;


import com.web.cloudapp.Repository.UserRepository;
import com.web.cloudapp.service.UserService;
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
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public @ResponseBody String sayTime(){
        Map<String, String> map = new HashMap<>();
        Date date = new Date();
        map.put("time",date.toString());
        map.put("messgae","Login Successful");
        return new JSONObject(map).toString();
    }

    @RequestMapping(value= "/user/register", method= RequestMethod.POST)
    public @ResponseBody String register(@RequestBody User user) {
        Map<String,String> map = new HashMap<String, String>();
        boolean userExists = findUser(user.getUserName());
        if(userExists){
            map.put("message","User Already Exists");
            map.put("status",HttpStatus.CONFLICT.toString());
            return new JSONObject(map).toString();
        }
        else if(!userService.checkUserName(user.getUserName())) {
            map.put("message","Please enter a valid email address");
            map.put("status",HttpStatus.NOT_ACCEPTABLE.toString());
            System.out.println("checkUserName");
            return new JSONObject(map).toString();
        }
        else if(!userService.checkPassword(user.getPassword())) {
            map.put("message","Please enter a password with atleast 1 Upper case, 1 Lower case, 1 Alphanumeric, 1 digit with minimum of 8 characters");
            map.put("status",HttpStatus.NOT_ACCEPTABLE.toString());
            System.out.println("checkPassword");
            return new JSONObject(map).toString();
        }
        else {
            String hashpass = hashpwd(user.getPassword());
            userRepository.save(new User(user.getUserName(), hashpass));

            map.put("message", "Registered Successfully");
            map.put("status", HttpStatus.OK.toString());

            return new JSONObject(map).toString();
        }
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
