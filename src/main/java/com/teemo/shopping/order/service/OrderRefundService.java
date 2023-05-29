package com.teemo.shopping.order.service;

import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.repository.GameRepository;
import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.order.domain.OrdersGames;
import com.teemo.shopping.payment.domain.Payment;
import com.teemo.shopping.order.domain.enums.OrderStates;
import com.teemo.shopping.order.domain.enums.OrdersGamesStates;
import com.teemo.shopping.payment.domain.enums.PaymentMethods;
import com.teemo.shopping.payment.domain.enums.PaymentStates;
import com.teemo.shopping.order.repository.OrderRepository;
import com.teemo.shopping.order.repository.OrdersGamesRepository;
import com.teemo.shopping.payment.repository.PaymentRepository;
import com.teemo.shopping.payment.service.PaymentService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teemo.shopping.payment.service.PaymentServiceDirector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderRefundService {

    /**
     * 입력된 모든 결제를 환불
     */
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrdersGamesRepository ordersGamesRepository;
    @Autowired
    private PaymentServiceDirector paymentServiceDirector;

    @Transactional
    public void refundPayments(Map<Payment, Integer> paymentRefundAmountMap) throws Exception {
        boolean isRefundable = true;
        String refundErrorMessage = "";
        for (var paymentRefundPriceEntry : paymentRefundAmountMap.entrySet()) {
            Payment payment = paymentRefundPriceEntry.getKey();
            Integer refundAmount = paymentRefundPriceEntry.getValue();
            if (!(payment.getState() == PaymentStates.SUCCESS
                || payment.getState() == PaymentStates.PARTIAL_REFUNDED)) {  // 환불 가능한 상태인지 확인
                isRefundable = false;
                refundErrorMessage = "이미 환불상태인 Payment가 있음.";
                break;
            }
            // 환불 금액이 환불 가능 금액 보다 클때
            if (refundAmount > payment.getRefundableAmount()) {
                isRefundable = false;
                refundErrorMessage = "환불할 금액이 환불 가능한 금액보다 큰 Payment가 있음.";
                break;
            }
        }
        if (isRefundable) {
            for (var paymentRefundPriceEntry : paymentRefundAmountMap.entrySet()) {
                Payment payment = paymentRefundPriceEntry.getKey();
                Integer refundAmount = paymentRefundPriceEntry.getValue();
                paymentServiceDirector.refund(payment, refundAmount);
            }
        } else {
            throw new IllegalStateException(refundErrorMessage);
        }

    }

    @Transactional
    //MUST EQUAL @Transaction on refundPayments(Map<Payment, Integer> paymentRefundAmountMap)   //improve: CTW, LTW
    public void refundPayments(List<Payment> payments) throws Exception {
        Map<Payment, Integer> paymentRefundPriceMap = new HashMap<>();
        payments.stream().forEach((payment) -> {
            paymentRefundPriceMap.put(payment, payment.getRefundableAmount());
        });
        refundPayments(paymentRefundPriceMap);
    }

    @Transactional
    //MUST EQUAL @Transaction on refundPayments(Map<Payment, Integer> paymentRefundAmountMap)
    public void refundPayment(Payment payment) throws Exception {
        refundPayments(List.of(payment));
    }

    @Transactional
    //MUST EQUAL @Transaction on refundPayments(Map<Payment, Integer> paymentRefundAmountMap)
    public void refundPayment(Payment payment, Integer refundPrice) throws Exception {
        refundPayments(Map.of(payment, refundPrice));
    }

    /**
     * 주문을 환불하고 게정과 게임 관계 삭제
     */
    @Transactional
    public void refundOrder(Long orderId) throws Exception {
        Order order = orderRepository.findById(orderId).get();
        List<Payment> payments = order.getPayments();

        order.updateState(OrderStates.PENDING_REFUND);
        List<OrdersGames> ordersGames = ordersGamesRepository.findAllByOrder(order);
        ordersGames.forEach((ordersGamesEntry) -> {
            if (ordersGamesEntry.getState().equals(OrdersGamesStates.PURCHASE)) {
                ordersGamesEntry.updateState(OrdersGamesStates.PENDING_REFUND);
            }
        });

        try {
            refundPayments(payments);
        } catch (Exception e) {
            throw new IllegalStateException("환불 불가능");
        }
    }

    @Transactional
    //MUST EQUAL @Transaction on refundPayments(Map<Payment, Integer> paymentRefundAmountMap)
    public void refundGame(Long orderId, Long gameId) {
        Order order = orderRepository.findById(orderId).get();
        Game game = gameRepository.findById(gameId).get();

        order.updateState(OrderStates.PENDING_REFUND);
        OrdersGames ordersGames = ordersGamesRepository.findByOrderAndGame(order, game).get();
        ordersGames.updateState(OrdersGamesStates.PENDING_REFUND);

        List<Payment> payments = order.getPayments();
        List<PaymentMethods> paymentMethodsSequence = new ArrayList<>();
        paymentMethodsSequence.add(PaymentMethods.DISCOUNT);
        paymentMethodsSequence.add(PaymentMethods.COUPON);
        paymentMethodsSequence.add(PaymentMethods.KAKAOPAY);
        paymentMethodsSequence.add(PaymentMethods.POINT);

        Map<Payment, Integer> paymentRefundAmountMap = new HashMap<>();
        int refundAmount = ordersGames.getPrice();
        for (var paymentMethod : paymentMethodsSequence) {
            for (var payment : payments) {
                if (paymentMethod.getPaymentClass().equals(payment.getClass())) {
                    int nowRefundAmount = Math.min(refundAmount, payment.getRefundableAmount());
                    paymentRefundAmountMap.put(payment, nowRefundAmount);
                    refundAmount -= nowRefundAmount;
                    if (refundAmount == 0) {
                        break;
                    }
                }
            }
        }
        if (refundAmount != 0) {
            throw new IllegalStateException("환불 가능한 금액이 없음");
        }
        try {
            refundPayments(paymentRefundAmountMap);
        } catch (Exception e) {
            throw new IllegalStateException("환불 오류");
        }

    }
}
