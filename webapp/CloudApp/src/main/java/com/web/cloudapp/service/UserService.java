package com.web.cloudapp.service;


import com.web.cloudapp.Exception.BadRequest;
import com.web.cloudapp.Exception.Conflict;
import com.web.cloudapp.Repository.UserRepository;
import com.web.cloudapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService implements UserDetailsService {

    private static int workload=12;
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(username).get();
        if (user == null){
            throw new UsernameNotFoundException(username + " was not found");
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                authorities
        );
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

        //Check for username
        if(username==null||username.equals("")) throw new BadRequest("User name cannot be empty");
        else {
            String ePattern = "^\\w+[\\w-\\.]*\\@\\w+((-\\w+)|(\\w*))\\.[a-z]{2,3}$";
            Pattern p = Pattern.compile(ePattern);
            Matcher m = p.matcher(username);
            if (m.matches()){
                //check for password
                if (password == null || password.equals("")) throw new BadRequest("Password cannot be empty");
                else {
                    ePattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#_^])[A-Za-z\\d@$!%*?&#_^]{8,}$";
                    p = Pattern.compile(ePattern);
                    m = p.matcher(password);
                    if (m.matches()){
                        //checking whether the user already exists
                        if(userRepository.existsById(username)) throw new Conflict("User already present");
                        else return true;
                    }
                    else throw new BadRequest("Password must contain atleast 1 Upper case, 1 Lower case, 1 Alphanumeric, 1 digit with minimum of 8 characters");
                }
            }
            else throw new BadRequest("Please enter valid email id");
        }
    }

    //Getting the current user
    public User getUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return userRepository.findById(authentication.getName()).get();
        }
        return null;}

    //Creating new User
    public boolean createUser(User user){
        if(checkCredentials(user)){
            user.setPassword(hashpwd(user.getPassword()));
            userRepository.save(user);
            return true;
        }
        return false;
    }

}