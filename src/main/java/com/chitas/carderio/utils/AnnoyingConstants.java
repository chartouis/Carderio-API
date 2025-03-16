package com.chitas.carderio.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.chitas.carderio.model.User;
import com.chitas.carderio.repo.UsersRepo;

@Component
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
