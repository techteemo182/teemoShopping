package com.teemo.shopping.payment.service.kakaopay;

import com.teemo.shopping.external_api.kakao.dto.KakaopayApproveParameter;
import com.teemo.shopping.external_api.kakao.service.KakaopayService;
import com.teemo.shopping.payment.domain.KakaopayPayment;
import com.teemo.shopping.payment.domain.enums.KakaopayAPIStates;
import com.teemo.shopping.payment.domain.enums.KakaopayRedirectStates;
import com.teemo.shopping.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KakaopayPaymentOnRedirectService {
    @Autowired
    private PaymentRepository<KakaopayPayment> kakaopayPaymentRepository;
    @Autowired
    private KakaopayService kakaopayService;
    @Autowired
    private KakaopayPaymentErrorService kakaopayPaymentErrorService;
    @Autowired
    private KakaopayPaymentOnApproveService kakaopayPaymentOnApproveService;
    @Transactional
    public void onKakaopayRedirect(Long paymentId,
        String pgToken, String redirectSecret, KakaopayRedirectStates kakaopayRedirectStates)
        throws RuntimeException {
        KakaopayPayment kakaopayPayment;
        try {
            kakaopayPayment = kakaopayPaymentRepository.findById(paymentId).get();
        } catch (Exception e) {
            throw new IllegalStateException("존재하지 않는 paymentId");
        }
        if (!kakaopayPayment.getKakaopayAPIStates().equals(KakaopayAPIStates.REDIRECT_PENDING)) {
            throw new IllegalStateException("잘못된 상태");
        }
        if (!kakaopayPayment.getRedirectSecret().equals(redirectSecret)) {
            throw new IllegalStateException("보안 코드 틀림");
        }
        if (!kakaopayRedirectStates.equals(KakaopayRedirectStates.SUCCESS)) {
            kakaopayPaymentErrorService.onPayError(paymentId);
        } else {

            kakaopayPayment.updatePgToken(pgToken);
            kakaopayPayment.updateKakaopayAPIState(KakaopayAPIStates.APPROVAL_PENDING);
            kakaopayService.approveKakaopay(KakaopayApproveParameter.builder().pgToken(pgToken)
                .partnerUserId(kakaopayPayment.getPartnerUserId())
                .cid(kakaopayPayment.getCid())
                .partnerOrderId(kakaopayPayment.getId().toString())
                .tid(kakaopayPayment.getTid()).build()).doOnError((error) -> {
                kakaopayPaymentErrorService.onPayError(paymentId);
            }).doOnSuccess((response) -> {
                kakaopayPaymentOnApproveService.onApprove(paymentId, response);
            }).subscribe();
        }
    }
}
