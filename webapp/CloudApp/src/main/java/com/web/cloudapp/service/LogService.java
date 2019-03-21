package com.web.cloudapp.service;

import com.web.cloudapp.Controllers.LoginController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Service
public class LogService {

    @Value("${logging.file}")
    private String filePath;


    public Logger logger = Logger.getLogger("MyLog");
    FileHandler fh;

    public LogService(){
        System.out.println("Constr");
    }

    @PostConstruct
    public void init() {
        try {
            System.out.println("Post COntr"+filePath);
            fh = new FileHandler(filePath);
            System.out.println("+"+filePath);
            logger.info("After My first log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            // the following statement is used to log any messages
            logger.info("My first log");
        }
        catch(Exception e){
            logger.info("Exception");
        }
    }


}
