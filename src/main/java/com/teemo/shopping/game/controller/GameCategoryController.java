package com.teemo.shopping.game.controller;

import com.teemo.shopping.game.dto.GameCategoryDTO;
import com.teemo.shopping.game.dto.GameDTO;
import com.teemo.shopping.game.service.GameCategoryService;
import com.teemo.shopping.security.PermissionChecker;
import com.teemo.shopping.security.enums.Role;
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
    @GetMapping(path = "/{gameCategoryId}")
    public GameCategoryDTO get(@PathVariable("gameCategoryId") Long gameCategoryId) throws Exception {
        return gameCategoryService.get(gameCategoryId);
    }
    @PostMapping(path = "")
    public Long add(GameCategoryDTO gameCategoryDTO) throws Exception {
        permissionChecker.checkAdminAndThrow();
        return gameCategoryService.add(gameCategoryDTO);
    }
    @DeleteMapping(path = "/{gameCategoryId}")
    public void remove(@PathVariable("gameCategoryId") Long gameCategoryId) throws Exception {
        permissionChecker.checkAdminAndThrow();
        gameCategoryService.remove(gameCategoryId);
    }
    @GetMapping(path = "/")
    public List<GameCategoryDTO> list() {
        return gameCategoryService.list();
    }

    @GetMapping(path = "/{gameCategoryId}/games/")
    public List<GameDTO> gameList(@PathVariable("gameCategoryId") Long gameCategoryId) {
        return gameCategoryService.gameList(gameCategoryId);
    }
}
