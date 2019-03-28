package com.web.cloudapp.Controllers;
import com.timgroup.statsd.StatsDClient;
import com.web.cloudapp.model.User;
import com.web.cloudapp.service.LogService;
import com.web.cloudapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private StatsDClient statsDClient;

    @Autowired
     private LogService logService;

    Map<String,String> out = new HashMap<>();
    ResponseEntity rs = new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);

    //User Registration
    @PostMapping("/user/register")
    public @ResponseBody
    ResponseEntity register(@RequestBody User user){
        out.clear();
        statsDClient.incrementCounter("user.post");
        if(userService.createUser(user)) {
            out.put("message","User Created Successfully");
            rs = new ResponseEntity(out,HttpStatus.CREATED);
            logService.logger.info("Request completed successfully with status : "+ HttpStatus.CREATED.toString());
        }
        return rs;
    }

    //User authentication
    @GetMapping("/")
    public @ResponseBody
    ResponseEntity getTime(){
        out.clear();
        statsDClient.incrementCounter("user.get");
        Date date = new Date();
        out.put("timestamp",date.toString());
        logService.logger.info("Request completed successfully with status : "+ HttpStatus.OK.toString());
        return new ResponseEntity(out,HttpStatus.OK);
    }

    //Password Reset
    @PostMapping("/reset")
    public @ResponseBody
    ResponseEntity resetPassword(@RequestBody String jsonEmail){
        out.clear();
        statsDClient.increment("password.reset.post");
        if(userService.resetpassword(jsonEmail)) {
            out.put("message: ", "Request raised successfully please check your email");
            logService.logger.info("SNS request created successfully with status : "+ HttpStatus.CREATED.toString());
            rs = new ResponseEntity(out,HttpStatus.CREATED);
        }
    return rs;}

}
