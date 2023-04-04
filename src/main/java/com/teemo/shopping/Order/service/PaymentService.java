package com.teemo.shopping.Order.service;

import com.teemo.shopping.Order.domain.KakaopayPayment;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.Order.dto.KakaopayApprovalParameter;
import com.teemo.shopping.Order.dto.KakaopayRedirectParameter;
import com.teemo.shopping.Order.dto.KakaopayRedirectParameter.KakaopayRedirectType;
import com.teemo.shopping.Order.repository.KakaopayPaymentRepository;
import com.teemo.shopping.external_api.kakao.KakaopayService;
import com.teemo.shopping.external_api.kakao.dto.KakaopayApproveRequest;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class PaymentService {
    @Value("${key.kakao.cid}")
    private String cid;
    @Autowired
    private KakaopayPaymentRepository kakaopayPaymentRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private KakaopayService kakaopayService;

    @Transactional(rollbackOn = RuntimeException.class)
    public void onKakaopayRedirectResponse(KakaopayRedirectParameter parameter) throws RuntimeException{
        KakaopayRedirectType type = parameter.getType();

        String partnerUserId = parameter.getPartnerUserId();
        String partnerOrderId = parameter.getPartnerOrderId();
        KakaopayPayment kakaopayPayment;
        try {
            kakaopayPayment = kakaopayPaymentRepository.findById(Long.parseLong(partnerOrderId)).get();  // throw NoSuchElementException
        } catch (Exception e) {
            throw new RuntimeException("잘못된 접근");
        }
        if(kakaopayPayment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("중복 접근");
        }
        if (parameter.getType() == KakaopayRedirectType.FAIL
            || parameter.getType() == KakaopayRedirectType.CANCEL) {
            kakaopayPayment.updateStatus(PaymentStatus.CANCEL);
        } else if (parameter.getType() == KakaopayRedirectType.SUCCESS) {
            try {
                var response = kakaopayService.approveKakaopay(
                        KakaopayApproveRequest.builder().pgToken(parameter.getPgToken())
                            .partnerOrderId(kakaopayPayment.getId().toString())
                            .partnerUserId(kakaopayPayment.getPartnerUserId())
                            .cid(cid)
                            .tid(kakaopayPayment.getTid()).build())
                    .block();
                kakaopayPayment.updateStatus(PaymentStatus.SUCCESS);
            } catch (Exception e) {
                kakaopayPayment.updateStatus(PaymentStatus.CANCEL);
            }
        }

        Long orderId = kakaopayPayment.getOrder().getId();
        orderService.updateOrder(orderId);
    }

}

