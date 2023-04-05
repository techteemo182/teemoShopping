package com.teemo.shopping.game.service;

import com.teemo.shopping.core.layer.ServiceLayer;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.dto.GameAdd;
import com.teemo.shopping.game.dto.GameRemoveById;
import com.teemo.shopping.game.repository.GameRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService implements ServiceLayer {

    @Autowired
    GameRepository gameRepository;

    @Transactional
    public void add(GameAdd gameAdd) throws Exception {
        Game game = Game.builder().name(gameAdd.getName()).description(gameAdd.getDescription())
            .price(gameAdd.getPrice()).ratingCount(0).ratingAvg(0).build();
        gameRepository.save(game);
    }

    @Transactional
    public void removeById(GameRemoveById gameRemoveById) throws Exception {
        gameRepository.deleteById(gameRemoveById.getId());

    }

}
