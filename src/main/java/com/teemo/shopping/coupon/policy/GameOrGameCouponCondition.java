package com.teemo.shopping.coupon.policy;

import com.teemo.shopping.game.dto.GameCategoryDTO;
import com.teemo.shopping.game.dto.GameDTO;

import java.util.List;

public class GameOrGameCouponCondition extends CouponCondition {
    public GameOrGameCouponCondition(List<Long> gameIds, List<Long> gameCategoryIds) {
        this.gameIds = gameIds;
        this.gameCategoryIds = gameCategoryIds;
    }
    private final List<Long> gameCategoryIds;
    private final List<Long> gameIds;

    @Override
    protected String getFalseMessage() {
        return "지원 하지 않는 쿠폰입니다.";
    }
    private boolean checkGameCategoryCondition(GameDTO game) {
        List<GameCategoryDTO> gameCategories = game.getGameCategories();
        for(var gameCategory : gameCategories) {        // improve: n ^ 2
            if(gameCategoryIds.contains(gameCategory.getId()))
                return true;
        }
        return false;
    }
    private boolean checkGameCondition(GameDTO game) {
        return gameIds.contains(game.getId());
    }
    @Override
    public boolean checkCondition(GameDTO game, int amount) {
        return checkGameCategoryCondition(game) || checkGameCondition(game);
    }
}
