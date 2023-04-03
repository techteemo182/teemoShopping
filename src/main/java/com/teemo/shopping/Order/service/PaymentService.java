package com.teemo.shopping.Order.service;

import com.teemo.shopping.external_api.kakao.KakaopayService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Autowired
    private KakaopayService kakaopayService;
}
