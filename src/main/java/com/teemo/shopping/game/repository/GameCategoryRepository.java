package com.teemo.shopping.game.repository;

import com.teemo.shopping.game.domain.GameCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameCategoryRepository extends JpaRepository<GameCategory, Long> {

}
