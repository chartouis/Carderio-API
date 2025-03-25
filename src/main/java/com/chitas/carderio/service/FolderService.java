package com.chitas.carderio.service;

import org.springframework.stereotype.Service;
import com.chitas.carderio.model.Card;
import com.chitas.carderio.model.Folder;
import com.chitas.carderio.model.DTO.FolderDTO;
import com.chitas.carderio.repo.CardsRepo;
import com.chitas.carderio.repo.FoldersRepo;
import com.chitas.carderio.utils.AnnoyingConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class FolderService {

    private final FoldersRepo folderRepository;
    private final CardsRepo cardRepository;
    private final AnnoyingConstants aconst;
    private static final Logger LOGGER = Logger.getLogger(FolderService.class.getName());
    private static final int MAX_DEPTH = 5;

    public FolderService(CardsRepo cardRepository, FoldersRepo folderRepository, AnnoyingConstants aconst) {
        this.folderRepository = folderRepository;
        this.cardRepository = cardRepository;
        this.aconst = aconst;
    }

    public Folder createFolder(Folder folder) {
        folder.setUser(aconst.getCurrentUser());
        if (folder.getParent() == null) {
            folder.setDepth(0);
        } else {
            Folder parent = folderRepository.findById(folder.getParent().getId()).orElse(null);
            if (parent == null || !parent.getUser().getId().equals(aconst.getCurrentUser().getId())) {
                LOGGER.warning("Parent folder not found or not owned");
                return null;
            }
            int newDepth = parent.getDepth() + 1;
            if (newDepth > MAX_DEPTH) {
                LOGGER.warning("Depth " + newDepth + " exceeds max of " + MAX_DEPTH);
                return null;
            }
            folder.setDepth(newDepth);
        }
        return folderRepository.save(folder);
    }

    public Folder updateFolder(Long id, String newName) {
        Folder folder = folderRepository.findById(id).orElse(null);
        if (folder == null || !folder.getUser().getId().equals(aconst.getCurrentUser().getId())) {
            LOGGER.warning("Folder not found or not owned");
            return null;
        }
        folder.setName(newName);
        return folderRepository.save(folder);
    }

    public boolean deleteFolder(Long id) {
        Folder folder = folderRepository.findById(id).orElse(null);
        if (folder == null || !folder.getUser().getId().equals(aconst.getCurrentUser().getId())) {
            LOGGER.warning("Folder not found or not owned");
            return false;
        }
        folderRepository.delete(folder);
        return true;
    }

    public List<Card> getCardsInFolder(Long folderId) {
        Folder folder = folderRepository.findById(folderId).orElse(null);
        if (folder == null || !folder.getUser().getId().equals(aconst.getCurrentUser().getId())) {
            LOGGER.warning("Folder not found or not owned");
            return List.of();
        }
        List<Card> result = new ArrayList<>();
        collectCards(folder, result);
        return result;
    }

    private void collectCards(Folder folder, List<Card> result) {
        List<Card> directCards = cardRepository.findByFolderId(folder.getId());
        if (directCards != null) {
            result.addAll(directCards);
        }
        List<Folder> subfolders = folderRepository.findByParentId(folder.getId());
        if (subfolders != null) {
            for (Folder sub : subfolders) {
                if (sub.getUser().getId().equals(aconst.getCurrentUser().getId())) {
                    collectCards(sub, result);
                }
            }
        }
    }

    public boolean addCardToFolder(Long cardId, Long folderId) {
        Card card = cardRepository.findById(cardId).orElse(null);
        Folder folder = folderRepository.findById(folderId).orElse(null);
        if (card == null || folder == null) {
            LOGGER.warning("Card or folder not found");
            return false;
        }
        if (!card.getUser().getId().equals(aconst.getCurrentUser().getId()) || 
            !folder.getUser().getId().equals(aconst.getCurrentUser().getId())) {
            LOGGER.warning("Card or folder not owned by user");
            return false;
        }
        card.setFolder(folder);
        cardRepository.save(card);
        return true;
    }

    public List<FolderDTO> getAllUserFolders() {
        List<Folder> folders = folderRepository.findByUserId(aconst.getCurrentUser().getId());
        List<FolderDTO> dtos = new ArrayList<>();
        for (Folder folder : folders) {
            Long parentId = folder.getParent() != null ? folder.getParent().getId() : null;
            dtos.add(new FolderDTO(folder.getId(), folder.getName(), parentId));
        }
        return dtos;
    }
}