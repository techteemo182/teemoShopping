package com.teemo.shopping.order.service.payment.kakaopay_service;

import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIApproveResponse;
import com.teemo.shopping.order.domain.KakaopayPayment;
import com.teemo.shopping.order.enums.KakaopayAPIStates;
import com.teemo.shopping.order.enums.PaymentStates;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.order.service.paymentSubscriber.PaymentStatePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KakaopayPaymentOnApproveService {
    @Autowired
    private PaymentRepository<KakaopayPayment> kakaopayPaymentRepository;
    @Autowired
    private PaymentStatePublisher paymentStatePublisher;
    @Transactional
    public void onApprove(Long paymentId, KakaopayAPIApproveResponse response) {
        KakaopayPayment kakaopayPayment = kakaopayPaymentRepository.findById(paymentId).get();
        kakaopayPayment.updateState(PaymentStates.SUCCESS);
        kakaopayPayment.updateKakaopayAPIState(KakaopayAPIStates.APPROVAL);
        paymentStatePublisher.publish(kakaopayPayment.getId());
    }
}
