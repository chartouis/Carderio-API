package com.chitas.carderio.repo;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chitas.carderio.model.CoolDown;

@Repository
public interface AICooldownRepo extends JpaRepository<CoolDown, Long> {
    
    @Query("SELECT c FROM CoolDown c WHERE c.user.id = :userId")
    CoolDown findByUserId(@Param("userId") Long userId);

    @Query("SELECT c.lastUse FROM CoolDown c WHERE c.user.id = :userId")
    Instant findLastUseByUser(@Param("userId") Long userId);

}
