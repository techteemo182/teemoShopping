package com.teemo.shopping.game.controller;

import com.teemo.shopping.game.dto.GameDTO;
import com.teemo.shopping.game.service.GameService;
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
@RequestMapping(path = "/games")
public class GameController {
    @Autowired
    private GameService gameService;
    @Autowired
    private PermissionChecker permissionChecker;
    @GetMapping(path = "/{gameId}")
    public GameDTO get(@PathVariable("gameId") Long gameId) throws Exception {
        return gameService.get(gameId);
    }
    @PostMapping(path = "/")
    public Long add(GameDTO gameDTO) throws Exception {
        if(!permissionChecker.checkAdmin()) {
            throw new SecurityException("접근 권한 없음");
        }
        return gameService.add(gameDTO);
    }
    @DeleteMapping(path = "/{gameId}")
    public void remove(@PathVariable("gameId") Long gameId) throws Exception {
        if(!permissionChecker.checkAdmin()) {
            throw new SecurityException("접근 권한 없음");
        }
        gameService.remove(gameId);
    }
    @GetMapping(path = "/")
    public List<GameDTO> list() {
        return gameService.list();
    }
}
