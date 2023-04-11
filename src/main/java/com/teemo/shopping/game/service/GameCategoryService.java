package com.teemo.shopping.game.service;

import com.teemo.shopping.core.layer.ServiceLayer;
import com.teemo.shopping.game.domain.GameCategory;
import com.teemo.shopping.game.dto.GameCategoryDTO;
import com.teemo.shopping.game.repository.GameCategoryRepository;
import com.teemo.shopping.security.enums.Role;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameCategoryService implements ServiceLayer {

    @Autowired
    GameCategoryRepository gameCategoryRepository;

    @Transactional
    public Long add(GameCategoryDTO gameCategoryDTO) throws
        IllegalArgumentException {
        return gameCategoryRepository.save(gameCategoryDTO.to()).getId();
    }

    @Transactional
    public void remove(Long gameCategoryId) throws
        IllegalArgumentException {
        gameCategoryRepository.deleteById(gameCategoryId);
    }

    @Transactional
    public void updateName(GameCategoryDTO gameCategoryDTO) throws NoSuchElementException {
        Long gameCategoryId = gameCategoryDTO.getId();
        GameCategory gameCategory = gameCategoryRepository.findById(gameCategoryId).get();
        gameCategory.updateName(gameCategoryDTO.getName());
        gameCategoryRepository.save(gameCategory);
    }

    @Transactional(readOnly = true)
    public GameCategoryDTO get(Long gameCategoryId) throws NoSuchElementException {
        return GameCategoryDTO.from(gameCategoryRepository.findById(gameCategoryId).get());
    }

    @Transactional(readOnly = true)
    public List<GameCategoryDTO> list() {
        return gameCategoryRepository.findAll().stream()
            .map(gameCategory -> GameCategoryDTO.from(gameCategory)).toList();
    }
}

