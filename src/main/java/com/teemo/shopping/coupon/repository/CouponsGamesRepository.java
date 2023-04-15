package com.teemo.shopping.coupon.repository;

import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.CouponsGames;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.domain.GameCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponsGamesRepository extends JpaRepository<CouponsGames, Long> {
    List<CouponsGames> findAllByCoupon(Coupon coupon);
    Optional<CouponsGames> findByCouponAndGame(Coupon coupon, Game game);
    void deleteByCouponAndGame(Coupon coupon, Game game);
}
