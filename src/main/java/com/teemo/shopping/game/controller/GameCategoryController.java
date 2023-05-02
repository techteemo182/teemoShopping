package com.teemo.shopping.game.controller;

import com.teemo.shopping.game.dto.GameCategoryDTO;
import com.teemo.shopping.game.dto.GameDTO;
import com.teemo.shopping.game.service.GameCategoryService;
import com.teemo.shopping.security.PermissionChecker;
import com.teemo.shopping.security.enums.Role;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/game-categories")
public class GameCategoryController {
    @Autowired
    private GameCategoryService gameCategoryService;
    @Autowired
    private PermissionChecker permissionChecker;
    @Operation(operationId = "게임 카테고리 조회", summary = "게임 카테고리 조회", tags = {"게임카테고리"})
    @GetMapping(path = "/{gameCategoryId}")
    public GameCategoryDTO get(@PathVariable("gameCategoryId") Long gameCategoryId) throws Exception {
        return gameCategoryService.get(gameCategoryId);
    }
    @Operation(operationId = "게임 카테고리 추가", summary = "게임 카테고리 추가", tags = {"게임카테고리"})
    @PostMapping(path = "")
    public Long add(GameCategoryDTO gameCategoryDTO) throws Exception {
        permissionChecker.checkAdminAndThrow();
        return gameCategoryService.add(gameCategoryDTO);
    }
    @Operation(operationId = "게임 카테고리 삭제", summary = "게임 카테고리 삭제", tags = {"게임카테고리"})
    @DeleteMapping(path = "/{gameCategoryId}")
    public void remove(@PathVariable("gameCategoryId") Long gameCategoryId) throws Exception {
        permissionChecker.checkAdminAndThrow();
        gameCategoryService.remove(gameCategoryId);
    }
    @Operation(operationId = "게임 카테고리 리스트", summary = "게임 카테고리 리스트", tags = {"게임카테고리"})
    @GetMapping(path = "")
    public List<GameCategoryDTO> list() {
        return gameCategoryService.list();
    }

    @Operation(operationId = "게임 카테고리의 게임 리스트", summary = "게임 카테고리의 게임 리스트", tags = {"게임카테고리"})
    @GetMapping(path = "/{gameCategoryId}/games")
    public List<GameDTO> gameList(@PathVariable("gameCategoryId") Long gameCategoryId) {
        return gameCategoryService.gameList(gameCategoryId);
    }

    @Operation(operationId = "게임 카테고리에 게임 추가", summary = "게임 카테고리에 게임 추가", tags = {"게임카테고리"})
    @PostMapping(path = "/{gameCategoryId}/games/{gameId}") // improve: gameId 추가하면 밑에 파라미터로 자동으로 넣어 주기
    public String addGame(@PathVariable("gameCategoryId") Long gameCategoryId, @PathVariable("gameId") Long gameId) {
        permissionChecker.checkAdminAndThrow();
        gameCategoryService.addGame(gameCategoryId, gameId);
        return "success";
    }

    @Operation(operationId = "게임 카테고리에 게임 삭제", summary = "게임 카테고리에 게임 삭제", tags = {"게임카테고리"})
    @DeleteMapping(path = "/{gameCategoryId}/games/{gameId}")
    public String removeGame(@PathVariable("gameCategoryId") Long gameCategoryId, @PathVariable("gameId") Long gameId) {
        permissionChecker.checkAdminAndThrow();
        gameCategoryService.removeGame(gameCategoryId, gameId);
        return "success";
    }
}
