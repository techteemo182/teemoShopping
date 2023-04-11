package com.teemo.shopping.coupon.service;

import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.dto.CouponDTO;
import com.teemo.shopping.coupon.repository.CouponRepository;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.dto.GameDTO;
import com.teemo.shopping.game.repository.GameRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponService {
    @Autowired
    private CouponRepository couponRepository;

    @Transactional
    public Long add(CouponDTO couponDTO) throws Exception {
        Coupon coupon = couponDTO.to();
        coupon = couponRepository.save(coupon);
        return coupon.getId();
    }

    @Transactional
    public void remove(Long couponId) throws Exception {
        couponRepository.deleteById(couponId);
    }

    @Transactional(readOnly = true)
    public CouponDTO get(Long couponId) throws Exception {
        return CouponDTO.from(couponRepository.findById(couponId).get());
    }

    @Transactional(readOnly = true)
    public List<CouponDTO> list() {
        return couponRepository.findAll().stream()
            .map(coupon -> CouponDTO.from(coupon)).toList();
    }
}
