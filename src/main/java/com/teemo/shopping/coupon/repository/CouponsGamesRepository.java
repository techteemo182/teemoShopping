package com.teemo.shopping.coupon.repository;

import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.CouponsGames;
import com.teemo.shopping.game.domain.GameCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponsGamesRepository extends JpaRepository<CouponsGames, Long> {
    List<CouponsGames> findAllByCoupon(Coupon coupon);
}
