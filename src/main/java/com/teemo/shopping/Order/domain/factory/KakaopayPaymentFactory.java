package com.teemo.shopping.Order.domain.factory;

import com.teemo.shopping.Order.domain.KakaopayPayment;
import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.Order.dto.AllGamePaymentFactoryContext;
import com.teemo.shopping.Order.repository.KakaopayPaymentRepository;
import com.teemo.shopping.external_api.kakao.KakaopayService;
import com.teemo.shopping.external_api.kakao.dto.KakaopayReadyRequest;
import com.teemo.shopping.game.dto.GameDTO;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(200)
public class KakaopayPaymentFactory implements AllGamePaymentFactory {    //전략 패턴

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
    public Optional<Payment> create(AllGamePaymentFactoryContext context) {
        int remainPrice = context.getRemainPrice();
        com.teemo.shopping.Order.domain.Order order = context.getOrder();
        KakaopayPayment kakaopayPayment = KakaopayPayment.builder().status(PaymentStatus.PENDING)
            .price(remainPrice)
            .cid(cid)
            .partnerUserId(partnerUserId)
            .order(order)
            .build();
        kakaopayPayment = kakaopayPaymentRepository.saveAndFlush(kakaopayPayment);

        KakaopayReadyRequest request = KakaopayReadyRequest
            .builder()
            .gameDTOs(context.getGames().stream().map(game -> GameDTO.from(game)).toList().iterator())
            .cid(cid)
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
}
