package com.teemo.shopping.game.repository;

import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.domain.GameCategoriesGames;
import com.teemo.shopping.game.domain.GameCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameCategoriesGamesRepository extends JpaRepository<GameCategoriesGames, Long> {
    List<GameCategoriesGames> findAllByGameCategory(GameCategory gameCategory);
    List<GameCategoriesGames> findAllByGame(Game game);
}
