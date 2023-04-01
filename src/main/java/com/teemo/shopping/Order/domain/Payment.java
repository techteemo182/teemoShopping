package com.teemo.shopping.Order.domain;

import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.core.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AttributeOverride(name = "id", column = @Column(name = "payments_id"))
@Table(
    name = "payments"
)
@DiscriminatorColumn(name = "method", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("null")
@Inheritance(strategy = InheritanceType.JOINED)
public class Payment extends BaseEntity {


    protected Payment(int price, PaymentStatus status) {
        this.price = price;
        this.status = status;
    }

    @Column
    /**
     *  가격
     *  + 면 고객에서 회사로의 지불
     *  - 면 회사에서 고객으로의 반환
     */
    private int price;

    /**
     *  결제 상태
     *  CANCEL : 취소
     *  PENDING : 기다리는 중
     *  SUCCESS : 성공
     */
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
}

