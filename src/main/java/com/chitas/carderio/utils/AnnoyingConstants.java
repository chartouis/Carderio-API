package com.chitas.carderio.utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import com.chitas.carderio.model.User;
import com.chitas.carderio.repo.UsersRepo;

@Configuration
public class AnnoyingConstants {
    private final UsersRepo usersRepo;

    public AnnoyingConstants(UsersRepo usersRepo){
        this.usersRepo = usersRepo;

    }

    public User getCurrentUser(){
        return usersRepo.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public String getCurrentUsername(){
        return getCurrentUser().getUsername();
    }

}
