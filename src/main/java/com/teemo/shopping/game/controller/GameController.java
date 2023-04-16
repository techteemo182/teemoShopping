package com.teemo.shopping.game.controller;

import com.teemo.shopping.game.dto.GameDTO;
import com.teemo.shopping.resource.dto.ResourceDTO;
import com.teemo.shopping.review.dto.ReviewDTO;
import com.teemo.shopping.game.service.GameService;
import com.teemo.shopping.security.PermissionChecker;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/games")
public class GameController {
    @Autowired
    private GameService gameService;
    @Autowired
    private PermissionChecker permissionChecker;
    @GetMapping(path = "/{gameId}")
    public ResponseEntity<GameDTO> get(@PathVariable("gameId") Long gameId) throws Exception {
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
            .body(gameService.get(gameId));
    }
    @PostMapping(path = "")
    public Long add(@RequestBody GameDTO gameDTO) throws Exception {
        permissionChecker.checkAdminAndThrow();
        return gameService.add(gameDTO);
    }
    @DeleteMapping(path = "/{gameId}")
    public void remove(@PathVariable("gameId") Long gameId) throws Exception {
        permissionChecker.checkAdminAndThrow();
        gameService.remove(gameId);
    }
    @GetMapping(path = "/")
    public List<GameDTO> list() {
        return gameService.list();
    }
    @GetMapping(path = "/{gameId}/reviews/")
    public List<ReviewDTO> getReviews(@PathVariable("gameId") Long gameId) throws Exception {
        return gameService.reviewList(gameId);
    }
    @GetMapping(path = "/{gameId}/resources/")
    public List<ResourceDTO> getResources(@PathVariable("gameId") Long gameId) throws Exception {
        return gameService.resourceList(gameId);
    }
    @PostMapping(path = "/{gameId}/resources/{resourcesId}")
    public String addResource(@PathVariable("gameId") Long gameId, @PathVariable("resourcesId") Long resourcesId) throws Exception {
        permissionChecker.checkAdminAndThrow();
        gameService.addResource(gameId, resourcesId);
        return "success";
    }
}
