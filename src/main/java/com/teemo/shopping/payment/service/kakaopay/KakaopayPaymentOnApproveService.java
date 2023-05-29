package com.teemo.shopping.payment.service.kakaopay;

import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIApproveResponse;
import com.teemo.shopping.payment.domain.KakaopayPayment;
import com.teemo.shopping.payment.domain.enums.KakaopayAPIStates;
import com.teemo.shopping.payment.domain.enums.PaymentStates;
import com.teemo.shopping.payment.observer.PaymentStateUpdatePublisher;
import com.teemo.shopping.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KakaopayPaymentOnApproveService {
    @Autowired
    private PaymentRepository<KakaopayPayment> kakaopayPaymentRepository;
    @Autowired
    private PaymentStateUpdatePublisher paymentStateUpdatePublisher;

    @Transactional
    public void onApprove(Long paymentId, KakaopayAPIApproveResponse response) {
        KakaopayPayment kakaopayPayment = kakaopayPaymentRepository.findById(paymentId).get();
        kakaopayPayment.setState(PaymentStates.SUCCESS);
        kakaopayPayment.updateKakaopayAPIState(KakaopayAPIStates.APPROVAL);
        paymentStateUpdatePublisher.publish(kakaopayPayment.getPaymentStateUpdateSubscriberTypes(), kakaopayPayment.getId());
    }
}
