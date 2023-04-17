package com.teemo.shopping.coupon.controller;

import com.teemo.shopping.coupon.dto.AddCouponRequest;
import com.teemo.shopping.coupon.dto.CouponDTO;
import com.teemo.shopping.coupon.service.CouponService;
import com.teemo.shopping.game.dto.GameCategoryDTO;
import com.teemo.shopping.game.dto.GameDTO;
import com.teemo.shopping.security.PermissionChecker;
import com.teemo.shopping.security.PermissionUtil;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/coupons")
public class CouponController {
    @Autowired
    private CouponService couponService;
    @Autowired
    private PermissionChecker permissionChecker;

    @Operation(operationId = "쿠폰조회", summary = "쿠폰조회", tags = {"쿠폰"})
    @GetMapping(path = "/{couponId}")
    public CouponDTO get(@PathVariable("couponId") Long couponId) throws Exception {
        return couponService.get(couponId);
    }
    @Operation(operationId = "쿠폰 추가", summary = "쿠폰 추가", tags = {"쿠폰"})
    @PostMapping(path = "")
    public Long add(@RequestBody AddCouponRequest addCouponRequest) throws Exception {
        permissionChecker.checkAdminAndThrow();
        return couponService.add(addCouponRequest);
    }
    @Operation(operationId = "쿠폰 삭제", summary = "쿠폰 삭제", tags = {"쿠폰"})
    @DeleteMapping(path = "/{couponId}")
    public void remove(@PathVariable("couponId") Long couponId) throws Exception {
        permissionChecker.checkAdminAndThrow();
        couponService.remove(couponId);
    }
    @Operation(operationId = "쿠폰 리스트", summary = "쿠폰 리스트", tags = {"쿠폰"})
    @GetMapping(path = "/")
    public List<CouponDTO> list() {
        return couponService.list();
    }

    @Operation(operationId = "쿠폰이 지원하는 게임추가", summary = "쿠폰이 지원하는 게임추가", tags = {"쿠폰"})
    @PostMapping(path = "/{couponId}/games/{gameId}")
    public Long addGame(@PathVariable("couponId") Long couponId, @PathVariable("gameId") Long gameId) throws Exception {
        permissionChecker.checkAdminAndThrow();
        return couponService.addGame(couponId, gameId);
    }
    @Operation(operationId = "쿠폰이 지원하는 게임 카테고리 추가", summary = "쿠폰이 지원하는 게임 카테고리 추가", tags = {"쿠폰"})
    @PostMapping(path = "/{couponId}/game_categories/{gameCategoryId}")
    public Long addGameCategory(@PathVariable("couponId") Long couponId, @PathVariable("gameCategoryId") Long gameCategoryId) throws Exception {
        permissionChecker.checkAdminAndThrow();
        return couponService.addGameCategory(couponId, gameCategoryId);
    }
    @Operation(operationId = "쿠폰이 지원하는 게임 리스트", summary = "쿠폰이 지원하는 게임 리스트", tags = {"쿠폰"})
    @GetMapping(path = "/{couponId}/games/")
    public List<GameDTO> gameList(@PathVariable("couponId") Long couponId) {
        return couponService.gameList(couponId);
    }

    @Operation(operationId = "쿠폰이 지원하는 게임 카테고리", summary = "쿠폰이 지원하는 게임 카테고리", tags = {"쿠폰"})
    @GetMapping(path = "/{couponId}/game-categories/")
    public List<GameCategoryDTO> gameCategoryList(@PathVariable("couponId") Long couponId) {
        return couponService.gameCategoryList(couponId);
    }
    @Operation(operationId = "쿠폰이 지원하는 게임 삭제", summary = "쿠폰이 지원하는 게임 삭제", tags = {"쿠폰"})
    @DeleteMapping(path = "/{couponId}/games/{gameId}")
    public void removeGame(@PathVariable("couponId") Long couponId, @PathVariable("gameId") Long gameId) {
        permissionChecker.checkAdminAndThrow();
        couponService.removeGame(couponId, gameId);
    }
    @Operation(operationId = "쿠폰이 지원하는 게임카테고리 삭제", summary = "쿠폰이 지원하는 게임카테고리 삭제", tags = {"쿠폰"})
    @DeleteMapping(path = "/{couponId}/game-categories/{gameCategoryId}")
    public void removeGameCategory(@PathVariable("couponId") Long couponId, @PathVariable("gameCategoryId") Long gameCategoryId) {
        permissionChecker.checkAdminAndThrow();
        couponService.removeGameCategory(couponId, gameCategoryId);
    }
}
