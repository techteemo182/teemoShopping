package com.teemo.shopping.account;

import com.teemo.shopping.core.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AttributeOverride(name = "id", column = @Column(name = "account_id"))
@Entity
public class Account extends BaseEntity {

    @Column
    private String username;    // 유저 이름

    @Column
    private String password;    // 암호화된 패스워드 암호화 방식 골라야함
}
