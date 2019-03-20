package com.web.cloudapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Service
@PropertySource("classpath:application.properties")
public class LogService {

    @Value("${log.file}")
    private String filePath;

    public Logger logger = Logger.getLogger("MyLog");
    FileHandler fh;

    public LogService() {
        try {
            fh = new FileHandler(filePath);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            // the following statement is used to log any messages
            logger.info("My first log");
        }
        catch(Exception e){
            logger.info(e.getMessage());
        }
    }
}
