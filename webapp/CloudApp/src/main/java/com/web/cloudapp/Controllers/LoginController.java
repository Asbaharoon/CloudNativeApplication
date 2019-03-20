package com.web.cloudapp.Controllers;

import com.timgroup.statsd.StatsDClient;
import com.web.cloudapp.model.User;
import com.web.cloudapp.service.LogService;
import com.web.cloudapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private StatsDClient statsDClient;


     private LogService logService = new LogService();

    Map<String,String> out = new HashMap<>();
    ResponseEntity rs = new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);

    //User Registration
    @PostMapping("/user/register")
    public @ResponseBody
    ResponseEntity register(@RequestBody User user){
        if(userService.createUser(user)) {
            out.put("message","User Created Successfully");
            rs = new ResponseEntity(out,HttpStatus.CREATED);
            statsDClient.incrementCounter("user.post");
            logService.logger.info("Request completed successfully with status : "+ HttpStatus.CREATED.toString());
        }
        return rs;
    }

    //User authentication
    @GetMapping("/")
    public @ResponseBody
    ResponseEntity getTime(){
        out.clear();
        Date date = new Date();
        out.put("timestamp",date.toString());
        statsDClient.incrementCounter("user.get");
        logService.logger.info("Request completed successfully with status : "+ HttpStatus.OK.toString());
        return new ResponseEntity(out,HttpStatus.OK);
    }
}
