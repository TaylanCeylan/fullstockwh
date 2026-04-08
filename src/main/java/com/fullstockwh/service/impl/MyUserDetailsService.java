package com.fullstockwh.service.impl;

import com.fullstockwh.entity.User;
import com.fullstockwh.repository.IUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService
{
    private final IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
    {
       return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("email not found " + email));
    }
}
