package com.teemo.shopping.account.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AttributeOverride(name = "id", column = @Column(name = "account_id"))
@Entity
@Table(
    name = "account"
)

public class Account extends BaseEntity {
    @Builder
    protected Account(String username, String password, int point) {
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
    private int point; // 포인트

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<AccountsCoupons> accountsCoupons = new ArrayList<>();
}
