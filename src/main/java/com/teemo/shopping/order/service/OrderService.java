package com.teemo.shopping.order.service;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.dto.OrderDTO;
import com.teemo.shopping.order.dto.payment.PaymentDTO;
import com.teemo.shopping.order.enums.PaymentStates;
import com.teemo.shopping.order.repository.OrderRepository;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.order.service.payment.PaymentService;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class OrderService {

    @Autowired
    private List<PaymentService> paymentServices;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository<Payment> paymentRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public void pay(List<Long> paymentIds) {
        List<Payment> payments = paymentRepository.findAllById(paymentIds);
        for (var paymentService : paymentServices) {
            for (var payment : payments) {
                if (paymentService.getPaymentClass().equals(payment.getClass())) {
                    paymentService.pay(payment.getId());
                }
            }
        }
    }

    /**
     * @param orderId
     * @return order 반환
     */
    @Transactional(readOnly = true)
    public OrderDTO get(Long orderId) {
        Order order;
        try {
            order = orderRepository.findById(orderId).get();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("orderId를 가지고있는 Order 없음");
        }
        return OrderDTO.from(order);
    }

    /**
     * @param accountId
     * @return account가 소유하는 orders 반환
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> list(Long accountId) {
        Account account;
        try {
            account = accountRepository.findById(accountId).get();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("accountId를 가지고있는 Account 없음");
        }
        return orderRepository.findAllByAccount(account).stream().map(order -> OrderDTO.from(order))
            .toList();
    }


    @Transactional(readOnly = true)
    public List<PaymentDTO> getPayments(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        return order.getPayments().stream().map(payment -> PaymentDTO.from(payment)).toList();
    }
}

