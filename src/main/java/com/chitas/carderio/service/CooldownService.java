package com.chitas.carderio.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.chitas.carderio.model.CoolDown;
import com.chitas.carderio.model.User;
import com.chitas.carderio.repo.AICooldownRepo;

@Service
public class CooldownService {
    private final AICooldownRepo aiCooldownRepo;

    public CooldownService(AICooldownRepo aiCooldownRepo) {
        this.aiCooldownRepo = aiCooldownRepo;
    }

    private final int COOLDONW_IN_SECONDS = 60;

    public boolean isAIDue(User user) {
        CoolDown cd = aiCooldownRepo.findByUserId(user.getId());
        if(cd == null){
            CoolDown cd1 = new CoolDown();
            cd1.setUser(user);
            cd1.setLastUse(Instant.now());
            aiCooldownRepo.save(cd1);
            return true;}
        if(cd.getLastUse() == null){
            cd.setLastUse(Instant.now());
            aiCooldownRepo.save(cd);
            return true;
        }
        Instant allowedTime = cd.getLastUse().plusSeconds(COOLDONW_IN_SECONDS);
        cd.setLastUse(Instant.now());
        aiCooldownRepo.save(cd);
        return Instant.now().isAfter(allowedTime);
    }
}
