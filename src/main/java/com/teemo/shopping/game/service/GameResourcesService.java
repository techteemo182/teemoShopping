package com.teemo.shopping.game.service;

import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.domain.GamesResources;
import com.teemo.shopping.game.repository.GameRepository;
import com.teemo.shopping.game.repository.GamesResourcesRepository;
import com.teemo.shopping.resource.domain.Resource;
import com.teemo.shopping.resource.dto.ResourceDTO;
import com.teemo.shopping.resource.repository.ResourceRepository;
import com.teemo.shopping.resource.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameResourcesService {
    @Autowired
    private GamesResourcesRepository gamesResourcesRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private ResourceService resourceService;

    //experimental: 새로운 Service 구성
    @Transactional
    public Long add(Long gameId, Long resourceId) throws Exception {
        Game game = gameService.getGame(gameId);
        Resource resource = resourceService.getResource(resourceId);
        GamesResources gamesResources = GamesResources.builder()
                .game(game)
                .resource(resource)
                .build();
        gamesResourcesRepository.save(gamesResources);
        return gamesResources.getId();
    }
    @Transactional(readOnly = true)
    public List<ResourceDTO> resourceList(Long gameId) {
        Game game = gameService.getGame(gameId);
        return gamesResourcesRepository.findAllByGame(game).stream()
                .map(gamesResources -> ResourceDTO.from(gamesResources.getResource())).toList();
    }
}
