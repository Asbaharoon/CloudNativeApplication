package com.web.cloudapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Service
public class LogService {

    @Value("${logging.file}")
    public String filePath;

    public Logger logger = Logger.getLogger("MyLog");
    FileHandler fh;

    public LogService() {
        try {
            System.out.println("Hiiiii");
            logger.info("My first log");
            fh = new FileHandler("/Users/vinyaskaushiktr/Sem4/cloud3/logs/csye6225.log");
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
