package com.chitas.carderio.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chitas.carderio.model.Folder;

import java.util.List;

public interface FoldersRepo extends JpaRepository<Folder, Long> {
    List<Folder> findByParentId(Long parentId); // Get subfolders
    List<Folder> findByDepth(int depth);        // Get folders by depth
}

