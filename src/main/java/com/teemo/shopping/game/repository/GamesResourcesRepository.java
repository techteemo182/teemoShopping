package com.teemo.shopping.game.repository;

import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.domain.GamesResources;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GamesResourcesRepository extends JpaRepository<GamesResources, Long> {
    List<GamesResources> findAllByGame(Game game);
}
