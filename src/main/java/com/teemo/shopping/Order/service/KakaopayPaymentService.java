package com.teemo.shopping.Order.service;

import com.teemo.shopping.Order.domain.KakaopayPayment;
import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.Order.dto.AllGamePaymentServiceContext;
import com.teemo.shopping.Order.dto.KakaopayRedirectParameter;
import com.teemo.shopping.Order.dto.KakaopayRedirectParameter.KakaopayRedirectType;
import com.teemo.shopping.Order.dto.OrderUpdateObserverContext;
import com.teemo.shopping.Order.repository.KakaopayPaymentRepository;
import com.teemo.shopping.external_api.kakao.KakaopayService;
import com.teemo.shopping.external_api.kakao.dto.KakaopayApproveRequest;
import com.teemo.shopping.external_api.kakao.dto.KakaopayReadyRequest;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(200)
public class KakaopayPaymentService extends AllGamePaymentService {    //전략 패턴

    @Autowired
    private KakaopayPaymentRepository kakaopayPaymentRepository;
    @Autowired
    private KakaopayService kakaopayService;
    public PaymentMethod getTargetPaymentMethod() {
        return PaymentMethod.KAKAOPAY;
    }

    @Value("${key.kakao.cid}")
    private String cid;
    @Value("${key.kakao.partner-user-id}")
    private String partnerUserId;
    @Override
    @Transactional(rollbackOn = RuntimeException.class)
    public Optional<Payment> create(AllGamePaymentServiceContext context) {
        int remainPrice = context.getRemainPrice();
        com.teemo.shopping.Order.domain.Order order = context.getOrder();
        KakaopayPayment kakaopayPayment = KakaopayPayment.builder().status(PaymentStatus.PENDING)
            .price(remainPrice)
            .cid(cid)
            .partnerUserId(partnerUserId)
            .order(order)
            .build();
        kakaopayPayment = kakaopayPaymentRepository.saveAndFlush(kakaopayPayment);

        String itemName = String.join(",",context.getGames().stream().map(game -> game.getName()).toList());    //GameName 추출 후 "," 으로 Join
        KakaopayReadyRequest request = KakaopayReadyRequest
            .builder()
            .cid(cid)
            .itemName(itemName)
            .partnerUserId(partnerUserId)
            .partnerOrderId(kakaopayPayment.getId().toString())
            .price(remainPrice)
            .build();
        var response = kakaopayService.readyKakaopay(request)   // throws RuntimeException
            .block();
        kakaopayPayment.updateTid(response.getTid());
        kakaopayPaymentRepository.save(kakaopayPayment);

        var createOrderReturnBuilder = context.getCreateOrderReturnBuilder();
        createOrderReturnBuilder.nextRedirectAppUrl(response.getNextRedirectAppUrl());
        createOrderReturnBuilder.nextRedirectMobileUrl(response.getNextRedirectMobileUrl());
        createOrderReturnBuilder.nextRedirectPcUrl(response.getNextRedirectPcUrl());
        context.setRemainPrice(0);
        return Optional.of(kakaopayPayment);
    }

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
        notifyObservers(OrderUpdateObserverContext.builder().payment(kakaopayPayment).build());
    }
}

