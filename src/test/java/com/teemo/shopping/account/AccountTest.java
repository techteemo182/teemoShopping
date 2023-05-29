package com.teemo.shopping.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teemo.shopping.Main;
import com.teemo.shopping.payment.domain.enums.PaymentMethods;
import com.teemo.shopping.payment.dto.payment_request.PaymentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Main.class)
public class AccountTest {
    @Test
    public void json() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        CouponPaymentRequest couponPaymentRequest = new CouponPaymentRequest(PaymentMethods.COUPON);
        String str = mapper.writeValueAsString(couponPaymentRequest);
    }
}
class CouponPaymentRequest extends PaymentRequest {
    @JsonCreator
    public CouponPaymentRequest(PaymentMethods paymentMethods) {
        super(paymentMethods);
    }
}

// 클래스 자체가 정보를 가지고 있다.
// 따라서 Serializable, Deserializable 할때 String을 Inject 해야한다.
