package com.teemo.shopping.account.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

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
        this.isAdmin = false;
    }

    @Column
    @NotNull
    @Size(max = 30)
    private String username;    // 유저 이름

    @Column
    @NotNull
    private String password;    // 암호화된 패스워드 암호화 방식 골라야함

    @Column
    @NotNull
    @Range(min = 0)
    private int point; // 포인트

    @Column
    @NotNull
    private boolean isAdmin;

    public void updatePoint(int point) {
        this.point = point;
    }
    public void updateIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
