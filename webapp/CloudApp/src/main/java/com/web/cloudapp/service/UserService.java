package com.web.cloudapp.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.web.cloudapp.Exception.BadRequest;
import com.web.cloudapp.Exception.Conflict;
import com.web.cloudapp.Exception.ResourceNotFound;
import com.web.cloudapp.Repository.UserRepository;
import com.web.cloudapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
@Transactional
public class UserService implements UserDetailsService {

    private static int workload=12;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LogService logService;
    
    @Value("${aws.topic.name}")
    private String topicName;

    @Value("${aws.account.id}")
    private String accId;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {

            User user = userRepository.findById(username).get();
            if (user == null) {
                throw new UsernameNotFoundException(username + " was not found");

            }
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            return new org.springframework.security.core.userdetails.User(
                    user.getUserName(),
                    user.getPassword(),
                    authorities
            );
        }catch (Exception ex){
            logService.logger.severe(ex.getMessage());
            throw ex;
        }
    }

    //Password Hashing
    public String hashpwd(String pwd){
        String salt = BCrypt.gensalt(workload);
        String hashpassword = BCrypt.hashpw(pwd,salt);
        return hashpassword;
    }

    //Check for credentials
    public boolean checkCredentials(User user) throws RuntimeException{
        String username = user.getUserName(),password=user.getPassword();
        try {
            //Check for username
            if (username == null || username.equals("")) throw new BadRequest("User name cannot be empty");
            else {
                String ePattern = "^\\w+[\\w-\\.]*\\@\\w+((-\\w+)|(\\w*))(\\.[a-z]{2,3}){1,2}$";
                Pattern p = Pattern.compile(ePattern);
                Matcher m = p.matcher(username);
                if (m.matches()) {
                    //check for password
                    if (password == null || password.equals("")) throw new BadRequest("Password cannot be empty");
                    else {
                        ePattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#_^])[A-Za-z\\d@$!%*?&#_^]{8,}$";
                        p = Pattern.compile(ePattern);
                        m = p.matcher(password);
                        if (m.matches()) {
                            //checking whether the user already exists
                            if (userRepository.existsById(username)) throw new Conflict("User already present");
                            else return true;
                        } else
                            throw new BadRequest("Password must contain atleast 1 Upper case, 1 Lower case, 1 Alphanumeric, 1 digit with minimum of 8 characters");
                    }
                } else throw new BadRequest("Please enter valid email id");
            }
        }catch (Exception ex){
            logService.logger.warning(ex.getMessage());
            throw ex;
        }
    }


    //Getting the current user
    public User getUserName() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                return userRepository.findById(authentication.getName()).get();
            }
            return null;
        }catch (Exception ex){
            logService.logger.warning(ex.getMessage());
            throw ex;
        }
    }

    //Creating new User
    public boolean createUser(User user) {

        try {
            System.out.println("Hiiiii");
            logService.logger.info("I am  logger");
            if (checkCredentials(user)) {
                user.setPassword(hashpwd(user.getPassword()));
                userRepository.save(user);
                logService.logger.info("User created successfully");
                return true;
            }
            return false;
        }catch (Exception ex){
            logService.logger.warning(ex.getMessage());
            throw ex;
        }
    }

    public boolean checkPasswordTest(String password){
        if (password == null || password.equals("")) throw new BadRequest("Password cannot be empty");
        else {

            String ePattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#_^])[A-Za-z\\d@$!%*?&#_^]{8,}$";
            Pattern p = Pattern.compile(ePattern);
            Matcher m = p.matcher(password);
            return m.matches();
        }
    }
    //Password reset username check
    public boolean checkUserName(String username) {
        try {
            if (username == null || username.equals("")) throw new BadRequest("User name cannot be empty");
            else {
                String ePattern = "^\\w+[\\w-\\.]*\\@\\w+((-\\w+)|(\\w*))(\\.[a-z]{2,3}){1,2}$";
                Pattern p = Pattern.compile(ePattern);
                Matcher m = p.matcher(username);
                if (m.matches()) {
                    if (userRepository.existsById(username)) return true;
                    else throw new BadRequest("Not a valid user");
                } else throw new BadRequest("Please enter valid email id");
            }
        } catch (Exception ex) {
            logService.logger.warning(ex.getMessage());
            throw ex;
        }
    }

    public boolean resetpassword(String jsonUsername){
        String username = jsonUsername.split(":")[1].split("\\\"")[1];
        checkUserName(username);
        AmazonSNS snsClient = AmazonSNSClient.builder().defaultClient();
        PublishRequest emailPublishRequest = new PublishRequest("arn:aws:sns:us-east-1:" + accId + ":" + topicName, username);
        PublishResult emailPublishResult = snsClient.publish(emailPublishRequest);
        snsClient.shutdown();
        return true;
    }
}
