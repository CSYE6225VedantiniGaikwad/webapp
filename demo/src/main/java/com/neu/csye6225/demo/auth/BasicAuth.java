package com.neu.csye6225.demo.auth;

import com.neu.csye6225.demo.entities.Users;
import com.neu.csye6225.demo.repositories.UserRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BasicAuth implements AuthenticationManager {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRespository userRespository;

    @Override
    public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
        String username = authentication.getPrincipal() + "";
        String password = authentication.getCredentials() + "";
        System.out.println("Authentication Manager: Username = " + username + " Password = " + password);
        Optional<Users> users = userRespository.findUsersByEmailID(username);
        if ( users == null ) {
            throw new BadCredentialsException("1000");
        }
        if ( !passwordEncoder.matches(password, users.get().getPassword())) {
            throw new BadCredentialsException("1000");
        }

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(simpleGrantedAuthority);

        return new UsernamePasswordAuthenticationToken(username, password, list);
    }
}
