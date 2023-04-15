package com.teemo.shopping.game.service;

import com.teemo.shopping.core.layer.ServiceLayer;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.dto.GameDTO;
import com.teemo.shopping.review.dto.ReviewDTO;
import com.teemo.shopping.game.repository.GameRepository;
import com.teemo.shopping.review.repository.ReviewRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameService implements ServiceLayer {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Transactional
    public Long add(GameDTO gameDTO) throws Exception {
        Game game = Game.builder().name(gameDTO.getName()).description(gameDTO.getDescription())
            .price(gameDTO.getPrice()).ratingCount(0).ratingAvg(0).build();
        game = gameRepository.save(game);
        return game.getId();
    }

    @Transactional
    public void remove(Long gameId) throws Exception {
        Game game = gameRepository.findById(gameId).orElseThrow(NoSuchElementException::new);
        gameRepository.deleteById(gameId);
    }

    @Transactional(readOnly = true)
    public GameDTO get(Long gameId) throws Exception {
        return GameDTO.from(gameRepository.findById(gameId).get());
    }

    @Transactional(readOnly = true)
    public List<GameDTO> list() {
        return gameRepository.findAll().stream()
            .map(game -> GameDTO.from(game)).toList();
    }
    @Transactional(readOnly = true)
    public List<ReviewDTO> list(Long gameId) {
        Game game = gameRepository.findById(gameId).get();
        return reviewRepository.findAllByGame(game).stream().map(review -> ReviewDTO.from(review)).toList();
    }

}
