package com.teemo.shopping.game.repository;

import com.teemo.shopping.game.domain.GameCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameCategoryRepository extends JpaRepository<GameCategory, Long> {


}
