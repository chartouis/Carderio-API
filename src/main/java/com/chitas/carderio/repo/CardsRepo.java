package com.chitas.carderio.repo;

import com.chitas.carderio.model.Card;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardsRepo extends JpaRepository<Card, Long> {
    List<Card> findByFolderId(Long folderId);
    List<Card> findByFolderIsNull();
}
