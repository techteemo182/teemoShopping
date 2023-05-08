package com.teemo.shopping.game.service;

import com.teemo.shopping.core.exception.ServiceException;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.domain.GameCategoriesGames;
import com.teemo.shopping.game.domain.GameCategory;
import com.teemo.shopping.game.dto.GameCategoryDTO;
import com.teemo.shopping.game.dto.GameDTO;
import com.teemo.shopping.game.repository.GameCategoriesGamesRepository;
import com.teemo.shopping.game.repository.GameCategoryRepository;
import com.teemo.shopping.game.repository.GameRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameCategoryService {

    @Autowired
    GameCategoryRepository gameCategoryRepository;
    @Autowired
    GameCategoriesGamesRepository gameCategoriesGamesRepository;
    @Autowired
    GameRepository gameRepository;

    @Transactional
    public Long add(GameCategoryDTO gameCategoryDTO) throws IllegalArgumentException {
        return gameCategoryRepository.save(gameCategoryDTO.to()).getId();
    }

    @Transactional
    public void remove(Long gameCategoryId) throws IllegalArgumentException {
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

    @Transactional(readOnly = true)
    public List<GameDTO> gameList(Long gameCategoryId) {
        GameCategory gameCategory = gameCategoryRepository.findById(gameCategoryId).get();
        return gameCategoriesGamesRepository.findAllByGameCategory(gameCategory).stream()
            .map(gameCategoriesGamesEntry -> GameDTO.from(gameCategoriesGamesEntry.getGame()))
            .toList();
    }

    @Transactional(readOnly = true)
    public void addGame(Long gameCategoryId, Long gameId) {
        Game game = gameRepository.findById(gameId).get();
        GameCategory gameCategory = gameCategoryRepository.findById(gameCategoryId).get();
        if (gameCategoriesGamesRepository.existsByGameAndGameCategory(game, gameCategory)) {
            throw ServiceException.of("이미 GameCategory Game 관계가 있음");
        }
        gameCategoriesGamesRepository.save(
            GameCategoriesGames.builder().game(game).gameCategory(gameCategory).build()
        );
    }
    @Transactional(readOnly = true)
    public void removeGame(Long gameCategoryId, Long gameId) {
        Game game = gameRepository.findById(gameId).get();
        GameCategory gameCategory = gameCategoryRepository.findById(gameCategoryId).get();
        GameCategoriesGames gameCategoriesGames = gameCategoriesGamesRepository.findByGameAndGameCategory(game, gameCategory).orElseThrow(() -> ServiceException.of("GameCategory Game 관계가 없음"));
        gameCategoriesGamesRepository.delete(gameCategoriesGames);
    }
}

