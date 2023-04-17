package com.teemo.shopping.game.controller;

import com.teemo.shopping.game.dto.GameDTO;
import com.teemo.shopping.resource.dto.ResourceDTO;
import com.teemo.shopping.review.dto.ReviewDTO;
import com.teemo.shopping.game.service.GameService;
import com.teemo.shopping.review.service.ReviewService;
import com.teemo.shopping.security.PermissionChecker;
import io.swagger.v3.oas.annotations.Operation;
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
    private ReviewService reviewService;
    @Autowired
    private PermissionChecker permissionChecker;
    @Operation(operationId = "게임 조회", summary = "게임 조회", tags = {"게임"})
    @GetMapping(path = "/{gameId}")
    public ResponseEntity<GameDTO> get(@PathVariable("gameId") Long gameId) throws Exception {
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
            .body(gameService.get(gameId));
    }
    @Operation(operationId = "게임 추가", summary = "게임 추가", tags = {"게임"})
    @PostMapping(path = "")
    public Long add(@RequestBody GameDTO gameDTO) throws Exception {
        permissionChecker.checkAdminAndThrow();
        return gameService.add(gameDTO);
    }
    @Operation(operationId = "게임 삭제", summary = "게임 삭제", tags = {"게임"})
    @DeleteMapping(path = "/{gameId}")
    public void remove(@PathVariable("gameId") Long gameId) throws Exception {
        permissionChecker.checkAdminAndThrow();
        gameService.remove(gameId);
    }
    @Operation(operationId = "게임 리스트", summary = "게임 리스트", tags = {"게임"})
    @GetMapping(path = "/")
    public List<GameDTO> list() {
        return gameService.list();
    }
    @Operation(operationId = "게임 리뷰 리스트", summary = "게임 리뷰 리스트", tags = {"게임"})
    @GetMapping(path = "/{gameId}/reviews/")
    public List<ReviewDTO> reviewList(@PathVariable("gameId") Long gameId) throws Exception {
        return gameService.reviewList(gameId);
    }
    @Operation(operationId = "게임 리소스 리스트", summary = "게임 리소스 리스트", tags = {"게임"})
    @GetMapping(path = "/{gameId}/resources/")
    public List<ResourceDTO> resourceList(@PathVariable("gameId") Long gameId) throws Exception {
        return gameService.resourceList(gameId);
    }
    @Operation(operationId = "게임 리소스 추가", summary = "게임 리소스 추가", tags = {"게임"})
    @PostMapping(path = "/{gameId}/resources/{resourcesId}")
    public String addResource(@PathVariable("gameId") Long gameId, @PathVariable("resourcesId") Long resourcesId) throws Exception {
        permissionChecker.checkAdminAndThrow();
        gameService.addResource(gameId, resourcesId);
        return "success";
    }
}
