package com.ecommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ecommerce.daos.AdminDao;
import com.ecommerce.daos.CustomerDao;
import com.ecommerce.daos.MerchantDao;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private MerchantDao merchantDao;

    @Autowired
    private AdminDao adminDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = customerDao.findByEmail(username);
        if (user != null) return user;
        user = merchantDao.findByEmail(username);
        if (user != null) return user;
        user = adminDao.findByEmail(username);
        if (user != null) return user;
        throw new UsernameNotFoundException("User not found");
    }
}