package com.teemo.shopping.coupon.controller;

import com.teemo.shopping.coupon.dto.CouponDTO;
import com.teemo.shopping.coupon.service.CouponService;
import com.teemo.shopping.game.dto.GameCategoryDTO;
import com.teemo.shopping.game.dto.GameDTO;
import com.teemo.shopping.security.PermissionChecker;
import com.teemo.shopping.security.PermissionUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/coupons")
public class CouponController {
    @Autowired
    private CouponService couponService;
    @Autowired
    private PermissionChecker permissionChecker;

    @GetMapping(path = "/{couponId}")
    public CouponDTO get(@PathVariable("couponId") Long couponId) throws Exception {
        return couponService.get(couponId);
    }
    @PostMapping(path = "")
    public Long add(CouponDTO couponDTO) throws Exception {
        permissionChecker.checkAdminAndThrow();
        return couponService.add(couponDTO);
    }
    @DeleteMapping(path = "/{couponId}")
    public void remove(@PathVariable("couponId") Long couponId) throws Exception {
        permissionChecker.checkAdminAndThrow();
        couponService.remove(couponId);
    }
    @GetMapping(path = "/")
    public List<CouponDTO> list() {
        return couponService.list();
    }

    @PostMapping(path = "/{couponId}/games/{gameId}")
    public Long addGame(@PathVariable("couponId") Long couponId, @PathVariable("gameId") Long gameId) throws Exception {
        permissionChecker.checkAdminAndThrow();
        return couponService.addGame(couponId, gameId);
    }
    @PostMapping(path = "/{couponId}/game_categories/{gameCategoryId}")
    public Long addGameCategory(@PathVariable("couponId") Long couponId, @PathVariable("gameCategoryId") Long gameCategoryId) throws Exception {
        permissionChecker.checkAdminAndThrow();
        return couponService.addGameCategory(couponId, gameCategoryId);
    }
    @GetMapping(path = "/{couponId}/games/")
    public List<GameDTO> gameList(@PathVariable("couponId") Long couponId) {
        return couponService.gameList(couponId);
    }

    @GetMapping(path = "/{couponId}/game-categories/")
    public List<GameCategoryDTO> gameCategoryList(@PathVariable("couponId") Long couponId) {
        return couponService.gameCategoryList(couponId);
    }
    @DeleteMapping(path = "/{couponId}/games/{gameId}")
    public void removeGame(@PathVariable("couponId") Long couponId, @PathVariable("gameId") Long gameId) {
        permissionChecker.checkAdminAndThrow();
        couponService.removeGame(couponId, gameId);
    }
    @DeleteMapping(path = "/{couponId}/game-categories/{gameCategoryId}")
    public void removeGameCategory(@PathVariable("couponId") Long couponId, @PathVariable("gameCategoryId") Long gameCategoryId) {
        permissionChecker.checkAdminAndThrow();
        couponService.removeGameCategory(couponId, gameCategoryId);
    }
}
