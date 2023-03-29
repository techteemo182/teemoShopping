package com.teemo.shopping.core;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass   // Entity 화 방지
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity extends TimeRecordEntity {
    @Id
    @GeneratedValue
    Long id;
}
