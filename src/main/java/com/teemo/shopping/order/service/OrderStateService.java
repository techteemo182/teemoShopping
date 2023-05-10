package com.teemo.shopping.order.service;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.domain.AccountsOwnGames;
import com.teemo.shopping.account.repository.AccountsOwnGamesRepository;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.order.domain.OrdersGames;
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.enums.OrderStates;
import com.teemo.shopping.order.enums.OrdersGamesStates;
import com.teemo.shopping.order.enums.PaymentStates;
import com.teemo.shopping.order.repository.OrderRepository;
import com.teemo.shopping.order.repository.OrdersGamesRepository;
import com.teemo.shopping.order.repository.PaymentRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderStateService {
    @Autowired OrderRepository orderRepository;
    @Autowired PaymentRepository paymentRepository;
    @Autowired OrdersGamesRepository  ordersGamesRepository;
    @Autowired OrderRefundService orderRefundService;
    @Autowired AccountsOwnGamesRepository accountsOwnGamesRepository;
    @Transactional
    public void orderStateTransition(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        List<Payment> payments = order.getPayments();
        boolean existsPaymentPending = false;
        boolean existsPaymentCancel = false;
        boolean existsPaymentPendingRefund = false;
        boolean existsPaymentRefunded = false;
        boolean existsPaymentPartialRefunded = false;
        boolean isAllPaymentRefunded = true;
        for (var payment : payments) {
            if (payment.getState().equals(PaymentStates.CANCEL)) {
                existsPaymentCancel = true;
            } else if (payment.getState().equals(PaymentStates.PENDING)) {
                existsPaymentPending = true;
            } else if(payment.getState().equals(PaymentStates.PENDING_REFUND)) {
                existsPaymentPendingRefund = true;
            } else if(payment.getState().equals(PaymentStates.REFUNDED)) {
                existsPaymentRefunded = true;
            } else if(payment.getState().equals(PaymentStates.PARTIAL_REFUNDED)) {
                existsPaymentPartialRefunded = true;
            }

            if(!payment.getState().equals(PaymentStates.REFUNDED)) {
                isAllPaymentRefunded = false;
            }
        }
        boolean isOrderSuccess = !existsPaymentPending && !existsPaymentCancel;
        boolean isOrderCancel = !existsPaymentPending && existsPaymentCancel;
        boolean isOrderPendingRefund = existsPaymentPendingRefund;
        boolean isOrderPartialRefunded = !isAllPaymentRefunded && (existsPaymentPartialRefunded || existsPaymentRefunded) && !existsPaymentPendingRefund;
        boolean isOrderRefunded = isAllPaymentRefunded;
        var orderState = order.getState();
        if (orderState.equals(OrderStates.PENDING) && isOrderCancel) { // PENDING -> CANCEL
            order.updateState(OrderStates.CANCEL);
            try {
                orderRefundService.refundOrder(orderId);
            } catch (Exception e) {
                throw new IllegalStateException("환불 실패 고객센터에 문의하세요.");
            }
            List<OrdersGames> ordersGames = ordersGamesRepository.findAllByOrder(order);
            for (var ordersGamesEntry : ordersGames) {
                ordersGamesEntry.updateState(OrdersGamesStates.CANCEL);
            }
        }
        else if (orderState.equals(OrderStates.PENDING) && isOrderSuccess) {  //PENDING -> SUCCESS
            order.updateState(OrderStates.SUCCESS);
            Account account = order.getAccount();
            List<OrdersGames> ordersGames = ordersGamesRepository.findAllByOrder(order);
            for (var ordersGamesEntry : ordersGames) {
                ordersGamesEntry.updateState(OrdersGamesStates.PURCHASE);
                Game game = ordersGamesEntry.getGame();
                accountsOwnGamesRepository.save(
                    AccountsOwnGames.builder().account(account).game(game).build());
            }
        }
        else if(orderState.equals(OrderStates.PENDING_REFUND) && (isOrderPartialRefunded || isOrderRefunded)) {     // PENDING_REFUND -> REFUND || PARTIAL_REFUND
            List<OrdersGames> ordersGames = ordersGamesRepository.findAllByOrder(order);
            for (var ordersGamesEntry : ordersGames) {
                if(ordersGamesEntry.getState().equals(OrdersGamesStates.PENDING_REFUND)) {
                    ordersGamesEntry.updateState(OrdersGamesStates.REFUND);
                    accountsOwnGamesRepository.deleteByAccountAndGame(order.getAccount(), ordersGamesEntry.getGame());  // 환불한 게임 계정에서 삭제
                }
            }
            if(isOrderPartialRefunded) {
                order.updateState(OrderStates.PARTIAL_REFUNDED);
            } else if(isOrderRefunded) {
                order.updateState(OrderStates.REFUNDED);
            }
        }
    }
}
