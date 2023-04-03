package com.teemo.shopping.Order.service.payment_factory;

import com.teemo.shopping.Order.domain.OrdersPayments;
import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.coupon.domain.Coupon;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@Component
public class OrderedSequencePaymentGenerator {

    @Autowired
    private List<SingleProductPaymentFactory> singleProductPaymentFactories;
    @Autowired
    private List<AllProductPaymentFactory> allProductPaymentFactories;

    @Transactional(rollbackOn = RuntimeException.class)
    public List<Payment> process(OrderContext orderContext) {
        int totalRemainPrice = 0;
        List<Payment> payments = new ArrayList<>();
        for (var game : orderContext.getGames()) {
            Optional<Coupon> coupon = orderContext.getGameCouponMap().get(game);
            SingleGameProductContext context = SingleGameProductContext.builder()
                .game(game)
                .coupon(coupon == null ? Optional.empty() : coupon)
                .account(orderContext.getAccount())
                .remainPrice(game.getPrice())
                .build();
            for (var singleProductPaymentFactory : singleProductPaymentFactories) {
                singleProductPaymentFactory.create(context)
                    .ifPresent(payment -> payments.add(payment));
            }
            totalRemainPrice += context.getRemainPrice();
        }

        {
            AllGameProductContext context = AllGameProductContext.builder()
                .games(orderContext.getGames())
                .account(orderContext.getAccount())
                .point(orderContext.getPoint())
                .totalRemainPrice(totalRemainPrice)
                .build();
            for (var allProductPaymentFactory : allProductPaymentFactories) {
                allProductPaymentFactory.create(context)
                    .ifPresent(payment -> payments.add(payment));
            }
            totalRemainPrice = context.getTotalRemainPrice();
        }

        if (totalRemainPrice == 0) {
            throw new RuntimeException();   //ROLLBACk
        }

        return payments;
    }
}
