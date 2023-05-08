package com.teemo.shopping.game.service;

import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.domain.GamesResources;
import com.teemo.shopping.game.dto.GameDTO;
import com.teemo.shopping.game.repository.GameRepository;
import com.teemo.shopping.game.repository.GamesResourcesRepository;
import com.teemo.shopping.resource.domain.Resource;
import com.teemo.shopping.resource.dto.ResourceDTO;
import com.teemo.shopping.resource.repository.ResourceRepository;
import com.teemo.shopping.review.dto.ReviewDTO;
import com.teemo.shopping.review.repository.ReviewRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameService  {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private GamesResourcesRepository gamesResourcesRepository;
    @Autowired
    private ResourceRepository resourceRepository;
    @Transactional
    public Long add(GameDTO gameDTO) throws Exception {
        Game game = Game.builder().name(gameDTO.getName()).description(gameDTO.getDescription())
            .price(gameDTO.getPrice()).ratingCount(0).ratingAvg(0).build();
        game = gameRepository.save(game);
        return game.getId();
    }
    @Transactional
    public Long addResource(Long gameId, Long resourceId) throws Exception {
        Game game = gameRepository.findById(gameId).get();
        Resource resource = resourceRepository.findById(resourceId).get();
        GamesResources gamesResources = GamesResources.builder()
            .game(game)
            .resource(resource)
            .build();
        gamesResources = gamesResourcesRepository.save(gamesResources);
        return gamesResources.getId();
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
        return gameRepository.findAll().stream().map(game -> GameDTO.from(game)).toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> reviewList(Long gameId) {
        Game game = gameRepository.findById(gameId).get();
        return reviewRepository.findAllByGame(game).stream().map(review -> ReviewDTO.from(review))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<ResourceDTO> resourceList(Long gameId) {
        Game game = gameRepository.findById(gameId).get();
        return gamesResourcesRepository.findAllByGame(game).stream()
            .map(gamesResources -> ResourceDTO.from(gamesResources.getResource())).toList();
    }
}
