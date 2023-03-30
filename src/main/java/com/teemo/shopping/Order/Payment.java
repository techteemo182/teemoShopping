package com.teemo.shopping.Order;

import com.teemo.shopping.core.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Inheritance(strategy = InheritanceType.JOINED)
public class Payment extends BaseEntity {

    @Builder
    protected Payment(PaymentMethod method, double price, String info) {
        this.method = method;
        this.price = price;
        this.info = info;
    }

    /**
     *  결제 방법
     *  /@DiscriminatorColumn 이기도 함
     */
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Column
    /**
     *  가격
     *  + 면 고객에서 회사로의 지불
     *  - 면 회사에서 고객으로의 반환
     */
    private double price;


    /**
     *  결제 정보
     *  예를 들어 카카오페이 ID
     *  info 로 부족하면 상속 테이블 만들어서 해결
     */
    @Column
    private String info;

    /**
     *  결제 상태
     *  CANCEL : 취소
     *  PENDING : 기다리는 중
     *  SUCCESS : 성공
     */
    @Enumerated
    private PaymentStatus status;

}

