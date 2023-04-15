package com.teemo.shopping.coupon.service;

import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.CouponsGameCategories;
import com.teemo.shopping.coupon.domain.CouponsGames;
import com.teemo.shopping.coupon.dto.CouponDTO;
import com.teemo.shopping.coupon.repository.CouponRepository;
import com.teemo.shopping.coupon.repository.CouponsGameCategoriesRepository;
import com.teemo.shopping.coupon.repository.CouponsGamesRepository;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.domain.GameCategory;
import com.teemo.shopping.game.dto.GameCategoryDTO;
import com.teemo.shopping.game.dto.GameDTO;
import com.teemo.shopping.game.repository.GameCategoryRepository;
import com.teemo.shopping.game.repository.GameRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameCategoryRepository gameCategoryRepository;
    @Autowired
    private CouponsGamesRepository couponsGamesRepository;
    @Autowired
    private CouponsGameCategoriesRepository couponsGameCategoriesRepository;

    @Transactional
    public Long add(CouponDTO couponDTO) throws Exception { //todo: game, gameCategory 추가 하기
        Coupon coupon = couponDTO.to();
        coupon = couponRepository.save(coupon);
        return coupon.getId();
    }

    @Transactional
    public void remove(Long couponId) throws Exception {
        if (!couponRepository.existsById(couponId)) {
            throw new IllegalStateException("존재하지 않는 쿠폰임.");
        }
        couponRepository.deleteById(couponId);
    }

    @Transactional(readOnly = true)
    public CouponDTO get(Long couponId) throws Exception {
        return CouponDTO.from(couponRepository.findById(couponId).get());
    }

    @Transactional(readOnly = true)
    public List<CouponDTO> list() {
        return couponRepository.findAll().stream().map(coupon -> CouponDTO.from(coupon)).toList();
    }

    @Transactional
    public Long addGame(Long couponId, Long gameId) {
        Coupon coupon = couponRepository.findById(couponId).get();
        Game game = gameRepository.findById(gameId).get();
        CouponsGames couponsGames = CouponsGames.builder().coupon(coupon).game(game).build();
        couponsGames = couponsGamesRepository.save(couponsGames);
        return couponsGames.getId();
    }

    @Transactional
    public Long addGameCategory(Long couponId, Long gameCategoryId) {
        Coupon coupon = couponRepository.findById(couponId).get();
        GameCategory gameCategory = gameCategoryRepository.findById(gameCategoryId).get();

        CouponsGameCategories couponsGameCategories = CouponsGameCategories.builder().coupon(coupon)
            .gameCategory(gameCategory).build();
        couponsGameCategories = couponsGameCategoriesRepository.save(couponsGameCategories);
        return couponsGameCategories.getId();
    }

    @Transactional
    public List<GameDTO> gameList(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId).get();
        return couponsGamesRepository.findAllByCoupon(coupon).stream()
            .map(couponsGames -> GameDTO.from(couponsGames.getGame())).toList();
    }

    @Transactional
    public List<GameCategoryDTO> gameCategoryList(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId).get();
        return couponsGameCategoriesRepository.findAllByCoupon(coupon).stream().map(
                couponsGameCategories -> GameCategoryDTO.from(couponsGameCategories.getGameCategory()))
            .toList();
    }

    public void removeGame(Long couponId, Long gameId) {
        Coupon coupon = couponRepository.findById(couponId).get();
        Game game = gameRepository.findById(gameId).get();
        couponsGamesRepository.deleteByCouponAndGame(coupon, game);
    }
    public void removeGameCategory(Long couponId, Long gameCategoryId) {
        Coupon coupon = couponRepository.findById(couponId).get();
        GameCategory gameCategory = gameCategoryRepository.findById(gameCategoryId).get();
        couponsGameCategoriesRepository.deleteByCouponAndGameCategory(coupon, gameCategory);
    }


}
