package com.teemo.shopping.game.repository;

import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.domain.GameCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
}
