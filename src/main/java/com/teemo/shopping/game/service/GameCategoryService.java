package com.teemo.shopping.game.service;

import com.teemo.shopping.game.domain.GameCategory;
import com.teemo.shopping.game.dto.GameCategoryAdd;
import com.teemo.shopping.game.dto.GameCategoryRemoveById;
import com.teemo.shopping.game.dto.GameCategoryModifyName;
import com.teemo.shopping.game.repository.GameCategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

public class GameCategoryService {
    @Autowired
    GameCategoryRepository gameRepository;

    @Transactional
    public void add(GameCategoryAdd gameCategoryCreate) throws Exception {
        gameRepository.save(GameCategory.builder().name(gameCategoryCreate.getName()).build());
    }

    @Transactional
    public void removeById(GameCategoryRemoveById gameCategory) throws Exception {
        gameRepository.deleteById(gameCategory.getGameCategoryId());
    }

    @Transactional
    public void modifyName(GameCategoryModifyName gameCategoryUpdate) throws Exception {
        GameCategory gameCategory = gameRepository.findById(gameCategoryUpdate.getGameCategoryId()).orElseThrow(RuntimeException::new);
        gameCategory.updateName(gameCategoryUpdate.getName());
        gameRepository.save(gameCategory);
    }
}

