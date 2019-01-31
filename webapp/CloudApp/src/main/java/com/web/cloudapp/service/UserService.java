package com.web.cloudapp.service;

import com.web.cloudapp.Repository.UserRepository;
import com.web.cloudapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
  @Autowired
  UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = findUsername(username);
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

    public User findUsername(String username) {
        Iterable<User> list = userRepository.findAll();
        for (User u : list) {
            if (u.getUserName().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    public boolean checkUserName(String username){
        //String ePattern = "[A-Z0-9._%+-]+@?[A-Z0-9.-]+\\.?[A-Z]{2,4}";
        String ePattern = "^\\w+[\\w-\\.]*\\@\\w+((-\\w+)|(\\w*))\\.[a-z]{2,3}$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(username);
        return m.matches();
    }

    public boolean checkPassword(String password){
        if(password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")){
            return true;
        }
        else
            return false;
    }

    }

