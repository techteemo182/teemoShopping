package com.teemo.shopping.order.service.context;

import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.order.service.OrderCreateService.OrderOption;
import com.teemo.shopping.order.service.OrderCreateService.PreparedData;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
//재설계
public class OrderCreateContext {
    private final PreparedData preparedData;
    private final OrderOption orderOption;
    private final Integer amount;
    private final Optional<Game> game;
}