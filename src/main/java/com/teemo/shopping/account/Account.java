package com.teemo.shopping.account;

import com.teemo.shopping.core.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AttributeOverride(name = "id", column = @Column(name = "accounts_id"))
@Entity
@Table(
    name = "accounts"
)

public class Account extends BaseEntity {
    @Builder
    protected Account(String username, String password, double point) {
        this.username = username;
        this.password = password;
        this.point = point;
    }

    @Column
    @NotNull
    private String username;    // 유저 이름

    @Column
    @NotNull
    private String password;    // 암호화된 패스워드 암호화 방식 골라야함

    @Column
    @NotNull
    private double point; // 포인트
}
